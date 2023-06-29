package com.example.strokepatientsvoicerecoveryapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.strokepatientsvoicerecoveryapp.databinding.ActivityMainBinding
import com.example.strokepatientsvoicerecoveryapp.databinding.UserloginBinding
import com.google.firebase.database.*


class userloginActivity : AppCompatActivity() {

    private lateinit var binding : UserloginBinding
    private lateinit var dbRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UserloginBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setContentView(R.layout.userlogin)

        binding.btnUserLogin.setOnClickListener {
            searchUser()
        }

        binding.btnSignin.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java);
            startActivity(intent)
        }//當按下註冊鍵就會跳轉到使用者註冊畫面
    }

    // ========================function======================================
    private fun searchUser(){
        val username = binding.edusername.text.toString()
        val password = binding.edpassword.text.toString()

        if(username.isEmpty()) {
            binding.edusername.error = "記得填寫你的用戶名稱"
        }
        if(password.isEmpty()) {
            binding.edpassword.error = "記得填寫你的密碼"
        }

        dbRef = FirebaseDatabase.getInstance().getReference("Users")
        val checkUser = dbRef.orderByChild("username").equalTo(username)

        checkUser.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    binding.edusername.setError(null);
                    val passwordFromDB = snapshot.child(username).child("password").getValue(String::class.java)

                    if(passwordFromDB == password){
                        binding.edusername.setError(null);
                        Toast.makeText(this@userloginActivity, "登入成功", Toast.LENGTH_LONG).show()

                        // 用intent 傳username到設定介面
                        val usernameFromDB = snapshot.child(username).child("username").getValue(String::class.java)
                        val intent = Intent(this@userloginActivity, BasicinformationActivity::class.java)
                        intent.putExtra("username", usernameFromDB)
                        startActivity(intent)

                    }else{
                        Toast.makeText(this@userloginActivity, "密碼輸入錯誤", Toast.LENGTH_LONG).show()
                    }
                }else{
                    Toast.makeText(this@userloginActivity, "查無此用戶", Toast.LENGTH_LONG).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    companion object

}

//private lateinit var edusername : EditText
//private lateinit var edpassword : EditText
//private lateinit var btn_userLogin : Button
//private lateinit var btn_signin : Button


//        edusername = findViewById(R.id.edusername)
//        edpassword = findViewById(R.id.edpassword)