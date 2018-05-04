package com.example.che.alex.smack.services

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.che.alex.smack.utils.URL_CREATE_USER
import com.example.che.alex.smack.utils.URL_LOGIN
import com.example.che.alex.smack.utils.URL_REGISTER
import org.json.JSONException
import org.json.JSONObject

object AuthService {

    var authUser = ""
    var authEmail = ""
    var authToken = ""
    var isLoggedIn = false

    fun registerUser(context : Context, email : String, pass: String, complete: (Boolean) -> Unit) {

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

        Volley.newRequestQueue(context).add(registerRequest)
    }


    fun loginUser(context : Context, email : String, pass: String, complete: (Boolean) -> Unit) {

        val jsonbody = JSONObject()
        jsonbody.put("email", email)
        jsonbody.put("password", pass)
        val jsonString = jsonbody.toString()

        val loginRequest = object : JsonObjectRequest(Method.POST, URL_LOGIN, null,
                Response.Listener { response ->
                    try {
                        authUser = response.getString("user")
                        authToken = response.getString("token")
                        isLoggedIn = true

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

        Volley.newRequestQueue(context).add(loginRequest)
    }

    fun createUser(context : Context, name : String, email : String, avatarName : String,
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
                headers.put("Authorization", "Bearer $authToken")
                return headers
            }
        }

        Volley.newRequestQueue(context).add(createRequest)
    }

}