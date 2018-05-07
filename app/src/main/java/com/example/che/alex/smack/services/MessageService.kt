package com.example.che.alex.smack.services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.example.che.alex.smack.controllers.App
import com.example.che.alex.smack.models.Channel
import com.example.che.alex.smack.utils.GET_CHANNELS_URL
import org.json.JSONArray
import org.json.JSONException

object MessageService {
    val channels = ArrayList<Channel>()

    fun getChannels(context: Context, complete: (Boolean) -> Unit) {
        val channelRequest = object : JsonArrayRequest(Method.GET, GET_CHANNELS_URL, null, Response.Listener { response ->
            try {
                for (x in 0 until response.length()) {
                    val channel = response.getJSONObject(x)
                    val name = channel.getString("name")
                    val desc = channel.getString("description")
                    val id = channel.getString("_id")

                    channels.add(Channel(name, desc, id))
                }

                complete(true)
            } catch (e: JSONException) {
                Log.d("JSON", "EXC: ${e.localizedMessage}")
                complete(false)
            }
        }, Response.ErrorListener { error ->
            Log.d("ERROR", "Can not find channels $error")
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

        App.sharedPreferences.requestQueue.add(channelRequest)
    }
}