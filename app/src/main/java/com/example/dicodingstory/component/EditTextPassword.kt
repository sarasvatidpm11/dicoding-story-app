package com.example.dicodingstory.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.dicodingstory.R

class EditTextPassword @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs), View.OnTouchListener {

    private var eyeOpenButtonImage: Drawable
    private var eyeClosedButtonImage: Drawable
    private var isPasswordVisible: Boolean = false

    init {
        eyeOpenButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_eye_on) as Drawable
        eyeClosedButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_eye_off) as Drawable

        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length < 8) {
                    setError("The password must not be less than 8 characters", null)
                } else {
                    error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        hidePassword()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = "Password"
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        textSize = 16f
    }

    private fun showPassword() {
        transformationMethod = HideReturnsTransformationMethod.getInstance()
        setButtonDrawables(endOfTheText = eyeClosedButtonImage)
        isPasswordVisible = true
    }

    private fun hidePassword() {
        transformationMethod = PasswordTransformationMethod.getInstance()
        setButtonDrawables(endOfTheText = eyeOpenButtonImage)
        isPasswordVisible = false
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (compoundDrawables[2] != null) {
            val clearButtonStart: Float
            val clearButtonEnd: Float
            var isClearButtonClicked = false
            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                clearButtonEnd = (eyeClosedButtonImage.intrinsicWidth + paddingStart).toFloat()
                if (event != null) {
                    when {
                        event.x < clearButtonEnd -> isClearButtonClicked = true
                    }
                }
            } else {
                clearButtonStart = (width - paddingEnd - eyeClosedButtonImage.intrinsicWidth).toFloat()
                if (event != null) {
                    when {
                        event.x > clearButtonStart -> isClearButtonClicked = true
                    }
                }
            }
            if (isClearButtonClicked) {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        showPassword()
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        hidePassword()
                        return true
                    }
                    else -> return false
                }
            } else return false
        }
        return false
    }
}
