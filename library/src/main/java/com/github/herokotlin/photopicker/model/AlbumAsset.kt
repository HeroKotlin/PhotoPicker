package com.github.herokotlin.photopicker.model

data class AlbumAsset(
    var title: String,
    var poster: PhotoAsset,
    var photoList: List<PhotoAsset>
) {
    companion object {

        fun build(title: String, photoList: List<PhotoAsset>): AlbumAsset {

            return AlbumAsset(title, photoList[0], photoList)

        }

    }
}