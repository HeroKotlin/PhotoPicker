package com.github.herokotlin.photopicker.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.github.herokotlin.photopicker.databinding.PhotoPickerTitleButtonBinding

internal class TitleButton: LinearLayout {

    lateinit var binding: PhotoPickerTitleButtonBinding

    var title = ""

        set(value) {

            if (field == value) {
                return
            }

            field = value

            binding.titleView.text = value

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
        binding = PhotoPickerTitleButtonBinding.inflate(LayoutInflater.from(context), this, true)
    }

}