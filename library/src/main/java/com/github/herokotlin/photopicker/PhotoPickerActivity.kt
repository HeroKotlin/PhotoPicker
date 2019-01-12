package com.github.herokotlin.photopicker

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.github.herokotlin.photopicker.model.AlbumAsset
import com.github.herokotlin.photopicker.model.PhotoAsset
import kotlinx.android.synthetic.main.photo_picker_activity.*
import kotlinx.android.synthetic.main.photo_picker_top_bar.view.*

class PhotoPickerActivity: AppCompatActivity() {

    companion object {

        lateinit var configuration: PhotoPickerConfiguration

        fun newInstance(context: Context) {
            val intent = Intent(context, PhotoPickerActivity::class.java)
            context.startActivity(intent)
        }

    }

    // 当前选中的相册
    var currentAlbum: AlbumAsset? = null

        set(value) {

            if (field === value) {
                return
            }

            field = value

            val title: String
            val photoList: List<PhotoAsset>

            if (value != null) {
                title = value.title
                photoList = PhotoPickerManager.fetchPhotoList(value.title)
            }
            else {
                title = ""
                photoList = listOf()
            }

            topBar.titleButton.title = title
            photoGridView.photoList = photoList

        }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        var flags = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            flags = flags or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }

        window.decorView.systemUiVisibility = flags

        supportActionBar?.hide()

        setContentView(R.layout.photo_picker_activity)


        photoGridView.init(configuration)
        photoGridView.onSelectedPhotoListChange = {
            bottomBar.selectedCount = photoGridView.selectedPhotoList.count()
        }

        albumListView.init(configuration)
        albumListView.onAlbumClick = {
            currentAlbum = it
            toggleAlbumList()
        }

        PhotoPickerManager.onScanComplete = {
            val albumList = PhotoPickerManager.fetchAlbumList(configuration)
            albumListView.albumList = albumList
            currentAlbum = if (albumList.count() > 0) albumList[0] else null
        }
        PhotoPickerManager.scan(this, configuration)

        topBar.cancelButton.setOnClickListener {
            finish()
        }

        topBar.titleButton.setOnClickListener {
            toggleAlbumList()
        }

    }

    private fun toggleAlbumList() {

        val checked = !topBar.titleButton.checked

        if (checked) {
            albumListView.visibility = View.VISIBLE
        }
        else {
            albumListView.visibility = View.GONE
        }

        topBar.titleButton.checked = checked

    }

}