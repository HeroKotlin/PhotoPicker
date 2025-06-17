package com.github.herokotlin.photopicker.view

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.github.herokotlin.photopicker.PhotoPickerConfiguration
import com.github.herokotlin.photopicker.model.Album
import com.github.herokotlin.photopicker.R
import com.github.herokotlin.photopicker.databinding.PhotoPickerAlbumItemBinding

class AlbumItem(binding: PhotoPickerAlbumItemBinding, private val configuration: PhotoPickerConfiguration, private val onClick: ((Album) -> Unit)): RecyclerView.ViewHolder(binding.root) {

    private val separatorView = binding.separatorView

    private val posterView = binding.posterView

    private val titleView = binding.titleView

    private val countView = binding.countView

    private var index = -1

    private lateinit var album: Album

    init {
        binding.root.setOnClickListener {
            onClick.invoke(album)
        }
    }

    fun bind(index: Int, album: Album) {

        titleView.text = album.title
        countView.text = "${album.assetList.count()}"

        val poster = album.poster
        if (poster != null) {
            configuration.loadAsset(
                posterView,
                poster.path,
                R.drawable.photo_picker_album_poster_loading_placeholder,
                R.drawable.photo_picker_album_poster_error_placeholder
            ) {

            }
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