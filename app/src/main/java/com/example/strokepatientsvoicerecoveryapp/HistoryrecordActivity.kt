package com.example.strokepatientsvoicerecoveryapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class HistoryrecordActivity : AppCompatActivity() {

    private lateinit var btn_see : Button
    private lateinit var btn_see1 : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.history_record_detail)

        btn_see.setOnClickListener {
            val   intent = Intent(this,QuestiondetailActivity::class.java);
            startActivity(intent)
        }//當按下查看鍵就會跳轉到題目紀錄畫面
        btn_see1.setOnClickListener {
            val   intent = Intent(this,QuestiondetailActivity::class.java);
            startActivity(intent)
        }//當按下查看鍵就會跳轉到題目紀錄畫面
    }
}
