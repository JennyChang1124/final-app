package com.example.strokepatientsvoicerecoveryapp

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class ChoosesentenceActivity: AppCompatActivity() {
    private lateinit var btn_next: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.choose_sentence)
    }
}