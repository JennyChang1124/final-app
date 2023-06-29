package com.example.strokepatientsvoicerecoveryapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.strokepatientsvoicerecoveryapp.databinding.MainoptionBinding

class MainoptionActivity : AppCompatActivity() {
    private lateinit var binding: MainoptionBinding
    private lateinit var username: String
    private lateinit var sp1Selection: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainoptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        username = intent.getStringExtra("username") ?: ""
        sp1Selection = intent.getStringExtra("sp1Selection") ?: ""

        //當按下類型選擇鍵就會跳轉到類型選擇畫面
        binding.btnItemPracrice.setOnClickListener {
            navigateToNextPage(itemPracriceActivity::class.java)
        }

        //當按下加強練習鍵就會跳轉到加強練習畫面
        binding.btnChooseType.setOnClickListener {
            navigateToNextPage(chooseTypeActivity::class.java)
        }
    }

    private fun navigateToNextPage(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        intent.putExtra("username", username)
        intent.putExtra("sp1Selection", sp1Selection)
        startActivity(intent)
    }
}