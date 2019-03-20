package com.github.herokotlin.photopicker

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import com.github.herokotlin.permission.Permission
import com.github.herokotlin.photopicker.model.Album
import com.github.herokotlin.photopicker.model.Asset
import com.github.herokotlin.photopicker.model.PickedAsset
import kotlinx.android.synthetic.main.photo_picker_activity.*
import kotlinx.android.synthetic.main.photo_picker_bottom_bar.view.*
import kotlinx.android.synthetic.main.photo_picker_title_button.view.*
import kotlinx.android.synthetic.main.photo_picker_top_bar.view.*

class PhotoPickerActivity: AppCompatActivity() {

    companion object {

        lateinit var callback: PhotoPickerCallback

        lateinit var configuration: PhotoPickerConfiguration

        fun newInstance(context: Activity) {
            val intent = Intent(context, PhotoPickerActivity::class.java)
            context.startActivity(intent)
        }

    }

    // 当前选中的相册
    private var currentAlbum: Album? = null

        set(value) {

            if (field === value) {
                return
            }

            field = value

            val title: String
            val assetList: List<Asset>

            if (value != null) {
                title = value.title
                assetList = PhotoPickerManager.fetchPhotoList(value.title)
            }
            else {
                title = ""
                assetList = listOf()
            }

            topBar.titleButton.title = title
            assetGridView.assetList = assetList

        }

    private var albumListVisible = false

    private var rotateAnimation: RotateAnimation? = null
    private var translateAnimation: ValueAnimator? = null

    private val permission = Permission(89190903, listOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE))

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.photo_picker_activity)


        assetGridView.init(configuration)
        assetGridView.onSelectedAssetListChange = {
            bottomBar.selectedCount = assetGridView.selectedAssetList.count()
        }

        albumListView.init(configuration)
        albumListView.onAlbumClick = {
            currentAlbum = it
            toggleAlbumList()
        }

        permission.onPermissionsGranted = {
            callback.onPermissionsGranted(this)
        }
        permission.onPermissionsDenied = {
            callback.onPermissionsDenied(this)
        }
        permission.onPermissionsNotGranted = {
            callback.onPermissionsNotGranted(this)
        }
        permission.onExternalStorageNotWritable = {
            callback.onExternalStorageNotWritable(this)
        }
        if (permission.checkExternalStorageWritable()) {
            permission.requestPermissions(this) {
                PhotoPickerManager.scan(this, configuration) {
                    val albumList = PhotoPickerManager.fetchAlbumList(configuration)
                    albumListView.albumList = albumList
                    currentAlbum = if (albumList.count() > 0) albumList[0] else null
                }
            }
        }

        if (configuration.cancelButtonTitle.isNotEmpty()) {
            topBar.cancelButton.text = configuration.cancelButtonTitle
        }
        topBar.cancelButton.setOnClickListener {
            callback.onCancel(this)
        }

        topBar.titleButton.setOnClickListener {
            toggleAlbumList()
        }

        if (configuration.submitButtonTitle.isNotEmpty()) {
            bottomBar.submitButton.text = configuration.submitButtonTitle
        }
        bottomBar.configuration = configuration
        bottomBar.submitButton.setOnClickListener {
            submit()
        }

        if (!configuration.rawButtonVisible) {
            bottomBar.rawButton.visibility = View.GONE
        }
        else if (configuration.rawButtonTitle.isNotEmpty()) {
            bottomBar.rawButton.text = configuration.rawButtonTitle
        }

    }

    private fun toggleAlbumList() {

        val visible = !albumListVisible

        val topBarBottom = topBar.y + topBar.height
        val bottomBarBottom = bottomBar.y + bottomBar.height

        val height = bottomBarBottom - (topBarBottom)
        albumListView.layoutParams.height = height.toInt()

        val fromY: Float
        val toY: Float

        val fromAngle: Float
        val toAngle: Float

        if (visible) {
            albumListView.visibility = View.VISIBLE
            fromY = topBarBottom - height
            toY = topBarBottom
            fromAngle = 0f
            toAngle = -180f
        }
        else {
            fromY = topBarBottom
            toY = topBarBottom - height
            fromAngle = -180f
            toAngle = 0f
        }

        translateAnimation?.cancel()

        val animator = ValueAnimator.ofFloat(fromY, toY)
        animator.duration = 200
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener {
            albumListView.y = it.animatedValue as Float
        }
        animator.addListener(object: AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                translateAnimation = null
                if (!visible) {
                    albumListView.visibility = View.GONE
                }
            }
        })
        animator.start()

        translateAnimation = animator



        rotateAnimation?.cancel()

        val animation = RotateAnimation(fromAngle, toAngle, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        animation.duration = 200
        animation.repeatCount = 0
        animation.fillAfter = true
        animation.setAnimationListener(object: Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {

            }
            override fun onAnimationStart(animation: Animation?) {

            }
            override fun onAnimationEnd(animation: Animation?) {
                rotateAnimation = null
            }
        })
        topBar.titleButton.arrowView.startAnimation(animation)

        rotateAnimation = animation


        albumListVisible = visible

    }

    private fun submit() {

        // 先排序
        val selectedList = mutableListOf<Asset>()

        assetGridView.selectedAssetList.forEach {
            selectedList.add(it)
        }

        // 不计数就用照片原来的顺序
        if (!configuration.countable) {
            selectedList.sortBy { it.index }
        }

        val isRawChecked = bottomBar.isRawChecked

        callback.onSubmit(
            this,
            selectedList.map { PickedAsset.build(it, isRawChecked) }
        )

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permission.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}