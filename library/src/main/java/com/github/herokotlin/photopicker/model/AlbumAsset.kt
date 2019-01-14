package com.github.herokotlin.photopicker.model

data class AlbumAsset(
    val title: String,
    val poster: PhotoAsset?,
    val photoList: List<PhotoAsset>
) {
    companion object {

        fun build(title: String, photoList: List<PhotoAsset>): AlbumAsset {

            return AlbumAsset(title, if (photoList.count() > 0) photoList[0] else null, photoList)

        }

    }
}