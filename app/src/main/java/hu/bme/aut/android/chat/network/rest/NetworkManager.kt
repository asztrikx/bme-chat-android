package hu.bme.aut.android.chat.network.rest

import hu.bme.aut.android.chat.contacts.ContactBrief
import hu.bme.aut.android.chat.contacts.ContactPostResponse
import hu.bme.aut.android.chat.login.LoginRequest
import hu.bme.aut.android.chat.messages.Message
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


object NetworkManager {
	private val api: Api

	private const val DOMAIN = "192.168.1.103:8080"
	const val SERVICE_URL = "http://$DOMAIN"
	const val API_PATH = "/api"

	init {
		// Create REST and Websocket managers from HttpClient
		val client = OkHttpClient.Builder()
			// Session token always has to be appended
			.addInterceptor(SessionInterceptor())
			.build()

		WebsocketManager.start(client)

		api = Retrofit.Builder()
			.baseUrl(SERVICE_URL)
			.client(client)
			.addConverterFactory(GsonConverterFactory.create())
			.build()
			.create(Api::class.java)
	}

	/**
	 * Api wrappers
	 */

	@Throws(Exception::class)
	suspend fun login(username: String, password: String): Boolean {
		val response = withContext(IO) {
			api.login(LoginRequest(username, password))
		}
		SessionProvider.session = response
		return response != null
	}

	@Throws(Exception::class)
	suspend fun register(username: String, password: String, name: String): String? {
		val response = withContext(IO) {
			api.register(RegisterRequest(username, password, name))
		} ?: throw IOException()

		return response.errorMessage
	}

	@Throws(Exception::class)
	suspend fun contacts(): MutableList<ContactBrief> {
		val response = withContext(IO) {
			api.contacts()
		} ?: throw IOException()

		return response.toMutableList()
	}

	@Throws(Exception::class)
	suspend fun messages(contactId: Int): MutableList<Message> {
		val response = withContext(IO) {
			api.messages(contactId)
		} ?: throw IOException()
		return response.toMutableList()
	}

	@Throws(Exception::class)
	suspend fun addContact(username: String): ContactPostResponse {
		val response = withContext(IO) {
			api.addContact(username)
		} ?: throw IOException()
		return response
	}
}
