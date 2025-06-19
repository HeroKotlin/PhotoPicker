package com.github.herokotlin.photopicker.view

import android.content.Context
import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.GridLayoutManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.github.herokotlin.photopicker.PhotoPickerConfiguration
import com.github.herokotlin.photopicker.R
import com.github.herokotlin.photopicker.databinding.PhotoPickerAssetGridBinding
import com.github.herokotlin.photopicker.databinding.PhotoPickerAssetItemBinding
import com.github.herokotlin.photopicker.model.Asset
import androidx.core.view.isVisible

class AssetGrid: FrameLayout {

    var onAssetClick: ((Asset) -> Unit)? = null

    var onSelectedAssetListChange: (() -> Unit)? = null

    var assetList = listOf<Asset>()

        set(value) {

            if (value == field) {
                return
            }

            if (value.isNotEmpty() && binding.spinnerView.isVisible) {
                binding.spinnerView.visibility = View.GONE
                binding.gridView.visibility = View.VISIBLE
            }

            field = value

            if (selectedAssetList.isNotEmpty()) {
                // 安卓和 ios 的实现机制不一样
                // 安卓会持续持有照片实例
                // 因此当来回切换时，照片的选中状态还在，这里要重置一下
                selectedAssetList.forEach {
                    it.order = -1
                }
                selectedAssetList.clear()
                onSelectedAssetListChange?.invoke()
            }

            adapter.notifyDataSetChanged()

        }

    var selectedAssetList = mutableListOf<Asset>()
    lateinit var binding: PhotoPickerAssetGridBinding

    private lateinit var configuration: PhotoPickerConfiguration

    private lateinit var adapter: PhotoGridAdapter

    private var cellSize = 0

    private var cellPixelSize = 0

    private val paddingHorizontal: Int by lazy {
        resources.getDimensionPixelSize(R.dimen.photo_picker_asset_grid_padding_horizontal)
    }

    private val paddingVertical: Int by lazy {
        resources.getDimensionPixelSize(R.dimen.photo_picker_asset_grid_padding_vertical)
    }

    private val rowSpacing: Int by lazy {
        resources.getDimensionPixelSize(R.dimen.photo_picker_asset_grid_row_spacing)
    }

    private val columnSpacing: Int by lazy {
        resources.getDimensionPixelSize(R.dimen.photo_picker_asset_grid_column_spacing)
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
        binding = PhotoPickerAssetGridBinding.inflate(LayoutInflater.from(context), this, true)
    }


    fun init(configuration: PhotoPickerConfiguration) {

        this.configuration = configuration

        updateCellSize()

        adapter = PhotoGridAdapter()

        binding.gridView.layoutManager = GridLayoutManager(context, configuration.assetGirdSpanCount)

        binding.gridView.adapter = adapter

        binding.gridView.addItemDecoration(PhotoGridDecoration())

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateCellSize()
    }

    private fun updateCellSize() {

        val columnCount = configuration.assetGirdSpanCount
        val spacing = columnSpacing * (columnCount - 1) - paddingHorizontal * 2

        val cellPixelSize = Math.max((width - spacing) / columnCount, 0)

        if (cellPixelSize != this.cellPixelSize) {

            this.cellSize = (cellPixelSize / resources.displayMetrics.density).toInt()
            this.cellPixelSize = cellPixelSize

            if (cellPixelSize > 0) {
                adapter.notifyDataSetChanged()
            }

        }

    }

    private fun toggleSingleChecked(asset: Asset) {

        // checked 获取反选值
        val checked = asset.order < 0
        val selectedCount = selectedAssetList.count()

        if (selectedCount == 1) {
            val selectedAsset = selectedAssetList[0]
            selectedAsset.order = -1
            selectedAssetList.remove(selectedAsset)
            // 如果只是点击取消选择，要触发回调
            // 否则跟下面那段一起触发回调
            if (!checked) {
                onSelectedAssetListChange?.invoke()
            }
            adapter.notifyItemChanged(selectedAsset.index)
        }

        if (checked) {
            asset.order = 0
            selectedAssetList.add(asset)
            onSelectedAssetListChange?.invoke()
            adapter.notifyItemChanged(asset.index)
        }

    }

    private fun toggleMultiChecked(asset: Asset) {

        // checked 获取反选值
        val checked = asset.order < 0
        val selectedCount = selectedAssetList.count()
        val maxSelectCount = configuration.maxSelectCount

        if (checked) {

            // 因为有动画，用户可能在动画过程中快速点击了新的照片
            // 这里应该忽略
            if (selectedCount == maxSelectCount) {
                return
            }

            asset.order = selectedCount
            selectedAssetList.add(asset)
            onSelectedAssetListChange?.invoke()

            // 到达最大值，就无法再选了
            if (selectedCount + 1 == maxSelectCount) {
                adapter.notifyDataSetChanged()
            }
            else {
                adapter.notifyItemChanged(asset.index)
            }

        }
        else {

            selectedAssetList.remove(asset)
            onSelectedAssetListChange?.invoke()

            asset.order = -1

            val changes = mutableListOf<Int>()

            changes.add(asset.index)

            // 重排顺序
            selectedAssetList.forEachIndexed { index, item ->
                if (index != item.order) {
                    item.order = index
                    changes.add(item.index)
                }
            }

            // 上个状态是到达上限
            if (selectedCount == maxSelectCount) {
                adapter.notifyDataSetChanged()
            }
            else {
                changes.forEach {
                    adapter.notifyItemChanged(it)
                }
            }

        }

    }

    inner class PhotoGridAdapter : RecyclerView.Adapter<AssetItem>() {

        override fun getItemCount(): Int {
            return assetList.count()
        }

        override fun onBindViewHolder(holder: AssetItem, position: Int) {

            val asset = assetList[position]

            asset.index = position

            // 选中状态下可以反选
            asset.selectable = if (asset.order >= 0) {
                true
            }
            // 单选总是可选
            else if (configuration.maxSelectCount == 1) {
                true
            }
            else {
                selectedAssetList.count() < configuration.maxSelectCount
            }

            holder.bind(asset, cellPixelSize)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetItem {

            val binding = PhotoPickerAssetItemBinding.inflate(LayoutInflater.from(context), parent, false)

            return AssetItem(
                binding,
                configuration,
                {
                    onAssetClick?.invoke(it)
                },
                {
                    if (configuration.maxSelectCount > 1) {
                        toggleMultiChecked(it)
                    }
                    else {
                        toggleSingleChecked(it)
                    }
                }
            )
        }

    }

    inner class PhotoGridDecoration : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

            val index = parent.getChildAdapterPosition(view)
            val columnCount = configuration.assetGirdSpanCount
            val rowCount = assetList.count() / columnCount
            val rowIndex = index / columnCount
            val columnIndex = index % columnCount

            if (rowIndex == 0) {
                outRect.top = paddingVertical
            }
            else {
                outRect.top = rowSpacing
                if (rowIndex == rowCount) {
                    outRect.bottom = paddingVertical
                }
            }

            if (columnIndex == 0) {
                outRect.left = paddingHorizontal
            }
            else {
                outRect.left = columnSpacing
                if (columnIndex == columnCount - 1) {
                    outRect.right = paddingHorizontal
                }
            }

        }

    }

}