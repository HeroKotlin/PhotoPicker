package com.github.herokotlin.photopicker.view

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import com.github.herokotlin.photopicker.PhotoPickerConfiguration

import com.github.herokotlin.photopicker.databinding.PhotoPickerAlbumItemBinding
import com.github.herokotlin.photopicker.databinding.PhotoPickerAlbumListBinding
import com.github.herokotlin.photopicker.model.Album

class AlbumList : FrameLayout {

    lateinit var binding: PhotoPickerAlbumListBinding

    var onAlbumClick: ((Album) -> Unit)? = null

    var albumList = listOf<Album>()

        set(value) {

            if (value == field) {
                return
            }

            field = value

            adapter.notifyDataSetChanged()

        }

    private lateinit var configuration: PhotoPickerConfiguration

    private lateinit var adapter: AlbumListAdapter

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    private fun init() {

        binding = PhotoPickerAlbumListBinding.inflate(LayoutInflater.from(context), this, true)

        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)

    }

    fun init(configuration: PhotoPickerConfiguration) {

        this.configuration = configuration

        adapter = AlbumListAdapter()

        binding.recyclerView.adapter = adapter

    }

    inner class AlbumListAdapter: RecyclerView.Adapter<AlbumItem>() {

        override fun getItemCount(): Int {
            return albumList.size
        }

        override fun onBindViewHolder(holder: AlbumItem, position: Int) {
            holder.bind(position, albumList[position])
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumItem {
            val binding = PhotoPickerAlbumItemBinding.inflate(LayoutInflater.from(context), parent, false)
            return AlbumItem(binding, configuration) {
                onAlbumClick?.invoke(it)
            }
        }

    }

}