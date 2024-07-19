package com.github.herokotlin.photopicker.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.util.Base64
import android.webkit.MimeTypeMap
import com.github.herokotlin.photopicker.PhotoPickerConfiguration
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
    val isOriginal: Boolean
) {
    companion object {

        fun build(asset: Asset, configuration: PhotoPickerConfiguration, isOriginal: Boolean): PickedAsset {

            val imageMaxWidth = configuration.imageMaxWidth
            val imageMaxHeight = configuration.imageMaxHeight

            val originalWidth = asset.width
            val originalHeight = asset.height

            var outputWidth = originalWidth
            var outputHeight = originalHeight

            var needScaleImage = false

            if (imageMaxWidth > 0 || imageMaxHeight > 0) {
                val ratio = originalWidth.toFloat() / originalHeight.toFloat()
                if (imageMaxWidth in 1 until outputWidth) {
                    outputWidth = imageMaxWidth
                    outputHeight = (outputWidth.toFloat() / ratio).toInt()
                    needScaleImage = true
                }
                if (imageMaxHeight in 1 until outputHeight) {
                    outputHeight = imageMaxHeight
                    outputWidth = (outputHeight.toFloat() * ratio).toInt()
                    needScaleImage = true
                }
            }

            var cachedBitmap: Bitmap? = null

            val loadBitmap = fun (): Bitmap {
                if (cachedBitmap != null) {
                    return cachedBitmap!!
                }
                cachedBitmap = BitmapFactory.decodeFile(asset.path)
                return cachedBitmap!!
            }

            if (needScaleImage) {
                val originalBitmap = loadBitmap()
                val scaleBitmap = Bitmap.createBitmap(outputWidth, outputHeight, Bitmap.Config.ARGB_8888)

                val scaleCanvas = Canvas(scaleBitmap)
                scaleCanvas.drawBitmap(originalBitmap, Rect(0, 0, originalWidth, originalHeight), Rect(0, 0, outputWidth, outputHeight), null)

                originalBitmap.recycle()
                cachedBitmap = scaleBitmap
            }

            var base64 = ""

            if (asset.type != AssetType.VIDEO && configuration.imageBase64Enabled) {
                val bitmap = loadBitmap()
                val output = ByteArrayOutputStream()

                val formatFormat = if (bitmap.hasAlpha()) {
                    Bitmap.CompressFormat.PNG
                } else {
                    Bitmap.CompressFormat.JPEG
                }
                bitmap.compress(formatFormat, 100, output)
                base64 = Base64.encodeToString(output.toByteArray(), Base64.DEFAULT)
                output.close()
            }

            cachedBitmap?.recycle()

            return PickedAsset(asset.path, asset.name, base64, asset.width, asset.height, asset.size, asset.type == AssetType.VIDEO, isOriginal)
        }

    }
}