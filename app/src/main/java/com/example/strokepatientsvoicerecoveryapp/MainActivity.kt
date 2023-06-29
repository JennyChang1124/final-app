package com.example.strokepatientsvoicerecoveryapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.strokepatientsvoicerecoveryapp.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this);

        binding.btnLogin.setOnClickListener {
            val   intent = Intent(this,userloginActivity::class.java);
            startActivity(intent)
        }//當按下登入鍵就會跳轉到使用者登入畫面
        binding.btnSignin.setOnClickListener {
            val   intent = Intent(this,RegisterActivity::class.java);
            startActivity(intent)
        }//當按下註冊鍵就會跳轉到使用者註冊畫面
    }
}


//    private lateinit var btn_login : Button
//    private lateinit var btn_signin : Button