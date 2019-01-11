package com.github.herokotlin.photopicker.view

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
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

    private var cellSize = 0

    private var cellPixelSize = 0

    private val paddingHorizontal: Int by lazy {
        resources.getDimensionPixelSize(R.dimen.photo_grid_padding_horizontal)
    }

    private val paddingVertical: Int by lazy {
        resources.getDimensionPixelSize(R.dimen.photo_grid_padding_vertical)
    }

    private val rowSpacing: Int by lazy {
        resources.getDimensionPixelSize(R.dimen.photo_grid_row_spacing)
    }

    private val columnSpacing: Int by lazy {
        resources.getDimensionPixelSize(R.dimen.photo_grid_column_spacing)
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
        LayoutInflater.from(context).inflate(R.layout.photo_picker_photo_grid, this)
    }

    fun init(configuration: PhotoPickerConfiguration) {

        this.configuration = configuration

        updateCellSize()

        adapter = PhotoGridAdapter()

        gridView.layoutManager = GridLayoutManager(context, configuration.photoGirdSpanCount)

        gridView.adapter = adapter

        gridView.addItemDecoration(PhotoGridDecoration())

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateCellSize()
    }

    private fun updateCellSize() {

        // GridLayoutManager 会水平均分
        // paddingHorizontal、paddingVertical、rowSpacing、columnSpacing 占用的是单元格的空间

        val columnCount = configuration.photoGirdSpanCount
        val spacing = columnSpacing * (columnCount - 1)


        val cellPixelSize = Math.max((measuredWidth - spacing) / columnCount, 0)

        if (cellPixelSize != this.cellPixelSize) {

            this.cellSize = (cellPixelSize / resources.displayMetrics.density).toInt()
            this.cellPixelSize = cellPixelSize

            if (cellPixelSize > 0 && adapter != null) {
                adapter.notifyDataSetChanged()
            }

        }

    }

    inner class PhotoGridAdapter : RecyclerView.Adapter<PhotoItem>() {

        override fun getItemCount(): Int {
            return photoList.count()
        }

        override fun onBindViewHolder(holder: PhotoItem, position: Int) {
            val photo = photoList[position]
            holder.bind(photo, cellSize, cellPixelSize)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoItem {
            val view = LayoutInflater.from(context).inflate(R.layout.photo_picker_photo_item, null)
            return PhotoItem(view, configuration)
        }

    }

    inner class PhotoGridDecoration : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(rect: Rect, view: View,
                                    parent: RecyclerView, state: RecyclerView.State?) {

            val index = parent.getChildAdapterPosition(view)
            val columnCount = configuration.photoGirdSpanCount
            val rowCount = photoList.count() / columnCount
            val rowIndex = index / columnCount
            val columnIndex = index % columnCount

            if (rowIndex == 0) {
                rect.top = paddingVertical
            }
            else {
                rect.top = rowSpacing
                if (rowIndex == rowCount) {
                    rect.bottom = paddingVertical
                }
            }

            if (columnIndex == 0) {
                rect.left = paddingHorizontal
            }
            else {
                rect.left = columnSpacing
                if (columnIndex == columnCount - 1) {
                    rect.right = paddingHorizontal
                }
            }

        }
    }

}