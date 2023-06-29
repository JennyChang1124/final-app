package com.example.strokepatientsvoicerecoveryapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.strokepatientsvoicerecoveryapp.databinding.SetTimeBinding

class SetTimeActivity : AppCompatActivity() {
    private lateinit var binding: SetTimeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SetTimeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //當按下確定鍵就會跳轉到倒數畫面
        binding.sure.setOnClickListener {
            val timeValue = binding.time.text.toString()
            if (timeValue.isNotEmpty()) {
                val time = timeValue.toInt()
                if (time in 1..99) {
                    val intent = Intent(this, CountdowntimerActivity::class.java)
                    intent.putExtra("username", intent.getStringExtra("username"))
                    intent.putExtra("sp1Selection", intent.getStringExtra("sp1Selection"))
                    startActivity(intent)
                    finish()
                } else {
                    // 不在範圍内
                    binding.time.error = "請輸入合理的數字"
                    Toast.makeText(this, "請輸入合理的數字", Toast.LENGTH_SHORT).show()
                }
            } else {
                // 沒有輸入
                binding.time.error = ""
                Toast.makeText(this, "請輸入數字", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

