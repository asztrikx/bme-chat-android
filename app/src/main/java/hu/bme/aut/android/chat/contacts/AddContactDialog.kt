package hu.bme.aut.android.chat.contacts

import android.app.Dialog
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.chat.R
import hu.bme.aut.android.chat.databinding.FragmentContactsBinding
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import hu.bme.aut.android.chat.connection.NetworkManager
import hu.bme.aut.android.chat.connection.handleNetworkError
import hu.bme.aut.android.chat.databinding.DialogAddContactBinding
import hu.bme.aut.android.chat.messages.MessagesFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddContactDialog(
	private val onAdd: (ContactBrief) -> Unit,
	private val parentView: View,
	private val parentGetString: (Int) -> String
) : DialogFragment() {
	private lateinit var binding: DialogAddContactBinding
	companion object {
		const val TAG = "AddContactDialog"
	}

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		binding = DialogAddContactBinding.inflate(layoutInflater)

		return AlertDialog.Builder(requireContext())
			.setTitle("Username of new contact")
			.setView(binding.root)
			.setPositiveButton("Add") { dialogInterface, i ->
				addContact()
			}
			.setNegativeButton("Cancel", null)
			.create()
	}

	private fun addContact() {
		val username = binding.textUsername.text.toString()

		CoroutineScope(Dispatchers.Main).launch {
			try {
				val contactPostResponse = NetworkManager.addContact(username)
				val error = contactPostResponse.error
				val contactBrief = contactPostResponse.contactBrief

				val text = error ?: "User added to contacts" // TODO use strings.xml even for errors
				Snackbar.make(parentView, text, Snackbar.LENGTH_SHORT).show()

				if (error == null) {
					onAdd(contactBrief!!)
				}
			} catch (e: Exception) {
				handleNetworkError(parentView, parentGetString)
			}
		}
	}
}