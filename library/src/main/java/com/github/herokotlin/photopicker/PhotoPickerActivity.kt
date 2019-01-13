package com.github.herokotlin.photopicker

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import com.github.herokotlin.photopicker.model.AlbumAsset
import com.github.herokotlin.photopicker.model.AssetType
import com.github.herokotlin.photopicker.model.PhotoAsset
import com.github.herokotlin.photopicker.model.PickedAsset
import kotlinx.android.synthetic.main.photo_picker_activity.*
import kotlinx.android.synthetic.main.photo_picker_bottom_bar.view.*
import kotlinx.android.synthetic.main.photo_picker_title_button.view.*
import kotlinx.android.synthetic.main.photo_picker_top_bar.view.*
import java.io.File

class PhotoPickerActivity: AppCompatActivity() {

    companion object {

        lateinit var configuration: PhotoPickerConfiguration

        fun newInstance(context: Activity, requestCode: Int) {
            val intent = Intent(context, PhotoPickerActivity::class.java)
            context.startActivityForResult(intent, requestCode)
        }

        fun obtainResult(data: Intent): ArrayList<PickedAsset> {
            return data.getParcelableArrayListExtra<PickedAsset>("photoList")
        }

    }

    // 当前选中的相册
    private var currentAlbum: AlbumAsset? = null

        set(value) {

            if (field === value) {
                return
            }

            field = value

            val title: String
            val photoList: List<PhotoAsset>

            if (value != null) {
                title = value.title
                photoList = PhotoPickerManager.fetchPhotoList(value.title)
            }
            else {
                title = ""
                photoList = listOf()
            }

            topBar.titleButton.title = title
            photoGridView.photoList = photoList

        }

    private var albumListVisible = false

    private var rotateAnimation: RotateAnimation? = null
    private var translateAnimation: ValueAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        var flags = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            flags = flags or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }

        window.decorView.systemUiVisibility = flags

        supportActionBar?.hide()

        setContentView(R.layout.photo_picker_activity)


        photoGridView.init(configuration)
        photoGridView.onSelectedPhotoListChange = {
            bottomBar.selectedCount = photoGridView.selectedPhotoList.count()
        }

        albumListView.init(configuration)
        albumListView.onAlbumClick = {
            currentAlbum = it
            toggleAlbumList()
        }

        PhotoPickerManager.onPermissionsGranted = {

        }
        PhotoPickerManager.onPermissionsDenied = {

        }
        PhotoPickerManager.onFetchWithoutPermissions = {

        }
        PhotoPickerManager.onFetchWithoutExternalStorage = {

        }
        PhotoPickerManager.requestPermissions(configuration) {
            PhotoPickerManager.scan(this, configuration) {
                val albumList = PhotoPickerManager.fetchAlbumList(configuration)
                albumListView.albumList = albumList
                currentAlbum = if (albumList.count() > 0) albumList[0] else null
            }
        }

        topBar.cancelButton.setOnClickListener {
            cancel()
        }

        topBar.titleButton.setOnClickListener {
            toggleAlbumList()
        }

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
        animator.duration = configuration.titleButtonArrowAnimationDuration
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
        animation.duration = configuration.titleButtonArrowAnimationDuration
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

    private fun cancel() {
        finish()
    }

    private fun submit() {

        val intent = Intent()

        val selectedList = mutableListOf<PhotoAsset>()

        photoGridView.selectedPhotoList.forEach {
            selectedList.add(it)
        }

        // 不计数就用照片原来的顺序
        if (!configuration.countable) {
            selectedList.sortBy { it.index }
        }

        // 排序完成之后，转成 PickedAsset

        val cacheDir = externalCacheDir
        val isRawChecked = bottomBar.isRawChecked

        val result = ArrayList<PickedAsset>()

        selectedList.forEach {

            // 先把原图转存一份
            // 避免调用者拿到原图乱改，比如改名字啥的，对用户其实不太好

            val src = File(it.path)
            val path = cacheDir.absolutePath + File.separator + src.name
            val dest = File(path)
            src.copyTo(dest, true)

            var asset = PickedAsset(path, it.width, it.height, dest.length(), it.type == AssetType.VIDEO)

            if (!isRawChecked) {
                asset = configuration.compressPhoto(asset)
            }

            result.add(asset)

        }

        intent.putParcelableArrayListExtra("photoList", result)

        setResult(Activity.RESULT_OK, intent)

        finish()

    }

}