package com.github.herokotlin.photopicker.view

import android.support.v7.widget.RecyclerView
import android.view.View
import com.github.herokotlin.photopicker.PhotoPickerConfiguration
import com.github.herokotlin.photopicker.model.AlbumAsset
import com.github.herokotlin.photopicker.R
import kotlinx.android.synthetic.main.photo_picker_album_item.view.*

class AlbumItem(view: View, private val configuration: PhotoPickerConfiguration, private val onClick: ((AlbumAsset) -> Unit)): RecyclerView.ViewHolder(view) {

    private val separatorView = view.separatorView

    private val posterView = view.posterView

    private val titleView = view.titleView

    private val countView = view.countView

    private var index = -1

    private lateinit var album: AlbumAsset

    init {
        view.setOnClickListener {
            onClick.invoke(album)
        }
    }

    fun bind(index: Int, album: AlbumAsset) {

        titleView.text = album.title
        countView.text = "${album.photoList.count()}"

        val poster = album.poster
        if (poster != null) {
            configuration.loadPhoto(posterView, poster.path, R.drawable.photo_picker_album_poster_loading_placeholder, R.drawable.photo_picker_album_poster_error_placeholder)
        }
        else {
            posterView.setImageResource(R.drawable.photo_picker_album_empty_placeholder)
        }

        if (index == 0) {
            if (this.index > 0) {
                separatorView.visibility = View.GONE
            }
        }
        else {
            if (this.index <= 0) {
                separatorView.visibility = View.VISIBLE
            }
        }

        this.index = index
        this.album = album

    }

}