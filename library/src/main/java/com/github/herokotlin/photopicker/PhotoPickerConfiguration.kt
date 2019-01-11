package com.github.herokotlin.photopicker

import android.content.Context
import android.widget.ImageView

abstract class PhotoPickerConfiguration(val context: Context) {

    /**
     * 照片网格每行多少个
     */
    var photoGirdSpanCount = 3

    /**
     * 加载图片
     */
    abstract fun loadImage(imageView: ImageView, url: String, width: Int, height: Int)

}