package com.fredporciuncula.phonemoji.internal

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import com.fredporciuncula.phonemoji.PhonemojiHelper

/**
 * A [Drawable] implementation that draws text so we can render the flag emoji as an icon drawable.
 */
class TextDrawable(private val text: String, private val size: Float) : Drawable() {

  private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = PhonemojiHelper.EMOJI_COLOR
    textSize = size
    textAlign = Paint.Align.CENTER
    style = Paint.Style.FILL
  }

  override fun draw(canvas: Canvas) = canvas.drawText(
    text,
    0,
    text.length,
    bounds.centerX().toFloat(),
    bounds.centerY().toFloat() - ((paint.descent() + paint.ascent()) / 2),
    paint
  )

  override fun setAlpha(alpha: Int) {
    paint.alpha = alpha
  }

  override fun setColorFilter(colorFilter: ColorFilter?) {
    paint.colorFilter = colorFilter
  }

  @Deprecated("Deprecated in Java",
    ReplaceWith("PixelFormat.TRANSLUCENT", "android.graphics.PixelFormat")
  )
  override fun getOpacity() = PixelFormat.TRANSLUCENT
}
