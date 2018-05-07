package com.example.che.alex.smack.controllers

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.view.View
import android.widget.Toast
import com.example.che.alex.smack.R
import com.example.che.alex.smack.services.AuthService
import com.example.che.alex.smack.services.UserDataService
import com.example.che.alex.smack.utils.BROADCAST_USER_DATA_CHANGE
import kotlinx.android.synthetic.main.activity_create_user.*
import java.util.*

class CreateUserActivity : AppCompatActivity() {

    var userAvatar = "profiledefault"
    var avatarColor = "[0.5,0.5,0.5,1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
        createUserSpinner.visibility = View.INVISIBLE
    }

    fun createUserChooseColorClick(view: View) {
        val random = Random()
        val r = random.nextInt(255)
        val g = random.nextInt(255)
        val b = random.nextInt(255)

        avatarImageChoose.setBackgroundColor(Color.rgb(r, g, b))

        val savedR = r.toDouble() /255
        val savedG = g.toDouble() /255
        val savedB = b.toDouble() /255

        avatarColor = "[$savedR, $savedG, $savedB, 1]"
    }

    fun createUserCreateClick(view: View) {

        enableSpinner(true)

        val nameUser = createUserNameText.text.toString()
        val emailUser = createUserEmailText.text.toString()
        val passUser = createUserPassText.text.toString()

        if(nameUser.isNotEmpty() && emailUser.isNotEmpty() && passUser.isNotEmpty()) {
            AuthService.registerUser(emailUser, passUser) {registerSuccess ->
                if(registerSuccess) {
                    AuthService.loginUser(emailUser, passUser) {loginSuccess ->
                        if(loginSuccess) {
                            AuthService.createUser(nameUser, emailUser, userAvatar, avatarColor) {success ->
                                if(success) {

                                    val broadcastChange = Intent(BROADCAST_USER_DATA_CHANGE)
                                    LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastChange)

                                    enableSpinner(false)
                                    finish()
                                } else {
                                    errorToast()
                                }
                            }
                        } else {
                            errorToast()
                        }
                    }
                } else {
                    errorToast()
                }
            }
        } else {
            Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show()
            enableSpinner(false)
        }
    }

    fun avatarImageChooseClick(view: View) {
        val random = Random()
        val avatarColor = random.nextInt(2)
        val avatarNum = random.nextInt(28)

        if(avatarColor == 0) {
            userAvatar = "light$avatarNum"
        } else {
            userAvatar = "dark$avatarNum"
        }

        val resourceId = resources.getIdentifier(userAvatar, "drawable", packageName)
        avatarImageChoose.setImageResource(resourceId)
    }

    fun errorToast() {
        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
        enableSpinner(false)
    }

    fun enableSpinner(enable : Boolean) {
        if(enable) {
            createUserSpinner.visibility = View.VISIBLE
        } else {
            createUserSpinner.visibility = View.INVISIBLE
        }

        createUserCreateBtn.isEnabled = !enable
        avatarImageChoose.isEnabled = !enable
        createUserChooseColorBtn.isEnabled = !enable
    }
}
