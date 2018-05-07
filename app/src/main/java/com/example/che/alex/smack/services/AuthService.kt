package com.example.che.alex.smack.services

import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.example.che.alex.smack.controllers.App
import com.example.che.alex.smack.utils.*
import org.json.JSONException
import org.json.JSONObject

object AuthService {

    fun registerUser(email : String, pass: String, complete: (Boolean) -> Unit) {

        val jsonbody = JSONObject()
        jsonbody.put("email", email)
        jsonbody.put("password", pass)
        val jsonString = jsonbody.toString()

        val registerRequest = object : StringRequest(Request.Method.POST, URL_REGISTER,
                Response.Listener { complete(true) },
                Response.ErrorListener {
                    error -> Log.d("ERROR", "Can not register user $error")
                    complete(false)
                }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return jsonString.toByteArray()
            }
        }

        App.sharedPreferences.requestQueue.add(registerRequest)
    }


    fun loginUser(email : String, pass: String, complete: (Boolean) -> Unit) {

        val jsonbody = JSONObject()
        jsonbody.put("email", email)
        jsonbody.put("password", pass)
        val jsonString = jsonbody.toString()

        val loginRequest = object : JsonObjectRequest(Method.POST, URL_LOGIN, null,
                Response.Listener { response ->
                    try {
                        App.sharedPreferences.userEmail = response.getString("user")
                        App.sharedPreferences.authToken = response.getString("token")
                        App.sharedPreferences.isLoggedIn = true

                        complete(true)
                    } catch (e : JSONException) {
                        Log.d("JSON", "EXC: ${e.localizedMessage}")
                        complete(false)
                    }
                },
                Response.ErrorListener {
                    error -> Log.d("ERROR", "Can not login user $error")
                    complete(false)
                }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return jsonString.toByteArray()
            }
        }

        App.sharedPreferences.requestQueue.add(loginRequest)
    }

    fun findUserByEmail(context : Context, complete: (Boolean) -> Unit) {

        val findUserRequest = object : JsonObjectRequest(Method.GET, "$URL_GET_BY_EMAIL_USER${App.sharedPreferences.userEmail}", null,
                Response.Listener { response ->
                    try {
                        UserDataService.name = response.getString("name")
                        UserDataService.email = response.getString("email")
                        UserDataService.avatarName = response.getString("avatarName")
                        UserDataService.avatarColor = response.getString("avatarColor")
                        UserDataService.id = response.getString("_id")

                        val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                        LocalBroadcastManager.getInstance(context).sendBroadcast(userDataChange)

                        complete(true)
                    } catch (e : JSONException) {
                        Log.d("JSON", "EXC: ${e.localizedMessage}")
                        complete(false)
                    }
                },
                Response.ErrorListener {
                    error -> Log.d("ERROR", "Can not find user by email $error")
                    complete(false)
                }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer ${App.sharedPreferences.authToken}")
                return headers
            }
        }

        App.sharedPreferences.requestQueue.add(findUserRequest)

    }

    fun createUser(name : String, email : String, avatarName : String,
                   avatarColor : String, complete: (Boolean) -> Unit) {

        val jsonbody = JSONObject()
        jsonbody.put("name", name)
        jsonbody.put("email", email)
        jsonbody.put("avatarName", avatarName)
        jsonbody.put("avatarColor", avatarColor)
        val jsonString = jsonbody.toString()

        val createRequest = object : JsonObjectRequest(Method.POST, URL_CREATE_USER, null,
                Response.Listener { response ->
                    try {
                        UserDataService.name = response.getString("name")
                        UserDataService.email = response.getString("email")
                        UserDataService.avatarName = response.getString("avatarName")
                        UserDataService.avatarColor = response.getString("avatarColor")
                        UserDataService.id = response.getString("_id")

                        complete(true)
                    } catch (e : JSONException) {
                        Log.d("JSON", "EXC: ${e.localizedMessage}")
                        complete(false)
                    }
                },
                Response.ErrorListener {
                    error -> Log.d("ERROR", "Can not create user $error")
                    complete(false)
                }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return jsonString.toByteArray()
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer ${App.sharedPreferences.authToken}")
                return headers
            }
        }

        App.sharedPreferences.requestQueue.add(createRequest)
    }

}