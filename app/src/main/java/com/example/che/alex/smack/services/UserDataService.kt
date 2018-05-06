package com.example.che.alex.smack.services

import android.graphics.Color

object UserDataService {

    var id =""
    var avatarColor =""
    var avatarName =""
    var email =""
    var name =""

    fun logout() {
        id =""
        avatarColor =""
        avatarName =""
        email =""
        name =""
        AuthService.authToken = ""
//        AuthService.authUser = ""
        AuthService.authEmail = ""
        AuthService.isLoggedIn = false
    }

    fun parseColorString(color : String) : Int {

        val rgbArr = color.substring(1, color.length -1).split(", ")

        return Color.rgb((rgbArr[0].toDouble() * 255).toInt(),
                (rgbArr[1].toDouble() * 255).toInt(),
                (rgbArr[2].toDouble() * 255).toInt())
    }
}