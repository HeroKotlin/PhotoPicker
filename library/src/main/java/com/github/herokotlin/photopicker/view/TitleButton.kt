package com.github.herokotlin.photopicker.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.github.herokotlin.photopicker.R
import kotlinx.android.synthetic.main.photo_picker_title_button.view.*
import android.view.animation.RotateAnimation
import android.view.animation.Animation

class TitleButton: LinearLayout {

    var title = ""

        set(value) {

            if (field == value) {
                return
            }

            field = value

            titleView.text = value

        }

    var checked = false

        set(value) {

            if (field == value) {
                return
            }

            field = value

            var from = 0f
            var to = -180f

            if (!checked) {
                from = -180f
                to = 0f
            }

            rotateAnimation?.cancel()

            val animation = RotateAnimation(from, to, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
            animation.duration = 300
            animation.repeatCount = 0
            animation.fillAfter = true
            arrowView.startAnimation(animation)

            rotateAnimation = animation

        }

    private var rotateAnimation: RotateAnimation? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    private fun init() {

        LayoutInflater.from(context).inflate(R.layout.photo_picker_title_button, this)

    }

}