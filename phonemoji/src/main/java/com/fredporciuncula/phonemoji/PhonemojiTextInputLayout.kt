package com.fredporciuncula.phonemoji

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputLayout
import com.fredporciuncula.phonemoji.internal.TextDrawable

/**
 * Works with a [PhonemojiTextInputEditText] child and renders an emoji flag as a start icon based on its input.
 *
 * The flag visibility and size can be set with the attributes `phonemoji_showFlag` and `phonemoji_flagSize`.
 */
open class PhonemojiTextInputLayout : TextInputLayout {

  private var showFlag = true
  private var flagSize = 0f

  constructor(context: Context) : super(context) {
    init(null)
  }

  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
    init(attrs)
  }

  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    init(attrs)
  }

  private fun init(attrs: AttributeSet?) {
    attrs?.let {
      with(context.theme.obtainStyledAttributes(attrs, R.styleable.PhonemojiTextInputLayout, 0, 0)) {
        try {
          showFlag = getBoolean(R.styleable.PhonemojiTextInputLayout_phonemoji_showFlag, true)
          val flagSizeFromAttr = getDimension(R.styleable.PhonemojiTextInputLayout_phonemoji_flagSize, 0f)
          flagSize = flagSizeFromAttr.takeIf { it > 0 } ?: resources.getDimension(R.dimen.phonemoji_default_flag_size)
        } finally {
          recycle()
        }
      }
    }
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    if (showFlag) watchPhoneNumber()
  }

  private fun watchPhoneNumber() {
    val phonemojiEditText = editText as? PhonemojiTextInputEditText
    checkNotNull(phonemojiEditText) { "PhonemojiTextInputLayout requires a PhonemojiTextInputEditText child" }
    PhonemojiHelper.watchPhoneNumber(phonemojiEditText) { startIconDrawable = TextDrawable(it, flagSize) }
  }
}
