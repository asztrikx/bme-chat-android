package hu.bme.aut.android.chat.contacts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import hu.bme.aut.android.chat.connection.NetworkManager
import hu.bme.aut.android.chat.connection.User
import hu.bme.aut.android.chat.connection.handleNetworkError
import hu.bme.aut.android.chat.databinding.ItemContactBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ContactsAdapter(val onClickListener: (Int) -> Unit): RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder>() {
	var contactBriefs = mutableListOf<ContactBrief>()

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
		val binding = ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return ContactsViewHolder(binding)
	}

	override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
		val contact = contactBriefs[position]

		holder.binding.profileImageText.text = contact.name.split(" ").take(2).joinToString("")
		holder.binding.fullName.text = contact.name
		holder.binding.lastMessageText.text = contact.lastMessageContent
		holder.binding.root.setOnClickListener {
			onClickListener(contact.userId)
		}

		if (contact.lastMessageDate != "") {
			val date = LocalDateTime.parse(
				contact.lastMessageDate,
				DateTimeFormatter.ofPattern("yy/MM/dd HH:mm")
			)
			val dateFormatPattern = if (Duration.between(date, LocalDateTime.now()).toDays() >= 1) {
				"yy/MM/dd"
			} else {
				"HH:mm"
			}
			holder.binding.lastMessageDate.text = date.format(DateTimeFormatter.ofPattern(dateFormatPattern))
		}
	}

	override fun getItemCount(): Int = contactBriefs.size

	class ContactsViewHolder(val binding: ItemContactBinding) : RecyclerView.ViewHolder(binding.root)
}