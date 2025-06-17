package com.github.herokotlin.photopicker.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.github.herokotlin.photopicker.R
import com.github.herokotlin.photopicker.databinding.PhotoPickerSelectButtonBinding

internal class SelectButton: RelativeLayout {

    lateinit var binding: PhotoPickerSelectButtonBinding

    var countable = false

    var checked = false

        set(value) {

            if (field == value) {
                return
            }

            field = value

            binding.imageView.setImageResource(
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
                    binding.titleView.visibility = View.VISIBLE
                }
                binding.titleView.text = "$value"
            }
            else {
                if (field > 0) {
                    binding.titleView.visibility = View.GONE
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
        binding = PhotoPickerSelectButtonBinding.inflate(LayoutInflater.from(context), this, true)
    }

}