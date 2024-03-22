package com.github.herokotlin.photopicker.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.webkit.MimeTypeMap
import com.github.herokotlin.photopicker.enum.AssetType
import java.io.ByteArrayOutputStream

data class PickedAsset(
    val path: String,
    val name: String,
    var base64: String,
    val width: Int,
    val height: Int,
    val size: Int,
    val isVideo: Boolean,
    val isRaw: Boolean
) {
    companion object {

        fun build(asset: Asset, isRawChecked: Boolean): PickedAsset {

            val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                MimeTypeMap.getFileExtensionFromUrl(asset.path)
            )

            var base64 = ""

            // 是图片要获取 base64
            if (mimeType.contains("image", true)) {
                val bitmap = BitmapFactory.decodeFile(asset.path)
                val output = ByteArrayOutputStream()

                if (mimeType == "image/png") {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
                }
                else {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output)
                }

                base64 = Base64.encodeToString(output.toByteArray(), Base64.DEFAULT)
                output.close()
            }

            return PickedAsset(asset.path, asset.name, base64, asset.width, asset.height, asset.size, asset.type == AssetType.VIDEO, isRawChecked)
        }

    }
}