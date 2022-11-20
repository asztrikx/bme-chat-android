package hu.bme.aut.android.chat.contacts

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import hu.bme.aut.android.chat.R
import hu.bme.aut.android.chat.databinding.FragmentContactsBinding
import androidx.navigation.fragment.findNavController
import hu.bme.aut.android.chat.messages.Message
import hu.bme.aut.android.chat.network.rest.handleNetworkError
import hu.bme.aut.android.chat.messages.MessagesFragment
import hu.bme.aut.android.chat.network.rest.RestManager
import hu.bme.aut.android.chat.network.socket.WebsocketManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContactsFragment : Fragment() {
	private lateinit var binding: FragmentContactsBinding

	private val adapter = ContactsAdapter(::openMessages, ::getString)

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		binding = FragmentContactsBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onResume() {
		super.onResume()

		binding.recyclerView.layoutManager = LinearLayoutManager(binding.root.context)
		binding.recyclerView.adapter = adapter

		// We only get new contacts from websocket, older messages have to be loaded
		reloadContacts()
	}

	override fun onStart() {
		super.onStart()
		WebsocketManager.contactBriefListeners.add(::receiveContactBrief)
		WebsocketManager.messageListeners.add(::receiveNewMessage)
	}

	override fun onStop() {
		super.onStop()
		WebsocketManager.contactBriefListeners.remove(::receiveContactBrief)
		WebsocketManager.messageListeners.remove(::receiveNewMessage)
	}

	/**
	 * Load all contacts
	 */
	private fun reloadContacts() {
		CoroutineScope(Dispatchers.Main).launch {
			try {
				adapter.contactBriefs = RestManager.contacts()
				adapter.notifyDataSetChanged()
			} catch (e: Exception) {
				context?.let {
					handleNetworkError(binding.root, it::getString)
				}
			}
		}
	}

	private fun receiveContactBrief(contactBrief: ContactBrief) {
		activity?.runOnUiThread {
			adapter.contactBriefs.add(contactBrief)
			adapter.notifyItemInserted(adapter.contactBriefs.size - 1)
		}
	}

	private fun receiveNewMessage(message: Message) {
		// ContactBrief should always display latest message
		activity?.runOnUiThread {
			adapter.contactBriefs.find {
				message.contactId == it.id
			}?.let {
				it.lastMessageContent = message.content
				it.lastMessageDate = message.date
				adapter.notifyItemChanged(adapter.contactBriefs.indexOf(it))
			}
		}
	}

	/**
	 * Handles navigation and communication with MessagesFragment
	 */
	private fun openMessages(contactId: Int) {
		val bundle = bundleOf(MessagesFragment.CONTACT_ID to contactId)
		findNavController().navigate(R.id.action_contactsFragment_to_messagesFragment, bundle)
	}
}