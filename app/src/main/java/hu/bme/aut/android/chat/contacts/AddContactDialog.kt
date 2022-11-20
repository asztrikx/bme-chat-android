package hu.bme.aut.android.chat.contacts

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.material.snackbar.Snackbar
import hu.bme.aut.android.chat.R
import hu.bme.aut.android.chat.network.rest.handleNetworkError
import hu.bme.aut.android.chat.databinding.DialogAddContactBinding
import hu.bme.aut.android.chat.network.rest.RestManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddContactDialog(
	private val parentView: View,
	private val parentGetString: (Int) -> String
) : DialogFragment() {
	private lateinit var binding: DialogAddContactBinding
	companion object {
		const val TAG = "AddContactDialog"
	}

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		binding = DialogAddContactBinding.inflate(layoutInflater)

		return AlertDialog.Builder(requireContext(), R.style.MyDialogTheme)
			.setTitle(parentGetString(R.string.newContactTitle))
			.setView(binding.root)
			.setPositiveButton(parentGetString(R.string.newContactAdd)) { dialogInterface, i ->
				addContact()
			}
			.setNegativeButton(parentGetString(R.string.newContactCancel), null)
			.create()
	}

	private fun addContact() {
		val username = binding.textUsername.text.toString()

		CoroutineScope(Dispatchers.Main).launch {
			val contactPostResponse = try {
				RestManager.addContact(username)
			} catch (e: Exception) {
				handleNetworkError(parentView, parentGetString)
				return@launch
			}
			val error = contactPostResponse.error

			val text = error ?: parentGetString(R.string.newContactSuccess)
			Snackbar.make(parentView, text, Snackbar.LENGTH_SHORT).show()
		}
	}
}