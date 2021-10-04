package com.fredporciuncula.phonemoji.tests

import android.content.Context
import android.telephony.TelephonyManager
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.material.textfield.TextInputLayout
import com.google.common.truth.Truth.assertThat
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import org.hamcrest.CoreMatchers.containsString
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class PhonemojiTest {

  private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
  private val phoneNumberUtil = PhoneNumberUtil.createInstance(context)

  private val editText get() = onView(withId(R.id.plainEditText))

  @Test fun phonemojiTests() {
    val scenario = launchActivity<TestActivity>()

    verifyPlainEditTextStartsWithNetworkCountryCode()
    verifyInputGetsFormatted()
    verifyPlusSignCannotBeRemoved()
    verifyInitialRegionCodeAttribute()
    verifyInitialCountryCodeAttribute()
    verifyFlagText()
    verifyInitialInputIsFormatted()
    verifyTextInputLayoutIconPresence(scenario)
  }

  private fun verifyPlainEditTextStartsWithNetworkCountryCode() {
    editText.check(matches(withText(containsString("+${networkCountryCode()}"))))
  }

  private fun verifyInputGetsFormatted() {
    editText.perform(clearText())
    editText.perform(typeText("558433422316"))
    editText.check(matches(withText("+55 84 3342-2316")))

    editText.perform(clearText())
    editText.perform(replaceText("4917659971284"))
    editText.check(matches(withText("+49 176 59971284")))
  }

  private fun verifyPlusSignCannotBeRemoved() {
    editText.perform(replaceText(""))
    editText.check(matches(withText("+")))

    editText.perform(replaceText("999"))
    editText.check(matches(withText("+999")))

    editText.perform(clearText())
    editText.check(matches(withText("+")))
  }

  private fun verifyInitialRegionCodeAttribute() {
    onView(withId(R.id.editTextWithInitialRegionCodeDE)).check(matches(withText(containsString("+49"))))
  }

  private fun verifyInitialCountryCodeAttribute() {
    onView(withId(R.id.editTextWithInitialCountryCode55)).check(matches(withText(containsString("+55"))))
  }

  private fun verifyFlagText() {
    editText.perform(clearText())
    editText.perform(replaceText("49"))
    onView(withId(R.id.flagTextView)).check(matches(withText("🇩🇪")))
    editText.perform(clearText())
    editText.perform(replaceText("1222"))
    onView(withId(R.id.flagTextView)).check(matches(withText("🇺🇸")))
    editText.perform(clearText())
    onView(withId(R.id.flagTextView)).check(matches(withText("🇺🇸")))
    editText.perform(replaceText("5548"))
    onView(withId(R.id.flagTextView)).check(matches(withText("🇧🇷")))
  }

  private fun verifyInitialInputIsFormatted() {
    onView(withId(R.id.editTextWithInitialInput)).check(matches(withText("+49 176")))
    onView(withId(R.id.flagTextViewForEditTextWithInitialInput)).check(matches(withText("🇩🇪")))
  }

  private fun verifyTextInputLayoutIconPresence(scenario: ActivityScenario<TestActivity>) {
    scenario.onActivity {
      assertThat(it.findViewById<TextInputLayout>(R.id.textInputLayout).startIconDrawable).isNotNull()
      assertThat(it.findViewById<TextInputLayout>(R.id.textInputLayoutWithNoFlag).startIconDrawable).isNull()
    }
  }

  private fun networkCountryCode() = phoneNumberUtil.getCountryCodeForRegion(networkCountry())

  private fun networkCountry() =
    (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).networkCountryIso.toUpperCase(Locale.ROOT)
}
