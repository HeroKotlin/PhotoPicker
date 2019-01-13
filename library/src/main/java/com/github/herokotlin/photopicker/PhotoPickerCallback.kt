package com.github.herokotlin.photopicker

import android.app.Activity
import com.github.herokotlin.photopicker.model.PickedAsset

interface PhotoPickerCallback {

    fun onCancel(activity: Activity) {

    }

    fun onSubmit(activity: Activity, assetList: List<PickedAsset>) {

    }

    // 拉取相册数据时，发现没权限
    fun onFetchWithoutPermissions(activity: Activity) {

    }

    // 没有外部存储可用
    fun onFetchWithoutExternalStorage(activity: Activity) {

    }

    // 用户点击同意授权
    fun onPermissionsGranted(activity: Activity) {

    }

    // 用户点击拒绝授权
    fun onPermissionsDenied(activity: Activity) {

    }

}