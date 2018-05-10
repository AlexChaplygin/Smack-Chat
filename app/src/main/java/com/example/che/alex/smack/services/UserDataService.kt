package com.example.che.alex.smack.services

import android.graphics.Color
import com.example.che.alex.smack.controllers.App

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
        App.sharedPreferences.authToken = ""
//        AuthService.authUser = ""
        App.sharedPreferences.userEmail = ""
        App.sharedPreferences.isLoggedIn = false

        MessageService.clearChannels()
        MessageService.clearMessages()
    }

    fun parseColorString(color : String) : Int {

        val rgbArr = color.substring(1, color.length -1).split(", ")

        return Color.rgb((rgbArr[0].toDouble() * 255).toInt(),
                (rgbArr[1].toDouble() * 255).toInt(),
                (rgbArr[2].toDouble() * 255).toInt())
    }
}