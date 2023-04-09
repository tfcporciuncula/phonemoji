package com.fredporciuncula.phonemoji.internal

import io.michaelrocks.libphonenumber.android.PhoneNumberUtil

internal object PhoneNumberUtilInstanceProvider {

  private var phoneNumberUtil: PhoneNumberUtil? = null

  fun set(phoneNumberUtil: PhoneNumberUtil) {
    this.phoneNumberUtil = phoneNumberUtil
  }

  fun get() = checkNotNull(phoneNumberUtil) { "PhoneNumberUtil instance hasn't been initialized." }
}
