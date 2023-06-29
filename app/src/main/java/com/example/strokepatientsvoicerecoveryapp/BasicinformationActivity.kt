package com.example.strokepatientsvoicerecoveryapp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.strokepatientsvoicerecoveryapp.databinding.BasicInformationBinding
import com.google.firebase.database.*


class BasicinformationActivity : AppCompatActivity() {

    private lateinit var binding : BasicInformationBinding
    private lateinit var dbRef : DatabaseReference
    private lateinit var checkUserDatabase: Query
    private lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BasicInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 接收上一頁傳來的user
        getPassedUser()

        //當按下送出鍵就會跳轉到主題選擇畫面
        binding.send.setOnClickListener {
            saveUserData()
        }

        // Age那邊Date格式限制
        setupDateInput()

        // 選到病症程度 給建議
        binding.sp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                spSuggestion()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 什麼都沒選
            }
        }
    }
    // ========================function======================================
    // 上一個頁面傳過來的帳號
    // 現在登入的帳號
    private fun getPassedUser() {
        val intent = getIntent()
        username = intent.getStringExtra("username") ?: ""

        dbRef = FirebaseDatabase.getInstance().getReference("Users")
        checkUserDatabase = dbRef.orderByChild("username").equalTo(username)

        checkUserDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val nameFromDB = snapshot.child("username").child("name").getValue(String::class.java)
                    // 有資料就放出來
                    // 在這裡處理從數據庫中獲取的名稱 nameFromDB
                    val userSnapshot = snapshot.children.first()
                    val realnameFromDB = userSnapshot.child("realName").getValue(String::class.java)
                    val ageFromDB = userSnapshot.child("age").getValue(String::class.java)
                    val addrFromDB = userSnapshot.child("address").getValue(String::class.java)
                    val memberFromDB = userSnapshot.child("member").getValue(String::class.java)
                    val membernameFromDB = userSnapshot.child("memberName").getValue(String::class.java)
                    val spSelectionFromDB = userSnapshot.child("spSelection").getValue(String::class.java)
                    val sp1SelectionFromDB = userSnapshot.child("sp1Selection").getValue(String::class.java)

                    // 設EditText的文字
                    binding.textUsername.editText?.setText(realnameFromDB)
                    binding.textAge.editText?.setText(ageFromDB)
                    binding.textaddr.editText?.setText(addrFromDB)
                    binding.textmember.editText?.setText(memberFromDB)
                    binding.textmembername.editText?.setText(membernameFromDB)
                    val spSelectionIndex = getIndexFromSpinner(binding.sp, spSelectionFromDB)
                    binding.sp.setSelection(spSelectionIndex)

                    val sp1SelectionIndex = getIndexFromSpinner(binding.sp1, sp1SelectionFromDB)
                    binding.sp1.setSelection(sp1SelectionIndex)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // 如果需要，處理 onCancelled 事件
            }
        })
        // 測試用 把名字改成username
        // binding.editAge.setText(username)
    }

    // 儲存帳號資料到FireBase
    private fun saveUserData() : Boolean{
        val realName = binding.textUsername.editText?.text.toString()
        val age = binding.textAge.editText?.text.toString()
        val address = binding.textaddr.editText?.text.toString()
        val member = binding.textmember.editText?.text.toString()
        val memberName = binding.textmembername.editText?.text.toString()

        val spSelection = binding.sp.selectedItem.toString()
        val sp1Selection = binding.sp1.selectedItem.toString()

        if(realName.isEmpty()) {
            binding.textUsername.error = "記得填寫你的真實名稱"
            Toast.makeText(this, "記得填寫你的真實名稱", Toast.LENGTH_SHORT).show()
            return false
        }
        else if(age.isEmpty()) {
            binding.textAge.error = "記得填寫你的年齡"
            Toast.makeText(this, "記得填寫你的年齡", Toast.LENGTH_SHORT).show()
            return false
        }
        else if(address.isEmpty()) {
            binding.textaddr.error = "記得填寫你的地址"
            Toast.makeText(this, "記得填寫你的地址", Toast.LENGTH_SHORT).show()
            return false
        }
        else if(member.isEmpty()) {
            binding.textmember.error = "記得填寫你的家庭成員稱謂"
            Toast.makeText(this, "記得填寫你的家庭成員稱謂", Toast.LENGTH_SHORT).show()
            return false
        }
        else if(memberName.isEmpty()) {
            binding.textmembername.error = "記得填寫你的家庭成員姓名"
            Toast.makeText(this, "記得填寫你的家庭成員姓名", Toast.LENGTH_SHORT).show()
            return false
        }
        else if(spSelection == "請選擇症狀程度名稱") {
            binding.sp.requestFocus()
            Toast.makeText(this, "請選擇一個症狀程度名稱", Toast.LENGTH_SHORT).show()
            return false
        }
        else if(sp1Selection == "請選擇難度") {
            binding.sp1.requestFocus()
            Toast.makeText(this, "請選擇一個難度", Toast.LENGTH_SHORT).show()
            return false
        }
        else{
            val userData = HashMap<String, Any>()
            userData["realName"] = realName
            userData["age"] = age
            userData["address"] = address
            userData["member"] = member
            userData["memberName"] = memberName
            userData["spSelection"] = spSelection
            userData["sp1Selection"] = sp1Selection

            val dbRef = FirebaseDatabase.getInstance().getReference("Users")
            val userRef = dbRef.child(username) // 這裡使用特定的用戶名
            userRef.updateChildren(userData)
                .addOnSuccessListener {
                    // 更新成功
                    Toast.makeText(this@BasicinformationActivity, "資料保存成功", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@BasicinformationActivity, MainoptionActivity::class.java)
                    intent.putExtra("username", username)
                    intent.putExtra("sp1Selection", sp1Selection)
                    startActivity(intent)
                    // passUserData(username)
                }
                .addOnFailureListener { e ->
                    // 更新失败
                    Toast.makeText(this@BasicinformationActivity, "資料保存失敗，請再試一次", Toast.LENGTH_SHORT).show()
                }
            return true
        }
    }

    private fun spSuggestion() {
        val suggestions = resources.getStringArray(R.array.suggestions)
        val suggestionIndex = binding.sp.selectedItemPosition
        if (suggestionIndex != AdapterView.INVALID_POSITION) {
            val suggestionText = suggestions[suggestionIndex]
            binding.suggestion.text = suggestionText
        }
    }

    private fun setupDateInput() {
        val dateTextWatcher = object : TextWatcher {
            private var currentText = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 在文字改變前
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 在文字改變時
            }

            override fun afterTextChanged(s: Editable?) {
                s?.let { editable ->
                    val inputText = editable.toString()
                    val formattedText = formatDateString(inputText)
                    if (formattedText != inputText) {
                        editable.replace(0, editable.length, formattedText)
                    }
                    currentText = formattedText
                }
            }

            private fun formatDateString(inputText: String): String {
                val digitsOnly = inputText.replace("[^\\d]".toRegex(), "")

                val formattedText = buildString {
                    for (i in digitsOnly.indices) {
                        append(digitsOnly[i])
                        if (i == 3 || i == 5) {
                            append("/")
                        }
                    }
                }

                return formattedText
            }
        }

        binding.textAge.editText?.addTextChangedListener(dateTextWatcher)
    }

    private fun getIndexFromSpinner(spinner: Spinner, selection: String?): Int {
        val adapter = spinner.adapter as ArrayAdapter<String>
        return adapter.getPosition(selection)
    }
}
/*
    // 帳號資料傳到下一頁
    private fun passUserData(username: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Users")
        val userRef = dbRef.child(username)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val sp1Selection = snapshot.child("sp1Selection").getValue(String::class.java)

                    val intent = Intent(this@BasicinformationActivity, MainoptionActivity::class.java)
                    intent.putExtra("username", username)
                    intent.putExtra("sp1Selection", sp1Selection)
                    startActivity(intent)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // 处理 onCancelled 事件
            }
        })
    }

 */