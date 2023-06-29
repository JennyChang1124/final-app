package com.example.strokepatientsvoicerecoveryapp

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SpeechActivity: AppCompatActivity() {
    private lateinit var btn_next1: Button
    private lateinit var tv_speech: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.speech)
    }
}