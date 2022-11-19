package hu.bme.aut.android.chat.messages

data class NewMessage(
	val contactId: Int,
	val content: String,
)