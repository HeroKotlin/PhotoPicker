package com.github.herokotlin.photopicker

import android.content.Context
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

    /**
     * 加载图片
     */
    abstract fun loadImage(imageView: ImageView, url: String, width: Int, height: Int)

}