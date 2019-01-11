package com.github.herokotlin.photopicker

import android.content.Context
import android.provider.MediaStore
import android.widget.ImageView

abstract class PhotoPickerConfiguration(val context: Context) {

    /**
     * 照片网格每行多少个
     */
    var photoGirdSpanCount = 3

    /**
     * 最多选择多少张照片
     */
    var maxSelectCount = 9

    /**
     * 是否支持多选
     */
    var selectable = true

    /**
     * 是否支持计数
     */
    var countable = true

    var photoSortField = MediaStore.Images.Media.DATE_ADDED

    var photoSortAscending = false

    var photoMimeTypes = arrayOf("image/jpeg", "image/png", "image/gif", "image/webp")

    var allPhotosAlbumTitle = "所有照片"

    /**
     * 加载图片
     */
    abstract fun loadImage(imageView: ImageView, url: String, width: Int, height: Int)

    /**
     * 过滤相册
     */
    open fun filterAlbum(title: String, count: Int): Boolean {
        return true
    }

}