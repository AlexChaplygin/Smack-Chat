package com.example.che.alex.smack.controllers

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.che.alex.smack.R

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun loginBtnClick(view: View) {

    }

    fun registerBtnClick(view: View) {
        val intent = Intent(this, CreateUserActivity::class.java)
        startActivity(intent)
    }
}
