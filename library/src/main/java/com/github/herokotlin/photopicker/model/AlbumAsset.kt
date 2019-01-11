package com.github.herokotlin.photopicker.model

data class AlbumAsset(
    var title: String,
    var poster: PhotoAsset,
    var photoList: List<PhotoAsset>
)