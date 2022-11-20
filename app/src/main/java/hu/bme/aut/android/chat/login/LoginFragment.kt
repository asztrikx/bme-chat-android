package hu.bme.aut.android.chat.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import hu.bme.aut.android.chat.R
import hu.bme.aut.android.chat.network.rest.handleNetworkError
import hu.bme.aut.android.chat.databinding.FragmentLoginBinding
import hu.bme.aut.android.chat.network.rest.RestManager
import hu.bme.aut.android.chat.validator.allValid
import hu.bme.aut.android.chat.validator.validateEmpty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
	private lateinit var binding: FragmentLoginBinding

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		binding = FragmentLoginBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.login.setOnClickListener {
			CoroutineScope(Main).launch {
				binding.login.isEnabled = false
				login()
				binding.login.isEnabled = true
			}
		}
	}

	private suspend fun login() {
		val username = binding.editTextUsername.validateEmpty(::getString)?.text?.toString()
		val password = binding.editTextPassword.validateEmpty(::getString)?.text?.toString()
		if (!allValid(username, password)) return
		username!!
		password!!

		val successful = try {
			RestManager.login(username, password)
		} catch (e: Exception) {
			handleNetworkError(binding.root, ::getString)
			return
		}

		if (successful) {
			findNavController().navigate(R.id.action_loginFragment_to_contactsFragment)
		} else {
			Snackbar.make(binding.root, getString(R.string.signinFailed), Snackbar.LENGTH_SHORT).show()
		}
	}
}