package hu.bme.aut.android.chat.connection

import android.content.res.Resources
import android.util.Log
import android.view.View
import com.google.android.material.snackbar.Snackbar
import hu.bme.aut.android.chat.R

fun handleNetworkError(view: View, getString: (Int) -> String) {
	Snackbar.make(view, getString(R.string.errorDuringConnection), Snackbar.LENGTH_SHORT).show()
}
