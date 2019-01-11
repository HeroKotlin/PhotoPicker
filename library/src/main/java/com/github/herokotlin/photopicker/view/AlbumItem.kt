package com.github.herokotlin.photopicker.view

import android.support.v7.widget.RecyclerView
import android.view.View
import com.github.herokotlin.photopicker.PhotoPickerConfiguration
import com.github.herokotlin.photopicker.model.AlbumAsset
import kotlinx.android.synthetic.main.photo_picker_album_item.view.*

class AlbumItem(view: View, private val configuration: PhotoPickerConfiguration): RecyclerView.ViewHolder(view) {

    var index = -1

    fun bind(index: Int, album: AlbumAsset, posterWidth: Int, posterHeight: Int) {

        if (index == 0) {
            if (this.index > 0) {
                itemView.separatorView.visibility = View.GONE
            }
        }
        else {
            if (this.index <= 0) {
                itemView.separatorView.visibility = View.VISIBLE
            }
        }
        this.index = index

        itemView.titleView.text = album.title
        itemView.countView.text = "${album.photoList.count()}"

        configuration.loadImage(itemView.posterView, album.poster.path, posterWidth, posterHeight)

    }

}