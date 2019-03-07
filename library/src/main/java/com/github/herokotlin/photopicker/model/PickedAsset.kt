package com.github.herokotlin.photopicker.model

import com.github.herokotlin.photopicker.enum.AssetType

data class PickedAsset(
    val path: String,
    val name: String,
    val width: Int,
    val height: Int,
    val size: Int,
    val isVideo: Boolean,
    val isRaw: Boolean
) {
    companion object {

        fun build(asset: Asset, isRawChecked: Boolean): PickedAsset {
            return PickedAsset(asset.path, asset.name, asset.width, asset.height, asset.size, asset.type == AssetType.VIDEO, isRawChecked)
        }

    }
}