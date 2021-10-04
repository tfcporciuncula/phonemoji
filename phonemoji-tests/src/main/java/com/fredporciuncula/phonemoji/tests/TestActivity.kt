package com.fredporciuncula.phonemoji.tests

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fredporciuncula.phonemoji.PhonemojiTextInputEditText

class TestActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.test_activity)
    findViewById<PhonemojiTextInputEditText>(R.id.editTextWithInitialInput).setInternationalPhoneNumber("49176")
  }
}
