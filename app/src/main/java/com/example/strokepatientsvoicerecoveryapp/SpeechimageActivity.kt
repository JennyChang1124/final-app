package com.example.strokepatientsvoicerecoveryapp

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SpeechimageActivity: AppCompatActivity() {
    private lateinit var btn_next2: Button
    private lateinit var tv_speech1: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.speech_image)
    }
}