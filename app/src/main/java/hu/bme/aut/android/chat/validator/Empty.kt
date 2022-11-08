package hu.bme.aut.android.chat.validator

import android.content.res.Resources
import android.widget.EditText
import hu.bme.aut.android.chat.R

fun EditText.validateEmpty(getString: (Int) -> String): EditText? {
	setText(text.toString().trim())
	if (text.toString().isEmpty()) {
		requestFocus()
		error = getString(R.string.missingField)
		return null
	}
	return this
}