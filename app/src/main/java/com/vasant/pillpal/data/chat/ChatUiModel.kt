package com.vasant.pillpal.data.chat

data class ChatUiModel(
    val messages: List<Message>,
    val addressee: Author,
) {
    data class Message(
        val text: String,
        val author: Author,
    ) {
        val isFromMe: Boolean
            get() = author == Author.ME

        companion object {
            val initConv = Message(
                text = "Hi there, how you doing?",
                author = Author.BOT
            )
        }
    }

}
