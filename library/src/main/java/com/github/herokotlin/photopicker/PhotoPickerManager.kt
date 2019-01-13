package com.github.herokotlin.photopicker

import android.content.Context
import android.content.pm.PackageManager
import android.os.*
import android.provider.MediaStore
import com.github.herokotlin.photopicker.model.AlbumAsset
import com.github.herokotlin.photopicker.model.PhotoAsset
import java.io.File
import java.util.*

object PhotoPickerManager {

    private const val PERMISSION_REQUEST_CODE = 12321

    var onPermissionsGranted: (() -> Unit)? = null

    var onPermissionsDenied: (() -> Unit)? = null

    var onFetchWithoutPermissions: (() -> Unit)? = null

    var onFetchWithoutExternalStorage: (() -> Unit)? = null

    private lateinit var onRequestPermissionsComplete: () -> Unit

    private lateinit var onScanComplete: () -> Unit

    private var allPhotos = mutableListOf<PhotoAsset>()

    private val allAlbums = HashMap<String, MutableList<PhotoAsset>>()

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

            val imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val contentProvider = context.contentResolver

            val cursor = contentProvider.query(
                imageUri,
                null,
                configuration.photoMimeTypes.map { "${MediaStore.Images.Media.MIME_TYPE}=?" }.joinToString(" or "),
                configuration.photoMimeTypes,
                configuration.photoSortField
            )

            allAlbums.clear()
            allPhotos.clear()

            while (cursor.moveToNext()) {

                val path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                val width = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.WIDTH))
                val height = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT))

                val photo = PhotoAsset.build(path, width, height)

                allPhotos.add(photo)

                val albumName = getAlbumName(path)
                if (!albumName.isEmpty()) {
                    if (!allAlbums.contains(albumName)) {
                        allAlbums[albumName] = mutableListOf()
                    }
                    allAlbums[albumName]?.add(photo)
                }

            }


            if (!configuration.photoSortAscending) {
                allPhotos.reverse()
                allAlbums.values.forEach {
                    it.reverse()
                }
            }

            cursor.close()

            // 回到主线程
            handler.sendEmptyMessage(0)

        }).start()

    }

    fun fetchAlbumList(configuration: PhotoPickerConfiguration): List<AlbumAsset> {

        val result = mutableListOf<AlbumAsset>()

        result.add(
            AlbumAsset.build(configuration.allPhotosAlbumTitle, allPhotos)
        )

        allAlbums.keys.forEach { title ->
            allAlbums[title]?.let {
                val list = it.toList()
                result.add(
                    AlbumAsset.build(title, list)
                )
            }
        }

        return result.filter { configuration.filterAlbum(it.title, it.photoList.count()) }

    }

    fun fetchPhotoList(album: String): List<PhotoAsset> {
        if (allAlbums.contains(album)) {
            return allAlbums[album]!!
        }
        return allPhotos
    }

    fun requestPermissions(configuration: PhotoPickerConfiguration, callback: () -> Unit) {

        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            onFetchWithoutExternalStorage?.invoke()
            return
        }

        onRequestPermissionsComplete = callback

        if (configuration.requestPermissions(
                listOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                PERMISSION_REQUEST_CODE
            )
        ) {
            callback()
        }

    }

    fun requestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (requestCode != PERMISSION_REQUEST_CODE) {
            return
        }

        for (i in 0 until permissions.size) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                onPermissionsDenied?.invoke()
                return
            }
        }

        onPermissionsGranted?.invoke()
        onRequestPermissionsComplete.invoke()

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