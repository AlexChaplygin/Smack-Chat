package com.example.che.alex.smack.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.che.alex.smack.R
import com.example.che.alex.smack.models.Message
import com.example.che.alex.smack.services.UserDataService
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(val context: Context, val messages: ArrayList<Message>): RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.message_list_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messages.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.bindMessage(messages[position], context)
    }

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val userImage = itemView?.findViewById<ImageView>(R.id.messageUserImage)
        val timeStamp = itemView?.findViewById<TextView>(R.id.messageTimeStamp)
        val messageBody = itemView?.findViewById<TextView>(R.id.messageBody)
        val userName = itemView?.findViewById<TextView>(R.id.messageUserName)

        fun bindMessage(message : Message, context : Context) {
            val resourceId = context.resources.getIdentifier(message.userAvatar,
                    "drawable", context.packageName)

            userImage?.setImageResource(resourceId)
            userImage?.setBackgroundColor(UserDataService.parseColorString(message.userAvatarColor))

            userName?.text = message.userName
            timeStamp?.text = returnDateString(message.timeStamp)
            messageBody?.text = message.message
        }
    }

    fun returnDateString(isoString: String) : String {
        val formatter = SimpleDateFormat("yyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        var convertDate = Date()

        try {
            convertDate = formatter.parse(isoString)
        } catch (e : ParseException) {
            Log.d("PARSE", "Cannot parse date")
        }

        val outFormatter = SimpleDateFormat("E, h:mm a", Locale.getDefault())

        return outFormatter.format(convertDate)
    }

}