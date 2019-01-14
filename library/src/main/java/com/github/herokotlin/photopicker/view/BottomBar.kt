package com.github.herokotlin.photopicker.view

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.github.herokotlin.photopicker.R
import kotlinx.android.synthetic.main.photo_picker_bottom_bar.view.*

class BottomBar: RelativeLayout {

    var isFullChecked = false

        set(value) {

            if (field == value) {
                return
            }

            field = value

            fullButton.image = if (value) {
                R.drawable.photo_picker_full_button_checked
            }
            else {
                R.drawable.photo_picker_full_button_unchecked
            }

        }

    var selectedCount = -1

        set(value) {

            if (field == value) {
                return
            }

            field = value

            if (value > 0) {
                submitButton.isEnabled = true
                submitButton.alpha = 1f
                submitButton.text = "$submitButtonTitle($selectedCount)"
            }
            else {
                submitButton.isEnabled = false
                submitButton.alpha = 0.5f
                submitButton.text = submitButtonTitle
            }

        }

    private val submitButtonTitle: String by lazy {
        resources.getString(R.string.photo_picker_submit_button_title)
    }

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

        LayoutInflater.from(context).inflate(R.layout.photo_picker_bottom_bar, this)

        fullButton.setOnClickListener {
            isFullChecked = !isFullChecked
        }

    }

}