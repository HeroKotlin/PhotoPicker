package com.github.herokotlin.photopicker.view

import android.support.v7.widget.RecyclerView
import android.view.View
import com.github.herokotlin.photopicker.PhotoPickerConfiguration
import com.github.herokotlin.photopicker.R
import com.github.herokotlin.photopicker.enum.AssetType
import com.github.herokotlin.photopicker.model.Asset
import kotlinx.android.synthetic.main.photo_picker_asset_item.view.*

class AssetItem(view: View, private val configuration: PhotoPickerConfiguration, private val onClick: ((Asset) -> Unit), private val onToggleChecked: ((Asset) -> Unit)): RecyclerView.ViewHolder(view) {

    private val thumbnailView = view.thumbnailView

    private val badgeView = view.badgeView

    private val selectButton = view.selectButton

    private val overlayView = view.overlayView

    private var pixelSize = 0

        set(value) {

            if (field == value) {
                return
            }

            field = value

            thumbnailView.layoutParams.width = value
            thumbnailView.layoutParams.height = value

        }

    private var asset: Asset? = null

        set(value) {

            if (field == value) {
                return
            }

            field = value

            selectButton.visibility = View.GONE
            configuration.loadAsset(
                itemView.thumbnailView,
                value!!.path,
                R.drawable.photo_picker_asset_thumbnail_loading_placeholder,
                R.drawable.photo_picker_asset_thumbnail_error_placeholder
            ) {
                if (it) {
                    selectButton.visibility = View.VISIBLE
                }
            }

        }

    private var checked = false

        set(value) {

            // 这里有 checked 和 order 两个操作
            // 因此不能加 if (field == value) { return }

            field = value

            selectButton.checked = asset!!.order >= 0
            selectButton.order = if (configuration.countable && asset!!.order >= 0) asset!!.order + 1 else -1

        }

    private var selectable = false

        set(value) {

            if (field == value) {
                return
            }

            field = value

            overlayView.visibility = if (value) View.GONE else View.VISIBLE

        }

    init {
        selectButton.countable = configuration.countable

        // overlayView 如果是透明色，点击会穿透
        selectButton.setOnClickListener {
            if (selectable) {
                onToggleChecked.invoke(asset!!)
            }
        }

        view.setOnClickListener {
            if (selectable) {
                onClick.invoke(asset!!)
            }
        }
    }

    fun bind(asset: Asset, pixelSize: Int) {

        this.asset = asset
        this.pixelSize = pixelSize

        val drawable = when (asset.type) {
            AssetType.GIF -> {
                R.drawable.photo_picker_badge_gif
            }
            AssetType.WEBP -> {
                R.drawable.photo_picker_badge_webp
            }
            else -> {
                0
            }
        }

        if (drawable != 0) {
            badgeView.setImageResource(drawable)
            badgeView.visibility = View.VISIBLE
        }
        else {
            badgeView.visibility = View.GONE
        }

        checked = asset.order >= 0
        selectable = asset.selectable

    }

}