package com.example.strokepatientsvoicerecoveryapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.strokepatientsvoicerecoveryapp.databinding.RegisterloginBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


// 註冊畫面
class RegisterActivity : AppCompatActivity() {

    // firebase
    private lateinit var dbRef : DatabaseReference
    private lateinit var binding: RegisterloginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegisterloginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbRef = FirebaseDatabase.getInstance().getReference("Users")

        binding.btnCreateUser.setOnClickListener{
            saveUserData()
        }

        //當按下登入鍵就會跳轉到使用者登入畫面
        binding.btnLogin.setOnClickListener {
            val intent = Intent(this,userloginActivity::class.java);
            startActivity(intent)
        }
    }

    // ========================function======================================
    private fun saveUserData() {
        val username = binding.tUsername.text.toString()
        val password = binding.tPassword.text.toString()
        val ComfirmPassword = binding.tComfirmPassword.text.toString()

        if(username.isEmpty()) {
            binding.tUsername.error = "記得填寫你的用戶名稱"
            Toast.makeText(this, "記得填寫你的用戶名稱", Toast.LENGTH_SHORT).show()
        }
        else if(password.isEmpty()) {
            binding.tPassword.error = "記得填寫你的密碼"
            Toast.makeText(this, "記得填寫你的密碼", Toast.LENGTH_SHORT).show()
        }
        else if(ComfirmPassword != password){
            binding.tPassword.error = "確認密碼與你的密碼不一致"
            Toast.makeText(this, "確認密碼與你的密碼不一致", Toast.LENGTH_SHORT).show()
        }
        else{
            // 註冊 存進資料庫
            val UserId = dbRef.push().key!!
            val User = UserModel(UserId, username, password)

            dbRef.child(username).setValue(User)
                .addOnCompleteListener {
                    Toast.makeText(this, "註冊成功!", Toast.LENGTH_LONG).show()
                    binding.tUsername.text.clear()
                    binding.tPassword.text.clear()

                    // 註冊完直接跳到登入畫面
                    val intent = Intent(this,userloginActivity::class.java);
                    startActivity(intent)
                }.addOnFailureListener {
                    Toast.makeText(this, "註冊失敗", Toast.LENGTH_LONG).show()
                }
        }
    }

}