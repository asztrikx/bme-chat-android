package hu.bme.aut.android.chat.messages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import hu.bme.aut.android.chat.databinding.FragmentMessagesBinding
import hu.bme.aut.android.chat.network.rest.NetworkManager
import hu.bme.aut.android.chat.network.rest.handleNetworkError
import hu.bme.aut.android.chat.network.socket.WebsocketManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class MessagesFragment : Fragment() {
	// Bundle communication keys
	companion object {
		const val CONTACT_ID = "contactId"
	}
	private lateinit var binding: FragmentMessagesBinding

	private var contactId by Delegates.notNull<Int>()
	private lateinit var adapter: MessagesAdapter

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			contactId = it.getInt(CONTACT_ID)
		}
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		binding = FragmentMessagesBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onStart() {
		super.onStart()
		WebsocketManager.messageListeners.add(::receiveNewMessage)
	}

	override fun onStop() {
		super.onStop()
		WebsocketManager.messageListeners.remove(::receiveNewMessage)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		adapter = MessagesAdapter(requireContext())
		binding.messages.layoutManager = LinearLayoutManager(binding.root.context).apply {
			// New messages should appear at the bottom
			stackFromEnd = true
		}
		binding.messages.adapter = adapter
		binding.imageButton.setOnClickListener { sendMessage() }

		// We only get new messages from websocket, older messages have to be loaded
		reloadMessages()
	}

	/**
	 * Load all messages
	 */
	private fun reloadMessages() {
		CoroutineScope(Dispatchers.Main).launch {
			try {
				adapter.messages = NetworkManager.messages(contactId)
				adapter.notifyDataSetChanged()
			} catch (e: Exception) {
				handleNetworkError(binding.root, ::getString)
			}
		}
	}

	private fun receiveNewMessage(message: Message) {
		activity?.runOnUiThread {
			// RecyclerView doesn't scroll to the bottom if new item is inserted.
			// Only scroll to the new bottom if user was already at the bottom.
			val scrollHeight = binding.messages.computeVerticalScrollRange() - binding.messages.computeVerticalScrollExtent()
			val scrollPosition = binding.messages.computeVerticalScrollOffset()

			adapter.messages.add(message)
			adapter.notifyItemInserted(adapter.messages.size - 1)

			// Calculate height before items are added
			// Scroll after items are added
			if (scrollHeight == scrollPosition) {
				binding.messages.smoothScrollToPosition(adapter.messages.size - 1)
			}
		}
	}

	private fun sendMessage() {
		val messageContent = binding.editTextMessage.text.toString()

		val success = WebsocketManager.sendNewMessage(NewMessage(
			contactId,
			messageContent
		))

		if (!success) {
			context?.let {
				handleNetworkError(binding.root, it::getString)
			}
		} else {
			// Only clear text if sending was successful
			binding.editTextMessage.setText("")
		}
	}
}