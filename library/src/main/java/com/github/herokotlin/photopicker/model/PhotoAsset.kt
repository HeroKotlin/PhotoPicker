package com.github.herokotlin.photopicker.model

import android.webkit.MimeTypeMap

data class PhotoAsset(
    val path: String,
    var name: String,
    var time: Long,
    var type: AssetType,
    var index: Int = -1,
    var order: Int = -1,
    var selectable: Boolean = true
) {
    companion object {

        fun build(path: String, name: String, time: Long): PhotoAsset {

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

            return PhotoAsset(path, name, time, type)

        }

    }
}