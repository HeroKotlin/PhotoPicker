package com.github.herokotlin.photopicker.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.util.Base64
import android.webkit.MimeTypeMap
import com.github.herokotlin.photopicker.PhotoPickerConfiguration
import com.github.herokotlin.photopicker.enum.AssetType
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

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

        private fun outputImageFile(context: Context, name: String, data: ByteArray): String {

            var outputDir = context.cacheDir.absolutePath
            if (!outputDir.endsWith(File.separator)) {
                outputDir += File.separator
            }

            val outputPath = outputDir + name

            val fileOutputStream = FileOutputStream(outputPath)
            fileOutputStream.write(data)
            fileOutputStream.close()

            return outputPath

        }
        fun build(context: Context, asset: Asset, configuration: PhotoPickerConfiguration, isOriginal: Boolean): PickedAsset {

            val imageMaxWidth = configuration.imageMaxWidth
            val imageMaxHeight = configuration.imageMaxHeight
            val imageBase64Enabled = asset.type != AssetType.VIDEO && configuration.imageBase64Enabled

            val originalWidth = asset.width
            val originalHeight = asset.height

            var outputWidth = originalWidth
            var outputHeight = originalHeight

            var outputPath = asset.path
            var outputName = asset.name
            var outputBase64 = ""
            var outputSize = asset.size
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

            if (needScaleImage) {
                val originalBitmap = BitmapFactory.decodeFile(asset.path)
                val originalHasAlpha = originalBitmap.hasAlpha()
                val formatFormat = if (originalHasAlpha) {
                    Bitmap.CompressFormat.PNG
                } else {
                    Bitmap.CompressFormat.JPEG
                }

                val scaleBitmap = Bitmap.createBitmap(outputWidth, outputHeight, Bitmap.Config.ARGB_8888)
                val scaleCanvas = Canvas(scaleBitmap)
                val byteArrayOutputStream = ByteArrayOutputStream()

                scaleCanvas.drawBitmap(originalBitmap, Rect(0, 0, originalWidth, originalHeight), Rect(0, 0, outputWidth, outputHeight), null)
                scaleBitmap.compress(formatFormat, 100, byteArrayOutputStream)

                outputSize = byteArrayOutputStream.size()
                outputName = UUID.randomUUID().toString() + if (originalHasAlpha) {
                    ".png"
                }
                else {
                    ".jpg"
                }

                val byteArray = byteArrayOutputStream.toByteArray()
                outputPath = outputImageFile(context, outputName, byteArray)
                if (imageBase64Enabled) {
                    outputBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT)
                }

                byteArrayOutputStream.close()
                scaleBitmap.recycle()
                originalBitmap.recycle()
            }
            else if (imageBase64Enabled) {
                val bitmap = BitmapFactory.decodeFile(asset.path)
                val output = ByteArrayOutputStream()

                val formatFormat = if (bitmap.hasAlpha()) {
                    Bitmap.CompressFormat.PNG
                } else {
                    Bitmap.CompressFormat.JPEG
                }
                bitmap.compress(formatFormat, 100, output)
                outputBase64 = Base64.encodeToString(output.toByteArray(), Base64.DEFAULT)
                output.close()
                bitmap.recycle()
            }

            return PickedAsset(outputPath, outputName, outputBase64, outputWidth, outputHeight, outputSize, asset.type == AssetType.VIDEO, isOriginal)
        }

    }
}