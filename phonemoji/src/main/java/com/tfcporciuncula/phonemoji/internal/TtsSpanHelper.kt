package com.tfcporciuncula.phonemoji.internal

import android.telephony.PhoneNumberUtils
import android.text.Spannable
import android.text.style.TtsSpan
import android.text.style.TtsSpan.TelephoneBuilder

/**
 * We have our own [android.telephony.PhoneNumberUtils.addTtsSpan] implementation that uses a
 * [io.michaelrocks.libphonenumber.android.PhoneNumberUtil] instead of a
 * [com.android.i18n.phonenumbers.PhoneNumberUtil]. Since [android.telephony.PhoneNumberUtils.addTtsSpan]
 * was introduced in API level 23, this also allows us to support API level 21.
 *
 * https://cs.android.com/android/platform/superproject/+/master:frameworks/base/telephony/java/android/telephony/PhoneNumberUtils.java;l=2592-2642;drc=master
 */
internal object TtsSpanHelper {

  private val phoneNumberUtil = PhoneNumberUtilInstanceProvider.get()

  fun addTtsSpan(s: Spannable, start: Int, endExclusive: Int) = s.setSpan(
    createTtsSpan(s.subSequence(start, endExclusive).toString()),
    start,
    endExclusive,
    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
  )

  private fun createTtsSpan(phoneNumberString: String): TtsSpan {
    val phoneNumber = runCatching { phoneNumberUtil.parse(phoneNumberString, null) }.getOrNull()
    return TelephoneBuilder().apply {
      phoneNumber?.let {
        if (it.hasCountryCode()) setCountryCode(it.countryCode.toString())
        setNumberParts(it.nationalNumber.toString())
      } ?: setNumberParts(splitAtNonNumerics(phoneNumberString))
    }.build()
  }

  private fun splitAtNonNumerics(number: CharSequence) = StringBuilder(number.length).apply {
    number.asIterable().forEach { append(if (PhoneNumberUtils.is12Key(it)) it else " ") }
  }.toString().replace(" +".toRegex(), " ").trim()
}
