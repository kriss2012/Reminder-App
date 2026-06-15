package com.vasant.pillpal.data.chat

import com.vasant.pillpal.APPCONSTANTS.BOT
import com.vasant.pillpal.APPCONSTANTS.MY_ID

data class Message(
    val text: String,
    val author: Author,
) {
    val isFromMe: Boolean
        get() = author== Author.ME

    companion object {
        val initConv = Message(
            text = "Hi there, how you doing?",
            author = Author.BOT)
    }
}
