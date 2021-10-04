package com.fredporciuncula.phonemoji.sample

import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.fredporciuncula.phonemoji.sample.databinding.SampleActivityBinding

class SampleActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val binding = SampleActivityBinding.inflate(layoutInflater)
    setContentView(binding.root)
    showKeyboard(binding.firstTextInputEditText)
  }

  private fun showKeyboard(textInputEditText: TextInputEditText) {
    textInputEditText.requestFocus()
    val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(textInputEditText, InputMethodManager.SHOW_IMPLICIT)
  }
}
