package com.github.herokotlin.photopicker.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.github.herokotlin.photopicker.databinding.PhotoPickerTopBarBinding

internal class TopBar: RelativeLayout {

    lateinit var binding: PhotoPickerTopBarBinding

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
        binding = PhotoPickerTopBarBinding.inflate(LayoutInflater.from(context), this, true)
    }

}