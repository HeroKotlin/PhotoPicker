package com.github.herokotlin.photopicker.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.github.herokotlin.photopicker.databinding.PhotoPickerOriginalButtonBinding

internal class OriginalButton: LinearLayout {

    lateinit var binding: PhotoPickerOriginalButtonBinding

    var text = ""

        set(value) {

            if (field == value) {
                return
            }

            field = value

            binding.titleView.text = value

        }

    var image = 0

        set(value) {

            if (field == value) {
                return
            }

            field = value

            binding.imageView.setImageResource(value)

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
        binding = PhotoPickerOriginalButtonBinding.inflate(LayoutInflater.from(context), this, true)
    }

}