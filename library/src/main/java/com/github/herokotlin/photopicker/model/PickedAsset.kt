package com.github.herokotlin.photopicker.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PickedAsset(
    val path: String,
    val width: Int,
    val height: Int,
    val size: Long,
    val isVideo: Boolean,
    val isRaw: Boolean
): Parcelable