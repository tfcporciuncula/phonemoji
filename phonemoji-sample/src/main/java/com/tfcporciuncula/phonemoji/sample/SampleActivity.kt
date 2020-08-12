package com.tfcporciuncula.phonemoji.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tfcporciuncula.phonemoji.sample.databinding.SampleActivityBinding

class SampleActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val binding = SampleActivityBinding.inflate(layoutInflater)
    setContentView(binding.root)
  }
}
