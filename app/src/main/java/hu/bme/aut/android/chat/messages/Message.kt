package hu.bme.aut.android.chat.messages

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class Message(val fromUserId: Int, val toUserId: Int, val content: String, val date: LocalDateTime)