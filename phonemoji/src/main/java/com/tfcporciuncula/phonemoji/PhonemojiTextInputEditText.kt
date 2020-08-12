package com.tfcporciuncula.phonemoji

import android.content.Context
import android.telephony.TelephonyManager
import android.text.InputType
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText
import com.tfcporciuncula.phonemoji.internal.PhoneNumberUtilInstanceProvider
import java.util.Locale

/**
 * Works together with the [InternationalPhoneNumberFormattingTextWatcher] to format the input as an international
 * phone number as it's entered. It also supports formatting programmatic inputs through [setInternationalPhoneNumber].
 *
 * It automatically sets the initial text to be '+XX', where XX is the country calling code for the network country
 * (from [TelephonyManager]) or for the country from the default [Locale] in case the network country doesn't resolve to
 * a valid country code. The initial country code can also be set with the attributes `phonemoji_initialRegionCode` and
 * `phonemoji_initialCountryCode` (the latter takes precedence) or through [setRegionCode] and [setCountryCode].
 */
class PhonemojiTextInputEditText : TextInputEditText {

  private val phoneNumberUtil = PhoneNumberUtilInstanceProvider.get()

  var initialCountryCode = -1
    private set

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
    setInitialCountryCode(attrs)
    addTextChangedListener(InternationalPhoneNumberFormattingTextWatcher())
    inputType = InputType.TYPE_CLASS_PHONE
  }

  private fun setInitialCountryCode(attrs: AttributeSet?) {
    attrs?.let {
      with(context.theme.obtainStyledAttributes(attrs, R.styleable.PhonemojiTextInputEditText, 0, 0)) {
        try {
          initialCountryCode = getInt(R.styleable.PhonemojiTextInputEditText_phonemoji_initialCountryCode, -1)
          val initialRegionCode = getString(R.styleable.PhonemojiTextInputEditText_phonemoji_initialRegionCode)
            ?.toUpperCase(Locale.ROOT)

          initialCountryCode = when {
            initialCountryCode != -1 -> initialCountryCode
            initialRegionCode != null -> phoneNumberUtil.getCountryCodeForRegion(initialRegionCode)
            else -> resolveInitialCountryCode()
          }
        } finally {
          recycle()
        }
      }
    }
    setCountryCode(initialCountryCode)
  }

  private fun resolveInitialCountryCode() =
    phoneNumberUtil.getCountryCodeForRegion(networkCountry()).takeIf { it != 0 }
      ?: phoneNumberUtil.getCountryCodeForRegion(Locale.getDefault().country)

  private fun networkCountry() =
    (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).networkCountryIso.toUpperCase(Locale.ROOT)

  /**
   * Clears the text and sets it as '+XX', where XX is the country code provided.
   *
   * @param countryCode The country calling code for a specific region (e.g. `1` for the US, `49` for Germany).
   */
  fun setCountryCode(countryCode: Int) {
    val text = "+$countryCode"
    setText(text)
    // Ensure selection starts after the country code when we get focus the first time.
    post { setSelection(text.length) }
  }

  /**
   * Clears the text and sets it as '+XX', where XX is the country calling code for the region code provided.
   *
   * @param regionCode The region code that matches a specific country calling code (e.g. `US` for 1, `DE` for 49).
   */
  fun setRegionCode(regionCode: String) {
    setCountryCode(phoneNumberUtil.getCountryCodeForRegion(regionCode.toUpperCase(Locale.ROOT)))
  }

  /**
   * Returns whether the current text is a valid international phone number or not.
   *
   * @return `true` if the current text is a valid international phone number, `false` otherwise.
   */
  fun isTextValidInternationalPhoneNumber() =
    runCatching { phoneNumberUtil.isValidNumber(phoneNumberUtil.parse(text, null)) }.getOrNull() == true

  /**
   * By default, any text set programmatically will also trigger [InternationalPhoneNumberFormattingTextWatcher] and
   * will get formatted. However, depending on the current input and current state of the watcher, formatting might've
   * been stopped. This makes sure that's not the case by clearing the text first to ensure the input will be formatted.
   *
   * @param phoneNumber International phone number to be formatted and set as the text.
   */
  fun setInternationalPhoneNumber(phoneNumber: String) {
    setText("")
    setText(phoneNumber)
    setSelection(text!!.length)
  }
}
