package com.tfcporciuncula.phonemoji

import android.text.Editable
import android.text.TextWatcher
import com.tfcporciuncula.phonemoji.internal.PhoneNumberUtilInstanceProvider

object PhonemojiHelper {

  const val EMOJI_COLOR = 0xff000000.toInt()

  private const val UNKNOWN_REGION = "ZZ" // Unfortunately that's private within PhoneNumberUtil.

  private val phoneNumberUtil = PhoneNumberUtilInstanceProvider.get()

  /**
   * Watches the input of [PhonemojiTextInputEditText] and invokes `onCountryChange` whenever the phone number's country
   * changes.
   *
   * @param editText The [PhonemojiTextInputEditText] to be watched.
   * @param onCountryChanged The lambda that will be invoked with a flag emoji `String` every time the country changes.
   */
  fun watchPhoneNumber(editText: PhonemojiTextInputEditText, onCountryChanged: (String) -> Unit) {
    var currentCountryCode = editText.initialCountryCode
    runCatching {
      currentCountryCode = phoneNumberUtil.parse(editText.text, null).countryCode
    }

    onCountryChanged(regionCodeToEmoji(phoneNumberUtil.getRegionCodeForCountryCode(currentCountryCode)))

    editText.addTextChangedListener(
      object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
          if (s.contains(" ")) { // First space is added when country code is identified.
            // If we can't get the country code between '+' (thus index 1) and the first space, we give up.
            val countryCode = runCatching { s.substring(1, s.indexOf(" ")).toInt() }.getOrNull() ?: return

            // If it's the same as the current one, no need to bother.
            if (currentCountryCode == countryCode) return

            // Otherwise, we get the region code for the country code, so we can translate it to a flag emoji.
            val regionCode = phoneNumberUtil.getRegionCodeForCountryCode(countryCode)

            // If we can't get a valid region code, we give up.
            if (regionCode == UNKNOWN_REGION) return

            currentCountryCode = countryCode
            onCountryChanged(regionCodeToEmoji(regionCode))
          }
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) = Unit
      }
    )
  }

  // https://stackoverflow.com/a/35849652/2352699
  private fun regionCodeToEmoji(regionCode: String): String {
    val firstLetter = Character.codePointAt(regionCode, 0) - 0x41 + 0x1F1E6
    val secondLetter = Character.codePointAt(regionCode, 1) - 0x41 + 0x1F1E6
    return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
  }
}
