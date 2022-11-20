package hu.bme.aut.android.chat.contacts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.chat.R
import hu.bme.aut.android.chat.databinding.ItemContactBinding
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ContactsAdapter(val onClickListener: (Int) -> Unit, val getString: (Int) -> String): RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder>() {
	var contactBriefs = mutableListOf<ContactBrief>()

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
		val binding = ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return ContactsViewHolder(binding)
	}

	override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
		val contact = contactBriefs[position]

		holder.binding.profileImageText.text = contact.name
			.split(" ").take(2)
			.map{ it[0] }
			.joinToString("")
			.uppercase(Locale.getDefault())
		holder.binding.fullName.text = contact.name
		holder.binding.lastMessageText.text = contact.lastMessageContent
		holder.binding.root.setOnClickListener {
			onClickListener(contact.id)
		}
		holder.binding.lastMessageDate.text = getLastMessageDateText(contact.lastMessageDate)
	}

	private fun getLastMessageDateText(lastMessageDate: String): String {
		if (lastMessageDate == "") {
			return ""
		}

		val date = LocalDateTime.parse(
			lastMessageDate,
			DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")
		)
		val dateFormatPattern = if (Duration.between(date, LocalDateTime.now()).toDays() >= 1) {
			getString(R.string.dateFormat)
		} else {
			getString(R.string.hourFormat)
		}
		return date.format(DateTimeFormatter.ofPattern(dateFormatPattern))
	}

	override fun getItemCount(): Int = contactBriefs.size

	class ContactsViewHolder(val binding: ItemContactBinding) : RecyclerView.ViewHolder(binding.root)
}