package hu.bme.aut.android.chat.network

import hu.bme.aut.android.chat.network.rest.Api
import hu.bme.aut.android.chat.network.socket.WebsocketManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkManager {
	lateinit var api: Api

	private const val DOMAIN = "ih1mo3bito2bcjitruqrex5l8.gimid.hu:6767"
	const val SERVICE_URL = "http://$DOMAIN"

	fun init() {
		// Create REST and Websocket managers from HttpClient
		val client = OkHttpClient.Builder()
			// Session token always has to be appended
			.addInterceptor(SessionInterceptor())
			.addInterceptor(ForbiddenInterceptor())
			.build()

		WebsocketManager.init(client)

		api = Retrofit.Builder()
			.baseUrl(SERVICE_URL)
			.client(client)
			.addConverterFactory(GsonConverterFactory.create())
			.build()
			.create(Api::class.java)
	}
}