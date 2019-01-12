package com.github.herokotlin.photopicker.view

import android.support.v7.widget.RecyclerView
import android.view.View
import com.github.herokotlin.photopicker.PhotoPickerConfiguration
import com.github.herokotlin.photopicker.model.AlbumAsset
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

    fun bind(index: Int, album: AlbumAsset, posterWidth: Int, posterHeight: Int) {

        titleView.text = album.title
        countView.text = "${album.photoList.count()}"

        configuration.loadImage(posterView, album.poster.path, posterWidth, posterHeight)

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