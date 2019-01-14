package com.github.herokotlin.photopicker.view

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.github.herokotlin.photopicker.PhotoPickerConfiguration

import com.github.herokotlin.photopicker.R
import com.github.herokotlin.photopicker.model.PhotoAsset
import kotlinx.android.synthetic.main.photo_picker_photo_grid.view.*

class PhotoGrid: FrameLayout {

    var onPhotoClick: ((PhotoAsset) -> Unit)? = null

    var onSelectedPhotoListChange: (() -> Unit)? = null

    var photoList = listOf<PhotoAsset>()

        set(value) {

            if (value == field) {
                return
            }

            field = value

            if (selectedPhotoList.count() > 0) {
                // 安卓和 ios 的实现机制不一样
                // 安卓会持续持有照片实例
                // 因此当来回切换时，照片的选中状态还在，这里要重置一下
                selectedPhotoList.forEach {
                    it.order = -1
                }
                selectedPhotoList.clear()
                onSelectedPhotoListChange?.invoke()
            }

            adapter?.notifyDataSetChanged()

        }

    var selectedPhotoList = mutableListOf<PhotoAsset>()

    private lateinit var configuration: PhotoPickerConfiguration

    private var adapter: PhotoGridAdapter? = null

    private var cellSize = 0

    private var cellPixelSize = 0

    private val paddingHorizontal: Int by lazy {
        resources.getDimensionPixelSize(R.dimen.photo_picker_photo_grid_padding_horizontal)
    }

    private val paddingVertical: Int by lazy {
        resources.getDimensionPixelSize(R.dimen.photo_picker_photo_grid_padding_vertical)
    }

    private val rowSpacing: Int by lazy {
        resources.getDimensionPixelSize(R.dimen.photo_picker_photo_grid_row_spacing)
    }

    private val columnSpacing: Int by lazy {
        resources.getDimensionPixelSize(R.dimen.photo_picker_photo_grid_column_spacing)
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

        val columnCount = configuration.photoGirdSpanCount
        val spacing = columnSpacing * (columnCount - 1) - paddingHorizontal * 2

        val cellPixelSize = Math.max((width - spacing) / columnCount, 0)

        if (cellPixelSize != this.cellPixelSize) {

            this.cellSize = (cellPixelSize / resources.displayMetrics.density).toInt()
            this.cellPixelSize = cellPixelSize

            if (cellPixelSize > 0) {
                adapter?.notifyDataSetChanged()
            }

        }

    }

    private fun toggleChecked(photo: PhotoAsset) {

        // checked 获取反选值
        val checked = photo.order < 0
        val selectedCount = selectedPhotoList.count()

        if (checked) {

            // 因为有动画，用户可能在动画过程中快速点击了新的照片
            // 这里应该忽略
            if (selectedCount == configuration.maxSelectCount) {
                return
            }

            photo.order = selectedCount
            selectedPhotoList.add(photo)
            onSelectedPhotoListChange?.invoke()

            // 到达最大值，就无法再选了
            if (selectedCount + 1 == configuration.maxSelectCount) {
                adapter?.notifyDataSetChanged()
            }
            else {
                adapter?.notifyItemChanged(photo.index)
            }

        }
        else {

            selectedPhotoList.removeAt(photo.order)
            onSelectedPhotoListChange?.invoke()

            photo.order = -1

            val changes = mutableListOf<Int>()

            changes.add(photo.index)

            // 重排顺序
            selectedPhotoList.forEachIndexed { index, photoAsset ->
                if (index != photoAsset.order) {
                    photoAsset.order = index
                    changes.add(photoAsset.index)
                }
            }

            // 上个状态是到达上限
            if (selectedCount == configuration.maxSelectCount) {
                adapter?.notifyDataSetChanged()
            }
            else {
                changes.forEach {
                    adapter?.notifyItemChanged(it)
                }
            }

        }

    }

    inner class PhotoGridAdapter : RecyclerView.Adapter<PhotoItem>() {

        override fun getItemCount(): Int {
            return photoList.count()
        }

        override fun onBindViewHolder(holder: PhotoItem, position: Int) {

            val photo = photoList[position]

            photo.index = position

            // 选中状态下可以反选
            if (photo.order >= 0) {
                photo.selectable = true
            }
            else {
                photo.selectable = selectedPhotoList.count() < configuration.maxSelectCount
            }

            holder.bind(photo, cellPixelSize)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoItem {
            val view = LayoutInflater.from(context).inflate(R.layout.photo_picker_photo_item, null)
            return PhotoItem(
                view,
                configuration,
                {
                    onPhotoClick?.invoke(it)
                },
                {
                    toggleChecked(it)
                }
            )
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