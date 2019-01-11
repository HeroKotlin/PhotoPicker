package com.github.herokotlin.photopicker.view

import android.support.v7.widget.RecyclerView
import android.view.View
import com.github.herokotlin.photopicker.PhotoPickerConfiguration
import com.github.herokotlin.photopicker.model.PhotoAsset
import kotlinx.android.synthetic.main.photo_picker_photo_item.view.*

class PhotoItem(view: View, private val configuration: PhotoPickerConfiguration): RecyclerView.ViewHolder(view) {

    private val thumbnailView = view.thumbnailView

    private var pixelSize = 0

        set(value) {

            if (field == value) {
                return
            }

            field = value

            thumbnailView.layoutParams.width = value
            thumbnailView.layoutParams.height = value

        }

    fun bind(photo: PhotoAsset, size: Int, pixelSize: Int) {

        this.pixelSize = pixelSize

        configuration.loadImage(itemView.thumbnailView, photo.path, size, size)

    }

}