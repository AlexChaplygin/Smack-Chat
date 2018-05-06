package com.example.che.alex.smack.controllers

import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import com.example.che.alex.smack.R
import com.example.che.alex.smack.services.AuthService
import com.example.che.alex.smack.services.UserDataService
import com.example.che.alex.smack.utils.BROADCAST_USER_DATA_CHANGE
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        hideKeyboard()

        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver, IntentFilter(BROADCAST_USER_DATA_CHANGE))

    }

    private val userDataChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (AuthService.isLoggedIn) {
                nameUserHeader.text = UserDataService.name
                emailTextHeader.text = UserDataService.email
                val resourceId = resources.getIdentifier(UserDataService.avatarName, "drawable", packageName)
                userImageHeader.setImageResource(resourceId)
                userImageHeader.setBackgroundColor(
                        UserDataService.parseColorString(UserDataService.avatarColor))
                loginButtonHeader.text = "logout"
            }
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun loginBtnHeaderClick(view: View) {

        if (AuthService.isLoggedIn) {
            UserDataService.logout()
            nameUserHeader.text = "name"
            emailTextHeader.text = "email"
            loginButtonHeader.text = "login"
            userImageHeader.setImageResource(R.drawable.profiledefault)
            userImageHeader.setBackgroundColor(Color.TRANSPARENT)
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    fun addChannelBtnClick(view: View) {
        if (AuthService.isLoggedIn) {
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)

            builder.setView(dialogView).setPositiveButton("Add") { dialog: DialogInterface?, which: Int ->
                val nameText = dialogView.findViewById<EditText>(R.id.addChannelNameTxt)
                val descText = dialogView.findViewById<EditText>(R.id.addChannnelDescrTxt)
                val channelName = nameText.text.toString()
                val channelDesc = descText.text.toString()

                hideKeyboard()
            }.setNegativeButton("Cancel") { dialog, which ->
                hideKeyboard()
            }.show()
        }
    }

    fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }

    fun sendMessageBtnClick(view: View) {

    }
}
