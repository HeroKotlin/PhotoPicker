package com.github.herokotlin.photopicker.view

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout

import com.github.herokotlin.photopicker.model.AlbumAsset
import com.github.herokotlin.photopicker.R
import kotlinx.android.synthetic.main.photo_picker_album_list.view.*

class AlbumList : LinearLayout {

    var albumList = listOf<AlbumAsset>()

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

        recyclerView.adapter = AlbumListAdapter()

    }

    inner class AlbumListAdapter: RecyclerView.Adapter<AlbumItem>() {

        override fun getItemCount(): Int = albumList.size

        override fun onBindViewHolder(holder: AlbumItem, position: Int) {
            holder.index = position
            holder.album = albumList[position]
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumItem {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.photo_picker_album_item, parent, false)
            return AlbumItem(view)
        }

    }

}