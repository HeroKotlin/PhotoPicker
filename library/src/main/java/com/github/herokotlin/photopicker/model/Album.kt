package com.github.herokotlin.photopicker.model

data class Album(
    val title: String,
    val poster: Asset?,
    val assetList: List<Asset>
) {
    companion object {

        fun build(title: String, assetList: List<Asset>): Album {

            return Album(title, if (assetList.isNotEmpty()) assetList[0] else null, assetList)

        }

    }
}