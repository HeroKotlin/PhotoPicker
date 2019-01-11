package com.github.herokotlin.photopicker.view

import android.support.v7.widget.RecyclerView
import android.view.View
import com.github.herokotlin.photopicker.model.AlbumAsset

class AlbumItem(view: View): RecyclerView.ViewHolder(view) {

    var index = -1

        set(value) {

            if (value == field) {
                return
            }

        }

    var album: AlbumAsset? = null

        set(value) {

            if (value == field) {
                return
            }


        }

}