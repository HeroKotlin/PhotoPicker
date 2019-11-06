package com.github.herokotlin.photopicker.view

import androidx.recyclerview.widget.RecyclerView
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

            value?.let {

                val selectable = it.selectable
                if (selectable) {
                    selectButton.visibility = View.GONE
                }

                configuration.loadAsset(
                    itemView.thumbnailView,
                    it.path,
                    R.drawable.photo_picker_asset_thumbnail_loading_placeholder,
                    R.drawable.photo_picker_asset_thumbnail_error_placeholder
                ) {
                    if (it && selectable) {
                        selectButton.visibility = View.VISIBLE
                    }
                }
            }

        }

    private var checked = false

        set(value) {

            // 这里有 checked 和 order 两个操作
            // 因此不能加 if (field == value) { return }

            field = value

            asset?.let {
                selectButton.checked = it.order >= 0
                selectButton.order = if (configuration.countable && it.order >= 0) it.order + 1 else -1
            }


        }

    init {
        selectButton.countable = configuration.countable

        selectButton.setOnClickListener {
            asset?.let {
                onToggleChecked.invoke(it)
            }
        }

        view.setOnClickListener {
            asset?.let {
                onClick.invoke(it)
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

        selectButton.visibility = if (asset.selectable) {
            View.VISIBLE
        }
        else {
            View.GONE
        }

    }

}