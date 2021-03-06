package com.example.che.alex.smack.utils

const val BASE_URL = "https://alexchattest.herokuapp.com/v1/"
const val GET_CHANNELS_URL = "${BASE_URL}channel/"
const val SOCKET_URL = "https://alexchattest.herokuapp.com/"
const val URL_REGISTER = "${BASE_URL}account/register"
const val URL_LOGIN = "${BASE_URL}account/login"
const val URL_CREATE_USER = "${BASE_URL}user/add"
const val URL_GET_BY_EMAIL_USER = "${BASE_URL}user/byEmail/"
const val URL_GET_MESSAGES_BY_CHANNEL = "${BASE_URL}message/byChannel/"

const val BROADCAST_USER_DATA_CHANGE = "BROADCAST_USER_DATA_CHANGE"