package com.example.che.alex.smack.controllers

import android.app.Application
import com.example.che.alex.smack.utils.SharedPrefs

class App : Application() {

    companion object {
        lateinit var sharedPreferences: SharedPrefs
    }

    override fun onCreate() {
        sharedPreferences = SharedPrefs(applicationContext)
        super.onCreate()
    }
}