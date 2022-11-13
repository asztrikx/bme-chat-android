package hu.bme.aut.android.chat.messages

import android.content.Context
import android.util.LayoutDirection
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.chat.R
import hu.bme.aut.android.chat.connection.SessionProvider
import hu.bme.aut.android.chat.databinding.ItemMessageBinding
import java.lang.RuntimeException

class MessagesAdapter(val context: Context): RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder>() {
	var messages = mutableListOf<Message>()

	// Messages sent by us vs other person should be differentiated by layout
	override fun getItemViewType(position: Int): Int {
		SessionProvider.session?.let {
			return if (messages[position].fromUserId == it.userId) {
				0
			} else {
				1
			}
		}
		throw RuntimeException()
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesViewHolder {
		val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return MessagesViewHolder(binding)
	}

	override fun onBindViewHolder(holder: MessagesViewHolder, position: Int) {
		val message = messages[position]

		val resourceId = when (getItemViewType(position)) {
			0 -> R.drawable.shape_message_me
			1 -> R.drawable.shape_message_you
			else -> throw RuntimeException()
		}
		holder.binding.textView.background = ContextCompat.getDrawable(context, resourceId)
		holder.binding.textView.text = message.content
		holder.binding.root.layoutDirection = when (getItemViewType(position)) {
			0 -> View.LAYOUT_DIRECTION_LTR
			1 -> View.LAYOUT_DIRECTION_RTL
			else -> throw RuntimeException()
		}
	}

	override fun getItemCount(): Int = messages.size

	class MessagesViewHolder(val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root)
}