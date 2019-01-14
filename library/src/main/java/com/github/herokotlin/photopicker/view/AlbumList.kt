package com.github.herokotlin.photopicker.view

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import com.github.herokotlin.photopicker.PhotoPickerConfiguration

import com.github.herokotlin.photopicker.R
import com.github.herokotlin.photopicker.model.AlbumAsset
import kotlinx.android.synthetic.main.photo_picker_album_list.view.*

class AlbumList : FrameLayout {

    var onAlbumClick: ((AlbumAsset) -> Unit)? = null

    var albumList = listOf<AlbumAsset>()

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

        LayoutInflater.from(context).inflate(R.layout.photo_picker_album_list, this)

        recyclerView.layoutManager = LinearLayoutManager(this.context)

    }

    fun init(configuration: PhotoPickerConfiguration) {

        this.configuration = configuration

        adapter = AlbumListAdapter()

        recyclerView.adapter = adapter

    }

    inner class AlbumListAdapter: RecyclerView.Adapter<AlbumItem>() {

        override fun getItemCount(): Int {
            return albumList.size
        }

        override fun onBindViewHolder(holder: AlbumItem, position: Int) {
            holder.bind(position, albumList[position])
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumItem {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.photo_picker_album_item, parent, false)
            return AlbumItem(view, configuration) {
                onAlbumClick?.invoke(it)
            }
        }

    }

}