package hu.bme.aut.android.chat.register

data class RegisterRequest (
	val username: String,
	val password: String,
	val name: String,
)