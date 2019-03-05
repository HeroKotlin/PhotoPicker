package com.github.herokotlin.photopicker.model

import com.github.herokotlin.photopicker.enum.AssetType

data class Asset(
    val path: String,
    val name: String,
    val width: Int,
    val height: Int,
    val size: Int,
    val type: AssetType,

    var index: Int = -1,
    var order: Int = -1,
    var selectable: Boolean = true
) {
    companion object {

        fun build(path: String, width: Int, height: Int, size: Int, mimeType: String?): Asset? {

            val index = path.lastIndexOf("/")
            val name: String

            if (index >= 0) {
                name = path.substring(index + 1)
            }
            else {
                return null
            }

            if (mimeType == null) {
                return null
            }

            var type = AssetType.IMAGE

            val parts = mimeType.split("/")
            when (parts[0]) {
                "video" -> {
                    type = AssetType.VIDEO
                }
                "image" -> {
                    when (parts[1]) {
                        "gif" -> {
                            type = AssetType.GIF
                        }
                        "webp" -> {
                            type = AssetType.WEBP
                        }
                    }
                }
            }

            return Asset(path, name, width, height, size, type)

        }

    }
}