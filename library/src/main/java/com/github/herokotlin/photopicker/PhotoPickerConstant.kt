package com.github.herokotlin.photopicker

import android.provider.MediaStore

object PhotoPickerConstant {

    const val SIZE_KB = 1024

    const val SIZE_MB = 1024 * SIZE_KB

    const val FIELD_PATH = MediaStore.Files.FileColumns.DATA

    const val FIELD_WIDTH = MediaStore.Files.FileColumns.WIDTH

    const val FIELD_HEIGHT = MediaStore.Files.FileColumns.HEIGHT

    const val FIELD_SIZE = MediaStore.Files.FileColumns.SIZE

    const val FIELD_CREATE_TIME = MediaStore.Files.FileColumns.DATE_ADDED

    const val FIELD_UPDATE_TIME = MediaStore.Files.FileColumns.DATE_MODIFIED

    const val FIELD_MIME_TYPE = MediaStore.Files.FileColumns.MIME_TYPE

    const val FIELD_MEDIA_TYPE = MediaStore.Files.FileColumns.MEDIA_TYPE

    val FILE_FIELDS = arrayOf(FIELD_PATH, FIELD_WIDTH, FIELD_HEIGHT, FIELD_SIZE, FIELD_MIME_TYPE)

    const val MEDIA_TYPE_IMAGE = MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE

    const val MEDIA_TYPE_VIDEO = MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO

    const val MEDIA_TYPE_AUDIO = MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO

    const val MEDIA_TYPE_PLAYLIST = MediaStore.Files.FileColumns.MEDIA_TYPE_PLAYLIST

}