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
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            onScanComplete.invoke()
        }
    }

    private var scanTask: Thread? = null

    fun scan(context: Context, configuration: PhotoPickerConfiguration, callback: () -> Unit) {

        onScanComplete = callback

        Thread({

            // 降低线程优先级
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)

            // 存储当前线程，方便停止
            scanTask = Thread.currentThread()

            val contentProvider = context.contentResolver

            val query = getQuery(
                configuration.assetMinSize,
                configuration.assetMaxSize,
                configuration.includeAssetMediaTypes,
                configuration.excludeAssetMediaTypes
            )

            val cursor = contentProvider.query(
                MediaStore.Files.getContentUri("external"),
                PhotoPickerConstant.FILE_FIELDS,
                query.where,
                query.args.toTypedArray(),
                configuration.assetSortField + " " + if (configuration.assetSortAscending) "ASC" else "DESC"
            )

            cursor?.let {

                allAlbums.clear()
                allPhotos.clear()

                while (it.moveToNext()) {

                    var path = ""
                    var width = 0
                    var height = 0
                    var size = 0
                    var mimeType = ""

                    val pathIndex = it.getColumnIndex(PhotoPickerConstant.FIELD_PATH)
                    val widthIndex = it.getColumnIndex(PhotoPickerConstant.FIELD_WIDTH)
                    val heightIndex = it.getColumnIndex(PhotoPickerConstant.FIELD_HEIGHT)
                    val sizeIndex = it.getColumnIndex(PhotoPickerConstant.FIELD_SIZE)
                    val mimeTypeIndex = it.getColumnIndex(PhotoPickerConstant.FIELD_MIME_TYPE)

                    if (pathIndex >= 0 ) {
                        path = it.getString(pathIndex)
                    }
                    if (widthIndex >= 0 ) {
                        width = it.getInt(widthIndex)
                    }
                    if (heightIndex >= 0 ) {
                        height = it.getInt(heightIndex)
                    }
                    if (sizeIndex >= 0 ) {
                        size = it.getInt(sizeIndex)
                    }
                    if (mimeTypeIndex >= 0 ) {
                        mimeType = it.getString(mimeTypeIndex)
                    }
                    val photo = Asset.build(path, width, height, size, mimeType)
                    if (photo == null || !configuration.filter(photo)) {
                        continue
                    }

                    allPhotos.add(photo)

                    val albumName = getAlbumName(photo.path)
                    if (albumName.isNotEmpty()) {
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

    private fun getQuery(minSize: Int, maxSize: Int, includeMediaTypes: List<Int>, excludeMediaTypes: List<Int>): Query {

        val args = mutableListOf<String>()
        val where = mutableListOf<String>()

        val sizeList = mutableListOf<String>()
        if (minSize > 0) {
            args.add(minSize.toString())
            sizeList.add(
                "${PhotoPickerConstant.FIELD_SIZE} >= ?"
            )
        }
        if (maxSize > 0) {
            args.add(maxSize.toString())
            sizeList.add(
                "${PhotoPickerConstant.FIELD_SIZE} <= ?"
            )
        }
        if (sizeList.isNotEmpty()) {
            where.add(
                sizeList.joinToString(" AND ")
            )
        }

        if (includeMediaTypes.isNotEmpty()) {
            val item = includeMediaTypes.map {
                args.add(it.toString())
                "${PhotoPickerConstant.FIELD_MEDIA_TYPE} = ?"
            }
            where.add(item.joinToString(" OR "))
        }
        else if (excludeMediaTypes.isNotEmpty()) {
            val item = excludeMediaTypes.map {
                args.add(it.toString())
                "${PhotoPickerConstant.FIELD_MEDIA_TYPE} != ?"
            }
            where.add(item.joinToString(" AND "))
        }

        var whereStr = ""

        val count = where.count()
        if (count > 0) {
            whereStr = if (count > 1) {
                where.joinToString(" AND ") { "($it)" }
            } else {
                where[0]
            }
        }

        return Query(whereStr, args)

    }

    private class Query(val where: String, val args: List<String>)

}