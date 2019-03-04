package com.github.herokotlin.photopicker.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.github.herokotlin.photopicker.R
import kotlinx.android.synthetic.main.photo_picker_title_button.view.*

internal class TitleButton: LinearLayout {

    var title = ""

        set(value) {

            if (field == value) {
                return
            }

            field = value

            titleView.text = value

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
        LayoutInflater.from(context).inflate(R.layout.photo_picker_title_button, this)
    }

}