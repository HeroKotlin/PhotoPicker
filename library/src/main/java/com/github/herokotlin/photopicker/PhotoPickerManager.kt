package com.github.herokotlin.photopicker

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.Process
import android.provider.MediaStore
import com.github.herokotlin.photopicker.model.AlbumAsset
import com.github.herokotlin.photopicker.model.PhotoAsset
import java.io.File

object PhotoPickerManager {

    var onScanComplete: (() -> Unit)? = null

    private var allPhotos = mutableListOf<PhotoAsset>()

    private val allAlbums = HashMap<String, MutableList<PhotoAsset>>()

    private val handler = object: Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            onScanComplete?.invoke()
        }
    }

    private var scanTask: Thread? = null

    fun scan(context: Context) {

        Thread(Runnable {

            // 降低线程优先级
//            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)
//
//            // 存储当前线程，方便停止
//            scanTask = Thread.currentThread()

            val imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val contentProvider = context.contentResolver

            val cursor = contentProvider.query(
                imageUri,
                arrayOf(
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.DATE_ADDED,
                    MediaStore.Images.Media._ID
                ),
                null,
                null,
                MediaStore.Images.Media.DATE_ADDED
            )

            allAlbums.clear()
            allPhotos.clear()

            while (cursor.moveToNext()) {

                val path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                val name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME))
                val time = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED))

                val photo = PhotoAsset(path, name, time)

                allPhotos.add(photo)

                val albumName = getAlbumName(path)
                if (!albumName.isEmpty()) {
                    if (!allAlbums.contains(albumName)) {
                        allAlbums[albumName] = mutableListOf()
                    }
                    val photoList = allAlbums[albumName]
                    photoList?.add(photo)
                }

            }

            cursor.close()

            // 回到主线程
            handler.sendEmptyMessage(0)

        }).start()

    }

    fun fetchAlbumList(): List<AlbumAsset> {

        val result = mutableListOf<AlbumAsset>()

        result.add(AlbumAsset("所有照片", allPhotos[0], allPhotos))

        allAlbums.keys.forEach { title ->
            allAlbums[title]?.let {
                val list = it.toList()
                result.add(
                    AlbumAsset(title, list[0], list)
                )
            }
        }

        return result

    }

    private fun getAlbumName(path: String): String {
        val parts = path.split(File.separator)
        val count = parts.count()
        if (count >= 2) {
            return parts[count - 2]
        }
        return ""
    }

}