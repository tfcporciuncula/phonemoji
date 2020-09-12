package com.tfcporciuncula.phonemoji

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputLayout
import com.tfcporciuncula.phonemoji.internal.TextDrawable
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

/**
 * Works with a [PhonemojiTextInputEditText] child and renders an emoji flag as a start icon based on its input.
 *
 * The flag visibility and size can be set with the attributes `phonemoji_showFlag` and `phonemoji_flagSize`.
 */
class PhonemojiTextInputLayout : TextInputLayout {

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

  private var bounds: Rect? = null
  private var recalculateMethod: Method? = null
  private var collapsingTextHelper: Any? = null

  /**
   * Companion Object
   */
  companion object {
    const val COLLAPSING_HELPER = "collapsingTextHelper"
    const val COLLAPSED_BOUNDS = "collapsedBounds"
    const val RECALCULATE = "recalculate"
  }

  init {
    init2()
  }

  private fun init2() {
    try {
      //Search internal and private class over object called as variable
      val cthField = TextInputLayout::class.java.getDeclaredField(COLLAPSING_HELPER)
      cthField.isAccessible = true
      collapsingTextHelper = cthField.get(this)

      //Search in private class the other component to create a view
      val boundsField = collapsingTextHelper?.javaClass?.getDeclaredField(COLLAPSED_BOUNDS)
      boundsField?.isAccessible = true
      bounds = boundsField?.get(collapsingTextHelper) as Rect
      recalculateMethod = collapsingTextHelper?.javaClass?.getDeclaredMethod(RECALCULATE)

    } catch (e: NoSuchFieldException) {
      collapsingTextHelper = null
      bounds = null
      recalculateMethod = null
      e.printStackTrace()
    } catch (e: IllegalAccessException) {
      collapsingTextHelper = null
      bounds = null
      recalculateMethod = null
      e.printStackTrace()
    } catch (e: NoSuchMethodException) {
      collapsingTextHelper = null
      bounds = null
      recalculateMethod = null
      e.printStackTrace()
    }
  }

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    super.onLayout(changed, left, top, right, bottom)
    adjustBounds()
  }

  private fun adjustBounds() {
    if (collapsingTextHelper == null)
      return
    try {
      bounds?.left = editText?.left!! + editText?.paddingLeft!!
      recalculateMethod?.invoke(collapsingTextHelper)
    } catch (e: InvocationTargetException) {
      e.printStackTrace()
    } catch (e: IllegalAccessException) {
      e.printStackTrace()
    } catch (e: IllegalArgumentException) {
      e.printStackTrace()
    }

  }
}
