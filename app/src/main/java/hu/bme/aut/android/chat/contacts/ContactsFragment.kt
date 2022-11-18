package hu.bme.aut.android.chat.contacts

import android.graphics.Rect
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.chat.R
import hu.bme.aut.android.chat.databinding.FragmentContactsBinding
import androidx.navigation.fragment.findNavController
import hu.bme.aut.android.chat.network.rest.NetworkManager
import hu.bme.aut.android.chat.network.rest.handleNetworkError
import hu.bme.aut.android.chat.messages.MessagesFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContactsFragment : Fragment() {
	private lateinit var binding: FragmentContactsBinding

	private val adapter = ContactsAdapter(::openMessages)

	// TODO reverse row

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
		binding.recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
			override fun getItemOffsets(
				outRect: Rect,
				view: View,
				parent: RecyclerView,
				state: RecyclerView.State
			) {
				super.getItemOffsets(outRect, view, parent, state)
				outRect.top = 17
				outRect.bottom = 17
				outRect.left = 20
				outRect.right = 20
			}
		})

		reloadContacts()
	}

	private fun reloadContacts() {
		CoroutineScope(Dispatchers.Main).launch {
			try {
				adapter.contactBriefs = NetworkManager.contacts()
				adapter.notifyDataSetChanged()
			} catch (e: Exception) {
				context?.let {
					handleNetworkError(binding.root, it::getString)
				}
			}
		}
	}

	private fun openMessages(contactId: Int) {
		val bundle = bundleOf(MessagesFragment.CONTACT_ID to contactId)
		findNavController().navigate(R.id.action_contactsFragment_to_messagesFragment, bundle)
	}
}