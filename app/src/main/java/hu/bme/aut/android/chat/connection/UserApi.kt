package hu.bme.aut.android.chat.connection

import hu.bme.aut.android.chat.contacts.ContactBrief
import hu.bme.aut.android.chat.contacts.ContactPostResponse
import hu.bme.aut.android.chat.messages.Message
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserApi {
	@POST("${NetworkManager.API_PATH}/login")
	suspend fun login(@Body loginRequest: LoginRequest): Session?

	@POST("${NetworkManager.API_PATH}/register")
	suspend fun register(@Body registerRequest: RegisterRequest): RegisterResponse?

	@GET("${NetworkManager.API_PATH}/contact")
	suspend fun contacts(): List<ContactBrief>?

	@GET("${NetworkManager.API_PATH}/message/{contactId}")
	suspend fun messages(@Path("contactId") id: Int): List<Message>?

	@POST("${NetworkManager.API_PATH}/contact")
	suspend fun addContact(@Body username: String): ContactPostResponse?
}