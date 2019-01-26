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
import android.webkit.URLUtil
import com.github.herokotlin.photopicker.model.Album
import com.github.herokotlin.photopicker.model.AssetType
import com.github.herokotlin.photopicker.model.Asset
import com.github.herokotlin.photopicker.model.PickedAsset
import kotlinx.android.synthetic.main.photo_picker_activity.*
import kotlinx.android.synthetic.main.photo_picker_bottom_bar.view.*
import kotlinx.android.synthetic.main.photo_picker_title_button.view.*
import kotlinx.android.synthetic.main.photo_picker_top_bar.view.*
import java.io.File
import java.util.regex.Pattern

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

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

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

        PhotoPickerManager.onPermissionsGranted = {
            callback.onPermissionsGranted(this)
        }
        PhotoPickerManager.onPermissionsDenied = {
            callback.onPermissionsDenied(this)
        }
        PhotoPickerManager.onFetchWithoutPermissions = {
            callback.onFetchWithoutPermissions(this)
        }
        PhotoPickerManager.onFetchWithoutExternalStorage = {
            callback.onFetchWithoutExternalStorage(this)
        }
        PhotoPickerManager.requestPermissions(configuration) {
            PhotoPickerManager.scan(this, configuration) {
                val albumList = PhotoPickerManager.fetchAlbumList(configuration)
                albumListView.albumList = albumList
                currentAlbum = if (albumList.count() > 0) albumList[0] else null
            }
        }

        topBar.cancelButton.setOnClickListener {
            callback.onCancel(this)
        }

        topBar.titleButton.setOnClickListener {
            toggleAlbumList()
        }

        bottomBar.configuration = configuration
        bottomBar.submitButton.setOnClickListener {
            submit()
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
            // 重置，避免下次打开 activity 还有选中状态
            it.order = -1
            selectedList.add(it)
        }

        // 不计数就用照片原来的顺序
        if (!configuration.countable) {
            selectedList.sortBy { it.index }
        }

        // 排序完成之后，转成 PickedAsset
        val isRawChecked = bottomBar.isRawChecked

        // 文件名包含其他字符，需转存一份，避免调用者出现编码问题，导致无法上传
        val pattern = Pattern.compile("[^A-Za-z0-9_]")
        val targetPathPrefix = "${externalCacheDir.absolutePath}${File.separator}${System.currentTimeMillis()}"

        val result = selectedList.map {

            var path = it.path

            var fileName = URLUtil.guessFileName(it.path, null, null)
            var extName = ""

            val index = fileName.indexOf(".")
            if (index > 0) {
                extName = fileName.substring(index)
                fileName = fileName.substring(0, index)
            }

            if (pattern.matcher(fileName).find()) {
                val source = File(path)
                path = "$targetPathPrefix${it.index}$extName"
                source.copyTo(File(path), true)
            }

            PickedAsset(path, it.width, it.height, it.size, it.type == AssetType.VIDEO, isRawChecked)
        }



        callback.onSubmit(this, result)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PhotoPickerManager.requestPermissionsResult(requestCode, permissions, grantResults)
    }

}