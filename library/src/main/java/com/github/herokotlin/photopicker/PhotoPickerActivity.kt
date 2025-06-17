package com.github.herokotlin.photopicker

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.core.view.WindowCompat
import com.github.herokotlin.photopicker.databinding.PhotoPickerActivityBinding
import com.github.herokotlin.photopicker.model.Album
import com.github.herokotlin.photopicker.model.Asset
import com.github.herokotlin.photopicker.model.PickedAsset

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

            binding.topBar.binding.titleButton.title = title
            binding.assetGridView.assetList = assetList

        }

    private lateinit var binding: PhotoPickerActivityBinding
    private var albumListVisible = false

    private var rotateAnimation: RotateAnimation? = null
    private var translateAnimation: ValueAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        // >= 安卓15 关闭 edge-to-edge
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            WindowCompat.setDecorFitsSystemWindows(window, true)
        }

        binding = PhotoPickerActivityBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.assetGridView.init(configuration)
        binding.assetGridView.onSelectedAssetListChange = {
            binding.bottomBar.selectedCount = binding.assetGridView.selectedAssetList.count()
        }

        binding.albumListView.init(configuration)
        binding.albumListView.onAlbumClick = {
            currentAlbum = it
            toggleAlbumList()
        }

        if (configuration.cancelButtonTitle.isNotEmpty()) {
            binding.topBar.binding.cancelButton.text = configuration.cancelButtonTitle
        }
        binding.topBar.binding.cancelButton.setOnClickListener {
            callback.onCancel(this)
        }

        binding.topBar.binding.titleButton.setOnClickListener {
            toggleAlbumList()
        }

        if (configuration.submitButtonTitle.isNotEmpty()) {
            binding.bottomBar.binding.submitButton.text = configuration.submitButtonTitle
        }
        binding.bottomBar.configuration = configuration
        binding.bottomBar.binding.submitButton.setOnClickListener {
            submit()
        }

        if (!configuration.showOriginalButton) {
            binding.bottomBar.binding.originalButton.visibility = View.GONE
        }
        else if (configuration.originalButtonTitle.isNotEmpty()) {
            binding.bottomBar.binding.originalButton.text = configuration.originalButtonTitle
        }

        // 用 permission 属性在外面获取完权限再进来吧
        // 否则没权限一片漆黑，体验极差
        PhotoPickerManager.scan(this, configuration) {
            val albumList = PhotoPickerManager.fetchAlbumList(configuration)
            binding.albumListView.albumList = albumList
            currentAlbum = if (albumList.isNotEmpty()) albumList[0] else null
        }

    }

    private fun toggleAlbumList() {

        val visible = !albumListVisible

        val topBarBottom = binding.topBar.y + binding.topBar.height
        val bottomBarBottom = binding.bottomBar.y + binding.bottomBar.height

        val height = bottomBarBottom - (topBarBottom)
        binding.assetGridView.layoutParams.height = height.toInt()

        val fromY: Float
        val toY: Float

        val fromAngle: Float
        val toAngle: Float

        if (visible) {
            binding.assetGridView.visibility = View.VISIBLE
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
            binding.assetGridView.y = it.animatedValue as Float
        }
        animator.addListener(object: AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                translateAnimation = null
                if (!visible) {
                    binding.assetGridView.visibility = View.GONE
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
        binding.topBar.binding.titleButton.binding.arrowView.startAnimation(animation)

        rotateAnimation = animation

        albumListVisible = visible

    }

    private fun submit() {

        // 先排序
        val selectedList = mutableListOf<Asset>()

        binding.assetGridView.selectedAssetList.forEach {
            selectedList.add(it)
        }

        // 不计数就用照片原来的顺序
        if (!configuration.countable) {
            selectedList.sortBy { it.index }
        }

        val context = this.applicationContext

        callback.onSubmit(
            this,
            selectedList.map { PickedAsset.build(context, it, configuration, binding.bottomBar.isOriginalChecked) }
        )

    }

}