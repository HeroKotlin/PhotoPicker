package com.github.herokotlin.photopicker

import android.app.Activity
import android.provider.MediaStore
import android.widget.ImageView
import com.github.herokotlin.photopicker.enum.AssetType
import com.github.herokotlin.photopicker.model.Album
import com.github.herokotlin.photopicker.model.Asset

abstract class PhotoPickerConfiguration {

    /**
     * 最多选择多少张照片
     */
    var maxSelectCount = 9

    /**
     * 是否支持计数
     */
    var countable = true

    /**
     * 图片的最小宽度
     */
    var imageMinWidth = 0

    /**
     * 图片的最小高度
     */
    var imageMinHeight = 0

    /**
     * 是否显示原图按钮
     */
    var rawButtonVisible = true

    /**
     * 网格每行多少个
     */
    var assetGirdSpanCount = 3

    /**
     * 排序方式
     */
    var assetSortBy = MediaStore.Images.Media.DATE_ADDED + " DESC"

    /**
     * "所有图片" 专辑的标题
     */
    var allPhotosAlbumTitle = "所有照片"

    /**
     * 请求权限
     */
    abstract fun requestPermissions(activity: Activity, permissions: List<String>, requestCode: Int): Boolean

    /**
     * 加载图片
     */
    abstract fun loadAsset(imageView: ImageView, url: String, loading: Int, error: Int, onComplete: (Boolean) -> Unit)

    /**
     * 过滤相册
     */
    open fun filter(album: Album): Boolean {
        if (album.assetList.count() == 0) {
            return false
        }
        if (album.title.startsWith("drawable-")) {
            return false
        }
        return true
    }

    /**
     * 过滤图片
     */
    open fun filter(asset: Asset): Boolean {
        return asset.width > imageMinWidth && asset.height > imageMinHeight && asset.type != AssetType.VIDEO
    }

}