package com.github.herokotlin.photopicker.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.github.herokotlin.photopicker.R
import kotlinx.android.synthetic.main.photo_picker_select_button.view.*

internal class SelectButton: RelativeLayout {

    var countable = false

    var checked = false

        set(value) {

            if (field == value) {
                return
            }

            field = value

            imageView.setImageResource(
                if (value) {
                    if (countable) {
                        R.drawable.photo_picker_select_button_checked_countable
                    }
                    else {
                        R.drawable.photo_picker_select_button_checked
                    }
                }
                else {
                    R.drawable.photo_picker_select_button_unchecked
                }
            )

        }

    var order = 0

        set(value) {

            if (field == value) {
                return
            }

            if (value > 0) {
                if (field <= 0) {
                    titleView.visibility = View.VISIBLE
                }
                titleView.text = "$value"
            }
            else {
                if (field > 0) {
                    titleView.visibility = View.GONE
                }
            }

            field = value

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
        LayoutInflater.from(context).inflate(R.layout.photo_picker_select_button, this)
    }

}