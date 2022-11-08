package hu.bme.aut.android.chat.messages

import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.chat.connection.User
import hu.bme.aut.android.chat.databinding.FragmentMessagesBinding


class MessagesFragment : Fragment() {
	private lateinit var binding: FragmentMessagesBinding

	companion object {
		var user = null as User
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

		val adapter = MessagesAdapter(requireContext(), user)
		binding.messages.layoutManager = LinearLayoutManager(binding.root.context).apply {
			reverseLayout = true
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
	}
}