package com.example.strokepatientsvoicerecoveryapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class chooseTypeActivity: AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList: ArrayList<Dataclass>
    private lateinit var myAdapter: Adapterclass
    lateinit var imageList: Array<Int>
    lateinit var titleList: Array<String>
    private lateinit var username: String
    private lateinit var sp1Selection: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.choose_type)

        username = intent.getStringExtra("username") ?: ""
        sp1Selection = intent.getStringExtra("sp1Selection") ?: ""

        myAdapter = Adapterclass(ArrayList()) // 初始化myAdapter

        imageList= arrayOf(
            R.drawable.image1,
            R.drawable.image2,
            R.drawable.images3,
            R.drawable.images10,
            R.drawable.images6,
            R.drawable.images7,
            R.drawable.images9,
        )//圖片按照順序
        titleList= arrayOf(
            "節慶","食物","飲料","衣服","身體部位","心情","住所"
        )//標題按照順序
        recyclerView = findViewById(R.id.rv_Type)
        recyclerView.layoutManager = LinearLayoutManager(this)//LayoutManager 來指定 RecyclerView 排列的方式
        recyclerView.setHasFixedSize(true)

        dataList = arrayListOf<Dataclass>()
        getDate()
    }

    //獲得索引的資訊，有另創一個Dataclass
    private fun getDate() {
        for (i in imageList.indices) {
            val dataclass = Dataclass(imageList[i], titleList[i])
            dataList.add(dataclass)
        }

        myAdapter = Adapterclass(dataList)
        myAdapter.setOnItemClickListener { selectedTitle ->
            navigateToNextPage(SetTimeActivity::class.java, selectedTitle.dataTitle)
        }

        recyclerView.adapter = myAdapter
    }

    private fun navigateToNextPage(activityClass: Class<*>, selectedTitle: String) {
        val intent = Intent(this, activityClass)
        intent.putExtra("username", username)
        intent.putExtra("sp1Selection", sp1Selection)
        intent.putExtra("selectedTitle", selectedTitle)
        startActivity(intent)

    }
}



