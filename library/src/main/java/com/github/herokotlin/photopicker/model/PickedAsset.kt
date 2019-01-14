package com.github.herokotlin.photopicker.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PickedAsset(
    val path: String,
    val width: Int,
    val height: Int,
    val size: Int,
    val isVideo: Boolean,
    val isFull: Boolean
): Parcelable