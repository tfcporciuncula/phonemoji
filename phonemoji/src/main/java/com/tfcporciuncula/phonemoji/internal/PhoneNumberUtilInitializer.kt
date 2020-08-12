package com.tfcporciuncula.phonemoji.internal

import android.content.Context
import androidx.startup.Initializer
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil

internal class PhoneNumberUtilInitializer : Initializer<PhoneNumberUtil> {

  override fun create(context: Context): PhoneNumberUtil =
    PhoneNumberUtil.createInstance(context).also { PhoneNumberUtilInstanceProvider.set(it) }

  override fun dependencies() = emptyList<Class<Initializer<*>>>()
}
