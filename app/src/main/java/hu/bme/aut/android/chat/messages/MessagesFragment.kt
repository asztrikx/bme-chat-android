package hu.bme.aut.android.chat.messages

import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.chat.connection.NetworkManager
import hu.bme.aut.android.chat.connection.handleNetworkError
import hu.bme.aut.android.chat.databinding.FragmentMessagesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class MessagesFragment : Fragment() {
	private lateinit var binding: FragmentMessagesBinding
	private var contactId by Delegates.notNull<Int>()
	companion object {
		const val CONTACT_ID = "contactId"
	}

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

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val adapter = MessagesAdapter(requireContext())
		binding.messages.layoutManager = LinearLayoutManager(binding.root.context).apply {
			stackFromEnd = true
		}
		binding.messages.adapter = adapter
		binding.messages.addItemDecoration(object : RecyclerView.ItemDecoration() {
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
				adapter.messages = NetworkManager.messages(contactId)
				adapter.notifyDataSetChanged()
			} catch (e: Exception) {
				handleNetworkError(binding.root, ::getString)
			}
		}
	}
}