package com.example.che.alex.smack.controllers

import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import com.example.che.alex.smack.R
import com.example.che.alex.smack.adapters.MessageAdapter
import com.example.che.alex.smack.models.Channel
import com.example.che.alex.smack.models.Message
import com.example.che.alex.smack.services.AuthService
import com.example.che.alex.smack.services.MessageService
import com.example.che.alex.smack.services.UserDataService
import com.example.che.alex.smack.utils.BROADCAST_USER_DATA_CHANGE
import com.example.che.alex.smack.utils.SOCKET_URL
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.message_list_view.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    val socket = IO.socket(SOCKET_URL)
    lateinit var channelAdapter: ArrayAdapter<Channel>
    lateinit var messageAdapter: MessageAdapter
    var selectedChannel : Channel? = null

    private fun setupAdapters() {
        channelAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, MessageService.channels)
        channel_list.adapter = channelAdapter

        messageAdapter = MessageAdapter(this,MessageService.messages)
        messageListView.adapter = messageAdapter
        val layoutManager = LinearLayoutManager(this)
        messageListView.layoutManager = layoutManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        socket.connect()
        socket.on("channelCreated", onNewChannel)
        socket.on("messageCreated", onNewMessage)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        setupAdapters()

        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver,
                IntentFilter(BROADCAST_USER_DATA_CHANGE))

        channel_list.setOnItemClickListener { _, _, position, _ ->
            selectedChannel = MessageService.channels[position]
            drawer_layout.closeDrawer(GravityCompat.START) // hide layout
            updateWithChannel()
        }

        if (App.sharedPreferences.isLoggedIn) {
            AuthService.findUserByEmail(this) {}
        }
    }

    override fun onDestroy() {
        socket.disconnect()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataChangeReceiver)
        super.onDestroy()
    }

    private val userDataChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (App.sharedPreferences.isLoggedIn) {
                nameUserHeader.text = UserDataService.name
                emailTextHeader.text = UserDataService.email
                val resourceId = resources.getIdentifier(UserDataService.avatarName, "drawable", packageName)
                userImageHeader.setImageResource(resourceId)
                userImageHeader.setBackgroundColor(
                        UserDataService.parseColorString(UserDataService.avatarColor))
                loginButtonHeader.text = "logout"

                MessageService.getChannels{complete->
                    if (complete) {

                        if(MessageService.channels.count() > 0) {
                            selectedChannel = MessageService.channels[0]
                            channelAdapter.notifyDataSetChanged()
                            updateWithChannel()
                        }
                    }
                }
            }
        }
    }

    fun updateWithChannel() {
        mainChannelName.text = "#${selectedChannel?.name}"
        if(selectedChannel != null) {
            MessageService.getMessages(selectedChannel!!.id){complete ->
                if(complete) {
                    messageAdapter.notifyDataSetChanged()
                    if (messageAdapter.itemCount > 0) { // scroll down to the last message
                        messageListView.smoothScrollToPosition(messageAdapter.itemCount - 1)
                    }
                }
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

    private val onNewChannel = Emitter.Listener { args->
        if(App.sharedPreferences.isLoggedIn) {
            runOnUiThread {
                val channelName = args[0] as String
                val channelDescr = args[1] as String
                val channelId = args[2] as String

                val channel = Channel(channelName, channelDescr, channelId)
                MessageService.channels.add(channel)

                channelAdapter.notifyDataSetChanged()
            }
        }
    }

    private val onNewMessage = Emitter.Listener { args ->
        if(App.sharedPreferences.isLoggedIn) {
            runOnUiThread {
                val channelId = args[2] as String
                if(channelId == selectedChannel?.id) {
                    val msgBody = args[0] as String
                    val userName = args[3] as String
                    val userAvatar = args[4] as String
                    val userAvatarColor = args[5] as String
                    val id = args[6] as String
                    val timeStamp = args[7] as String

                    val newMessage = Message(msgBody, userName, channelId, userAvatar, userAvatarColor, id, timeStamp)
                    MessageService.messages.add(newMessage)

                    messageAdapter.notifyDataSetChanged() // scroll down to the last message
                    messageListView.smoothScrollToPosition(messageAdapter.itemCount - 1)
                }
            }
        }
    }

    fun loginBtnHeaderClick(view: View) {

        if (App.sharedPreferences.isLoggedIn) {
            UserDataService.logout()
            channelAdapter.notifyDataSetChanged()
            messageAdapter.notifyDataSetChanged()
            nameUserHeader.text = "name"
            emailTextHeader.text = "email"
            loginButtonHeader.text = "login"
            userImageHeader.setImageResource(R.drawable.profiledefault)
            userImageHeader.setBackgroundColor(Color.TRANSPARENT)
            mainChannelName.text = "Please log in"
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    fun addChannelBtnClick(view: View) {
        if (App.sharedPreferences.isLoggedIn) {
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)

            builder.setView(dialogView).setPositiveButton("Add") { dialog: DialogInterface?, which: Int ->
                val nameText = dialogView.findViewById<EditText>(R.id.addChannelNameTxt)
                val descText = dialogView.findViewById<EditText>(R.id.addChannnelDescrTxt)
                val channelName = nameText.text.toString()
                val channelDesc = descText.text.toString()

                socket.emit("newChannel", channelName, channelDesc)
            }.setNegativeButton("Cancel") { dialog, which ->

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

        if(App.sharedPreferences.isLoggedIn &&
                messageTextField.text.isNotEmpty() &&
                selectedChannel != null) {
            val userId = UserDataService.id
            val channelId = selectedChannel!!.id // definitely not null

            socket.emit("newMessage", messageTextField.text.toString(), userId, channelId,
                    UserDataService.name, UserDataService.avatarName, UserDataService.avatarColor)

            messageTextField.text.clear()

            hideKeyboard()
        }

    }
}
