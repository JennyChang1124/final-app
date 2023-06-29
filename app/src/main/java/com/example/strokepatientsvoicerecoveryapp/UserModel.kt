package com.example.strokepatientsvoicerecoveryapp

// 註冊登入 使用者
data class UserModel(
    var userId : String? = null,
    var username : String? = null,
    var password : String? = null,
    var name : String? = null,
    var birth : String? = null,
    var phoneNum : String? = null,
    var address : String? = null,
    var member : String? = null,
    var memberName : String? = null
)
