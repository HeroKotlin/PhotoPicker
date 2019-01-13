package com.github.herokotlin.photopicker

import android.provider.MediaStore
import android.widget.ImageView
import com.github.herokotlin.photopicker.model.PhotoAsset

abstract class PhotoPickerConfiguration {

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
     * 标题按钮箭头动画时长
     */
    var titleButtonArrowAnimationDuration = 200L

    /**
     * 加载图片
     */
    abstract fun loadPhoto(imageView: ImageView, url: String, width: Int, height: Int)

    /**
     * 未选择原图模式时的压缩
     */
    open fun compressPhoto(photo: PhotoAsset): PhotoAsset {
        return photo
    }

    /**
     * 过滤相册
     */
    open fun filterAlbum(title: String, count: Int): Boolean {
        if (count == 0) {
            return false
        }
        if (title.startsWith("drawable-")) {
            return false
        }
        return true
    }

}