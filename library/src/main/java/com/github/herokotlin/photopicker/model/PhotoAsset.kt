package com.github.herokotlin.photopicker.model

import android.webkit.MimeTypeMap

data class PhotoAsset(
    val path: String,
    val width: Int,
    val height: Int,
    val size: Int,
    val type: AssetType,
    var index: Int = -1,
    var order: Int = -1,
    var selectable: Boolean = true
) {
    companion object {

        fun build(path: String, width: Int, height: Int, size: Int): PhotoAsset {

            var type = AssetType.IMAGE

            val extension = MimeTypeMap.getFileExtensionFromUrl(path)
            if (extension.isNotEmpty()) {
                val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase())

                val parts = mimeType.split("/")
                when (parts[0]) {
                    "video" -> {
                        type = AssetType.VIDEO
                    }
                    "audio" -> {
                        type = AssetType.AUDIO
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
            }

            return PhotoAsset(path, width, height, size, type)

        }

    }
}