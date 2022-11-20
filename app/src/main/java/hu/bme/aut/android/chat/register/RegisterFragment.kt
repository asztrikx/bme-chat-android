package hu.bme.aut.android.chat.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import hu.bme.aut.android.chat.R
import hu.bme.aut.android.chat.network.rest.handleNetworkError
import hu.bme.aut.android.chat.databinding.FragmentRegisterBinding
import hu.bme.aut.android.chat.network.rest.RestManager
import hu.bme.aut.android.chat.validator.allValid
import hu.bme.aut.android.chat.validator.validateEmpty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {
	private lateinit var binding: FragmentRegisterBinding

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		binding = FragmentRegisterBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.buttonRegister.setOnClickListener {
			CoroutineScope(Dispatchers.Main).launch {
				binding.buttonRegister.isEnabled = false
				register()
				binding.buttonRegister.isEnabled = true
			}
		}
	}

	private suspend fun register() {
		val username = binding.editTextUsername.validateEmpty(::getString)?.text?.toString()
		val password = binding.editTextPassword.validateEmpty(::getString)?.text?.toString()
		val passwordAgain = binding.editTextPasswordAgain.validateEmpty(::getString)?.text?.toString()
		val name = binding.editTextName.validateEmpty(::getString)?.text?.toString()

		if (!allValid(username, password, passwordAgain, name)) return
		username!!
		password!!
		passwordAgain!!
		name!!

		if (password != passwordAgain) {
			Snackbar.make(binding.root, getString(R.string.passwordsMismatch), Snackbar.LENGTH_SHORT).show()
			return
		}

		val errorMessage = try {
			RestManager.register(username, password, name)
		} catch (e: Exception) {
			handleNetworkError(binding.root, ::getString)
			return
		}

		errorMessage?.let {
			Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
		}
		if (errorMessage == null) {
			Snackbar.make(binding.root, getString(R.string.successfulRegistration), Snackbar.LENGTH_SHORT).show()
			findNavController().navigate(R.id.action_registerFragment_to_welcomeFragment)
		}
	}
}