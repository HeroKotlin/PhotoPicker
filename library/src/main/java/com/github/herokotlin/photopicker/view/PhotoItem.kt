package com.github.herokotlin.photopicker.view

import android.support.v7.widget.RecyclerView
import android.view.View
import com.github.herokotlin.photopicker.PhotoPickerConfiguration
import com.github.herokotlin.photopicker.model.PhotoAsset
import kotlinx.android.synthetic.main.photo_picker_photo_item.view.*

class PhotoItem(view: View, private val configuration: PhotoPickerConfiguration): RecyclerView.ViewHolder(view) {

    fun bind(photo: PhotoAsset) {



        configuration.loadImage(itemView.thumbnailView, photo.path, 50, 50)

    }

}