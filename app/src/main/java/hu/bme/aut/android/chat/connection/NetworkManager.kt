package hu.bme.aut.android.chat.connection

import java.util.Base64
import hu.bme.aut.android.chat.contacts.ContactBrief
import hu.bme.aut.android.chat.messages.Message
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.time.Duration
import kotlin.jvm.Throws


object NetworkManager {
	private val retrofit: Retrofit
	private val userApi: UserApi

	//private const val SERVICE_URL = "http://152.66.182.135:8080"
	private const val SERVICE_URL = "http://192.168.1.103:8080"
	const val API_PATH = "/api"

	init {
		val client = OkHttpClient.Builder()
			.addInterceptor(AuthInterceptor())
			.callTimeout(Duration.ofDays(1)) // TODO
			.build()

		retrofit = Retrofit.Builder()
			.baseUrl(SERVICE_URL)
			.client(client)
			.addConverterFactory(GsonConverterFactory.create())
			.build()
		userApi = retrofit.create(UserApi::class.java)
	}

	class AuthInterceptor : Interceptor {
		override fun intercept(chain: Interceptor.Chain): Response {
			val request = when(SessionProvider.session) {
				null -> chain.request()
				else -> {
					val tokenEncoded = Base64.getEncoder().encodeToString(SessionProvider.session!!.token.toByteArray())
					val basicDecoded = "${SessionProvider.session!!.userId}:${tokenEncoded}"
					val basic = Base64.getEncoder().encodeToString(basicDecoded.toByteArray())

					val request = chain.request().newBuilder()
					request.addHeader("Authorization", "Basic $basic")
					request.build()
				}
			}
			return chain.proceed(request)
		}
	}

	@Throws(Exception::class)
	suspend fun login(username: String, password: String): Boolean {
		val response = withContext(IO) {
			userApi.login(LoginRequest(username, password))
		}
		SessionProvider.session = response
		return response != null
	}

	@Throws(Exception::class)
	suspend fun register(username: String, password: String, name: String): String? {
		val response = withContext(IO) {
			userApi.register(RegisterRequest(username, password, name))
		} ?: throw IOException()

		return response.errorMessage
	}

	@Throws(Exception::class)
	suspend fun contacts(): MutableList<ContactBrief> {
		val response = withContext(IO) {
			userApi.contacts()
		} ?: throw IOException()

		return response.toMutableList()
	}

	@Throws(Exception::class)
	suspend fun messages(contactId: Int): MutableList<Message> {
		val response = withContext(IO) {
			userApi.messages(contactId)
		} ?: throw IOException()
		return response.toMutableList()
	}
}
