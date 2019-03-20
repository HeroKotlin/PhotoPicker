package com.github.herokotlin.photopicker

import android.app.Activity
import com.github.herokotlin.photopicker.model.PickedAsset

interface PhotoPickerCallback {

    fun onCancel(activity: Activity)

    fun onSubmit(activity: Activity, assetList: List<PickedAsset>)

    fun onPermissionsGranted(activity: Activity) {

    }

    fun onPermissionsDenied(activity: Activity) {

    }

    fun onPermissionsNotGranted(activity: Activity) {

    }

    fun onExternalStorageNotWritable(activity: Activity) {

    }

}