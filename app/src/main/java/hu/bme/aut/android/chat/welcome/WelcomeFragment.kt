package hu.bme.aut.android.chat.welcome

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import hu.bme.aut.android.chat.R
import hu.bme.aut.android.chat.session.SessionProvider
import hu.bme.aut.android.chat.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {
	private lateinit var binding: FragmentWelcomeBinding

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		binding = FragmentWelcomeBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.login.setOnClickListener {
			findNavController().navigate(R.id.action_welcomeFragment_to_loginFragment)
		}
		binding.register.setOnClickListener {
			findNavController().navigate(R.id.action_welcomeFragment_to_registerFragment)
		}

		// With session the first screen should be the ContactsFragment, with nothing else on the stack
		if (SessionProvider.session != null) {
			findNavController().navigate(R.id.action_welcomeFragment_to_contactsFragment)
		}
	}
}