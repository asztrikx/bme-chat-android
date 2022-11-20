package hu.bme.aut.android.chat.messages

data class Message (
	val contactId: Int,
	val fromUserId: Int,
	val toUserId: Int,
	val content: String,
	val date: String,
)
