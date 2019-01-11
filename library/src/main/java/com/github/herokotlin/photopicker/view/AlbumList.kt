package com.github.herokotlin.photopicker.view

import android.content.Context
import android.graphics.Rect
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.github.herokotlin.photopicker.PhotoPickerConfiguration

import com.github.herokotlin.photopicker.R
import com.github.herokotlin.photopicker.model.AlbumAsset
import kotlinx.android.synthetic.main.photo_picker_album_list.view.*

class AlbumList : LinearLayout {

    var albumList = listOf<AlbumAsset>()

        set(value) {

            if (value == field) {
                return
            }

            field = value

            adapter.notifyDataSetChanged()
        }

    lateinit var configuration: PhotoPickerConfiguration

    private val adapter = AlbumListAdapter()

    private val posterWidth: Int by lazy {
        resources.getDimension(R.dimen.album_poster_width).toInt()
    }

    private val posterHeight: Int by lazy {
        resources.getDimension(R.dimen.album_poster_height).toInt()
    }

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

        recyclerView.adapter = adapter

    }

    inner class AlbumListAdapter: RecyclerView.Adapter<AlbumItem>() {

        override fun getItemCount(): Int {
            return albumList.size
        }

        override fun onBindViewHolder(holder: AlbumItem, position: Int) {
            holder.bind(position, albumList[position], posterWidth, posterHeight)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumItem {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.photo_picker_album_item, parent, false)
            return AlbumItem(view, configuration)
        }

    }

}