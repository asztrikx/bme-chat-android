package hu.bme.aut.android.chat.messages

data class Message(val fromUserId: Int, val toUserId: Int, val content: String, val date: String)
