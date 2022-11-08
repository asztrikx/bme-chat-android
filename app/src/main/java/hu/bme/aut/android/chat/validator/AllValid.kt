package hu.bme.aut.android.chat.validator

fun <T> allValid(vararg items: T?): Boolean {
	return items.all { it != null }
}