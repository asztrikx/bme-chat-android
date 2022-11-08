package hu.bme.aut.android.chat.contacts

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.chat.R
import hu.bme.aut.android.chat.databinding.FragmentContactsBinding
import androidx.navigation.fragment.findNavController
import hu.bme.aut.android.chat.connection.NetworkManager
import hu.bme.aut.android.chat.connection.SessionProvider
import hu.bme.aut.android.chat.connection.handleNetworkError
import hu.bme.aut.android.chat.messages.MessagesFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class ContactsFragment : Fragment() {
	private lateinit var binding: FragmentContactsBinding

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		SessionProvider.listeners.add {
			if (SessionProvider.session == null) {
				findNavController().navigate(R.id.action_global_welcomeFragment)
			}
		}

		binding = FragmentContactsBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onResume() {
		super.onResume()

		val adapter = ContactsAdapter(::openContact)
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

		CoroutineScope(Dispatchers.Main).launch {
			try {
				adapter.contactBriefs = NetworkManager.contacts()
				adapter.notifyDataSetChanged()
			} catch (e: Exception) {
				handleNetworkError(binding.root, ::getString)
			}
		}
	}

	private fun openContact(userId: Int) {
		//MessagesFragment.user = it
		findNavController().navigate(R.id.action_contactsFragment_to_messagesFragment)
	}
}