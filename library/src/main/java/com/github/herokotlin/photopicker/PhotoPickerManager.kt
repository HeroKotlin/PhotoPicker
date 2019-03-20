package com.github.herokotlin.photopicker

import android.content.Context
import android.os.*
import android.provider.MediaStore
import com.github.herokotlin.photopicker.model.Album
import com.github.herokotlin.photopicker.model.Asset
import java.io.File
import java.util.*

object PhotoPickerManager {

    private lateinit var onScanComplete: () -> Unit

    private var allPhotos = mutableListOf<Asset>()

    private val allAlbums = HashMap<String, MutableList<Asset>>()

    private val handler = object: Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            onScanComplete.invoke()
        }
    }

    private var scanTask: Thread? = null

    fun scan(context: Context, configuration: PhotoPickerConfiguration, callback: () -> Unit) {

        onScanComplete = callback

        Thread(Runnable {

            // 降低线程优先级
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)

            // 存储当前线程，方便停止
            scanTask = Thread.currentThread()

            val contentProvider = context.contentResolver

            val cursor = contentProvider.query(
                MediaStore.Files.getContentUri("external"),
                PhotoPickerConstant.FILE_FIELDS,
                getSelection(
                    configuration.assetMinSize,
                    configuration.assetMaxSize,
                    configuration.includeAssetMediaTypes,
                    configuration.excludeAssetMediaTypes
                ),
                null,
                configuration.assetSortField + " " + if (configuration.assetSortAscending) "ASC" else "DESC"
            )

            cursor?.let {

                allAlbums.clear()
                allPhotos.clear()

                while (it.moveToNext()) {

                    val photo = Asset.build(
                        it.getString(it.getColumnIndex(PhotoPickerConstant.FIELD_PATH)),
                        it.getInt(it.getColumnIndex(PhotoPickerConstant.FIELD_WIDTH)),
                        it.getInt(it.getColumnIndex(PhotoPickerConstant.FIELD_HEIGHT)),
                        it.getInt(it.getColumnIndex(PhotoPickerConstant.FIELD_SIZE)),
                        it.getString(it.getColumnIndex(PhotoPickerConstant.FIELD_MIME_TYPE))
                    )

                    if (photo == null || !configuration.filter(photo)) {
                        continue
                    }

                    allPhotos.add(photo)

                    val albumName = getAlbumName(photo.path)
                    if (!albumName.isEmpty()) {
                        if (!allAlbums.contains(albumName)) {
                            allAlbums[albumName] = mutableListOf()
                        }
                        allAlbums[albumName]?.add(photo)
                    }

                }

                it.close()

            }

            // 回到主线程
            handler.sendEmptyMessage(0)

        }).start()

    }

    fun fetchAlbumList(configuration: PhotoPickerConfiguration): List<Album> {

        val result = mutableListOf<Album>()

        result.add(
            Album.build(configuration.allPhotosAlbumTitle, allPhotos)
        )

        allAlbums.keys.forEach { title ->
            allAlbums[title]?.let {
                val list = it.toList()
                result.add(
                    Album.build(title, list)
                )
            }
        }

        return result.filter { configuration.filter(it) }

    }

    fun fetchPhotoList(album: String): List<Asset> {
        if (allAlbums.contains(album)) {
            return allAlbums[album]!!
        }
        return allPhotos
    }

    private fun getAlbumName(path: String): String {
        val parts = path.split(File.separator)
        val count = parts.count()
        if (count >= 2) {
            return parts[count - 2]
        }
        return ""
    }

    private fun getSelection(minSize: Int, maxSize: Int, includeMediaTypes: List<Int>, excludeMediaTypes: List<Int>): String? {

        val list = mutableListOf<String>()

        val sizeList = mutableListOf<String>()
        if (minSize > 0) {
            sizeList.add(
                "${PhotoPickerConstant.FIELD_SIZE} >= $minSize"
            )
        }
        if (maxSize > 0) {
            sizeList.add(
                "${PhotoPickerConstant.FIELD_SIZE} <= $maxSize"
            )
        }
        if (sizeList.count() > 0) {
            list.add(
                sizeList.joinToString(" AND ")
            )
        }

        if (includeMediaTypes.count() > 0) {
            val item = includeMediaTypes.map {"${PhotoPickerConstant.FIELD_MEDIA_TYPE} = $it" }
            list.add(item.joinToString(" OR "))
        }
        else if (excludeMediaTypes.count() > 0) {
            val item = excludeMediaTypes.map {"${PhotoPickerConstant.FIELD_MEDIA_TYPE} != $it" }
            list.add(item.joinToString(" AND "))
        }

        if (list.count() > 0) {
            return list.joinToString(" AND ")
        }

        return null

    }

}