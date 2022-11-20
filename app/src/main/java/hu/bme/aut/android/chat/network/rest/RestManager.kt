package hu.bme.aut.android.chat.network.rest

import hu.bme.aut.android.chat.contacts.ContactBrief
import hu.bme.aut.android.chat.contacts.ContactPostResponse
import hu.bme.aut.android.chat.login.LoginRequest
import hu.bme.aut.android.chat.messages.Message
import hu.bme.aut.android.chat.network.NetworkManager
import hu.bme.aut.android.chat.network.socket.WebsocketManager
import hu.bme.aut.android.chat.register.RegisterRequest
import hu.bme.aut.android.chat.session.SessionProvider
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import kotlin.jvm.Throws

/**
 * REST Api wrappers
 */
object RestManager {
	const val API_PATH = "/api"

	@Throws(Exception::class)
	suspend fun login(username: String, password: String): Boolean {
		val response = withContext(IO) {
			NetworkManager.api.login(LoginRequest(username, password))
		}
		// Retrofit doesn't support "null" body
		return if (response.userId != 0) {
			SessionProvider.session = response
			true
		} else {
			false
		}
	}

	@Throws(Exception::class)
	suspend fun register(username: String, password: String, name: String): String? {
		val response = withContext(IO) {
			NetworkManager.api.register(RegisterRequest(username, password, name))
		}

		return response.errorMessage
	}

	@Throws(Exception::class)
	suspend fun logout() {
		withContext(IO) {
			NetworkManager.api.logout()
		}
	}

	@Throws(Exception::class)
	suspend fun contacts(): MutableList<ContactBrief> {
		val response = withContext(IO) {
			NetworkManager.api.contacts()
		}

		return response.toMutableList()
	}

	@Throws(Exception::class)
	suspend fun messages(contactId: Int): MutableList<Message> {
		val response = withContext(IO) {
			NetworkManager.api.messages(contactId)
		}
		return response.toMutableList()
	}

	@Throws(Exception::class)
	suspend fun addContact(username: String): ContactPostResponse {
		val response = withContext(IO) {
			NetworkManager.api.addContact(username)
		}
		return response
	}
}
