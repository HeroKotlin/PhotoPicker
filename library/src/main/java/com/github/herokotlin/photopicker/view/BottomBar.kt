package com.github.herokotlin.photopicker.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.github.herokotlin.photopicker.PhotoPickerConfiguration
import com.github.herokotlin.photopicker.R
import com.github.herokotlin.photopicker.databinding.PhotoPickerBottomBarBinding

internal class BottomBar: RelativeLayout {

    lateinit var binding: PhotoPickerBottomBarBinding
    lateinit var configuration: PhotoPickerConfiguration

    var isOriginalChecked = false

        set(value) {

            if (field == value) {
                return
            }

            field = value

            binding.originalButton.image = if (value) {
                R.drawable.photo_picker_original_button_checked
            }
            else {
                R.drawable.photo_picker_original_button_unchecked
            }

        }

    var selectedCount = -1

        set(value) {

            if (field == value) {
                return
            }

            field = value

            var title = submitButtonTitle
            if (value > 0) {
                binding.submitButton.isEnabled = true
                binding.submitButton.alpha = 1f
                if (configuration.maxSelectCount > 1) {
                    title = "$submitButtonTitle($value/${configuration.maxSelectCount})"
                }
            }
            else {
                binding.submitButton.isEnabled = false
                binding.submitButton.alpha = 0.5f
            }

            binding.submitButton.text = title
        }

    private val submitButtonTitle: String by lazy {
        if (configuration.submitButtonTitle.isEmpty()) {
            resources.getString(R.string.photo_picker_submit_button_title)
        }
        else {
            configuration.submitButtonTitle
        }
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

        binding = PhotoPickerBottomBarBinding.inflate(LayoutInflater.from(context), this, true)

        binding.originalButton.setOnClickListener {
            isOriginalChecked = !isOriginalChecked
        }

    }

}