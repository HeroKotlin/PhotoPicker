package com.github.herokotlin.photopicker.view

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import com.github.herokotlin.photopicker.PhotoPickerConfiguration

import com.github.herokotlin.photopicker.R
import com.github.herokotlin.photopicker.model.PhotoAsset
import kotlinx.android.synthetic.main.photo_picker_photo_grid.view.*

class PhotoGrid: FrameLayout {

    var photoList = listOf<PhotoAsset>()

        set(value) {

            if (value == field) {
                return
            }

            field = value

            adapter.notifyDataSetChanged()

        }

    private lateinit var configuration: PhotoPickerConfiguration

    private lateinit var adapter: PhotoGridAdapter

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
        LayoutInflater.from(context).inflate(R.layout.photo_picker_photo_grid, this)
    }

    fun init(configuration: PhotoPickerConfiguration) {

        this.configuration = configuration

        adapter = PhotoGridAdapter()

        gridView.layoutManager = GridLayoutManager(context, configuration.photoGirdSpanCount)

        gridView.adapter = adapter

    }

    inner class PhotoGridAdapter : RecyclerView.Adapter<PhotoItem>() {

        override fun getItemCount(): Int {
            return photoList.count()
        }

        override fun onBindViewHolder(holder: PhotoItem, position: Int) {
            val photo = photoList[position]
            holder.bind(photo)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoItem {
            val view = LayoutInflater.from(context).inflate(R.layout.photo_picker_photo_item, null)
            return PhotoItem(view, configuration)
        }

    }

}