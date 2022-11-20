package hu.bme.aut.android.chat.validator

/**
 * Handles input validation results
 * @return true if all input is valid (not null)
 */
fun <T> allValid(vararg items: T?): Boolean {
	return items.all { it != null }
}