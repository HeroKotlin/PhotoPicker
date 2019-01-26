package com.github.herokotlin.photopicker

import android.graphics.Bitmap
import android.provider.MediaStore
import android.widget.ImageView
import com.github.herokotlin.photopicker.model.AssetType

abstract class PhotoPickerConfiguration {

    /**
     * 网格每行多少个
     */
    var assetGirdSpanCount = 3

    /**
     * 最多选择多少张照片
     */
    var maxSelectCount = 9

    /**
     * 是否支持计数
     */
    var countable = true

    /**
     * 排序字段
     */
    var assetSortField = MediaStore.Images.Media.DATE_ADDED

    /**
     * 是否正序
     */
    var assetSortAscending = false

    /**
     * 支持的文件类型
     */
    var assetMimeTypes = arrayOf("image/jpeg", "image/png", "image/gif", "image/webp")

    /**
     * "所有图片" 专辑的标题
     */
    var allPhotosAlbumTitle = "所有照片"

    /**
     * 请求权限
     */
    abstract fun requestPermissions(permissions: List<String>, requestCode: Int): Boolean

    /**
     * 加载图片
     */
    abstract fun loadAsset(imageView: ImageView, url: String, loading: Int, error: Int, onComplete: (Boolean) -> Unit)

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

    /**
     * 过滤图片
     */
    open fun filterAsset(width: Int, height: Int, type: AssetType): Boolean {
        return width > 44 && height > 44
    }

}