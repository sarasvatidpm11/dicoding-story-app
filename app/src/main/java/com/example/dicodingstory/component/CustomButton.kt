package com.example.dicodingstory.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.dicodingstory.R

class CustomButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatButton(context, attrs) {
    private var txtColor: Int = 0
    private var enabledBackground: Drawable
    private var disabledBackground: Drawable

    init {
        txtColor = ContextCompat.getColor(context, android.R.color.background_light)
        enabledBackground = ContextCompat.getDrawable(context, R.drawable.bg_button) as Drawable
        disabledBackground = ContextCompat.getDrawable(context, R.drawable.bg_button_disable) as Drawable
        updateBackground()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        setTextColor(txtColor)
        textSize = 14f
        gravity = Gravity.CENTER
    }

    private fun updateBackground() {
        background = if (isEnabled) enabledBackground else disabledBackground
    }

    fun setEnabledState(email: String, password: String, name: String? = null, isRegister: Boolean = false) {
        isEnabled = if (isRegister) {
            email.isNotEmpty() && password.isNotEmpty() && !name.isNullOrEmpty()
        } else {
            email.isNotEmpty() && password.isNotEmpty()
        }
        updateBackground()
    }
}
