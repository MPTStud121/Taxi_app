package com.example.taxi_app.models


data class UserModel(
    val id: String = "",
    val role: String = "",
    val phone_user: String = "",
    var name_user: String = "",
    var email_user: String = "",
    var pay_method: String = "",
    var image_user: String = "empty",
)