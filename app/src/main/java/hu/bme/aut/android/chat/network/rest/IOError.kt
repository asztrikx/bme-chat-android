package hu.bme.aut.android.chat.network.rest

import android.view.View
import com.google.android.material.snackbar.Snackbar
import hu.bme.aut.android.chat.R

fun handleNetworkError(view: View, getString: (Int) -> String) {
	Snackbar.make(view, getString(R.string.errorDuringConnection), Snackbar.LENGTH_SHORT).show()
}
