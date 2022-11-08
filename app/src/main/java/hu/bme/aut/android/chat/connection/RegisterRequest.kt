package hu.bme.aut.android.chat.connection

data class RegisterRequest (
	val username: String,
	val password: String,
	val name: String,
)