package com.github.herokotlin.photopicker.model

import com.github.herokotlin.photopicker.enum.AssetType
import java.io.File
import java.net.URLEncoder
import java.util.regex.Pattern

data class PickedAsset(
    val path: String,
    val name: String,
    val width: Int,
    val height: Int,
    val size: Int,
    val isVideo: Boolean,
    val isRaw: Boolean
) {
    companion object {

        // 文件名包含其他字符，需转存一份，避免调用者出现编码问题，导致无法上传
        private val pattern = Pattern.compile("[^A-Za-z0-9_]")

        fun build(asset: Asset, isRawChecked: Boolean, cacheDir: String): PickedAsset {

            var path = asset.path
            var fileName = asset.name

            var extName = ""

            val index = fileName.indexOf(".")
            if (index > 0) {
                extName = fileName.substring(index)
                fileName = fileName.substring(0, index)
            }

            if (pattern.matcher(fileName).find()) {
                val source = File(path)
                fileName = URLEncoder.encode(fileName, "utf-8")
                path = "$cacheDir/$fileName$extName"
                source.copyTo(File(path), true)
            }

            return PickedAsset(path, asset.name, asset.width, asset.height, asset.size, asset.type == AssetType.VIDEO, isRawChecked)

        }

    }
}