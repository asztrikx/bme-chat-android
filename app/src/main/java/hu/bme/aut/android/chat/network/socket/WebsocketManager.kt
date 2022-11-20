package hu.bme.aut.android.chat.network.socket

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import hu.bme.aut.android.chat.contacts.ContactBrief
import hu.bme.aut.android.chat.messages.Message
import hu.bme.aut.android.chat.messages.NewMessage
import hu.bme.aut.android.chat.network.NetworkManager
import hu.bme.aut.android.chat.network.rest.RestManager
import hu.bme.aut.android.chat.session.SessionProvider
import okhttp3.*
import kotlin.reflect.KClass

/**
 * Websocket manager
 */
object WebsocketManager: WebSocketListener() {
	private val gson = Gson()
	private const val URL = NetworkManager.SERVICE_URL + "/ws"

	/**
	 * Websocket message listeners
	 */
	val contactBriefListeners = mutableListOf<(ContactBrief) -> Unit>()
	val messageListeners = mutableListOf<(Message) -> Unit>()

	fun init(client: OkHttpClient) {
		this.client = client
		SessionProvider.listeners.add(::onSessionChange)
	}

	private fun onSessionChange() {
		if (SessionProvider.session == null) {
			websocket.close(1000, "")
		} else {
			start()
		}
	}

	/**
	 * Creates connection to websocket endpoint
	 */
	private lateinit var client: OkHttpClient
	private lateinit var websocket: WebSocket
	fun start() {
		this.websocket = client.newWebSocket(
			Request.Builder().url(URL).build(),
			WebsocketManager,
		)
	}

	/**
	 * Websocket data senders
	 */

	fun sendNewMessage(newMessage: NewMessage): Boolean {
		return websocket.send(gson.toJson(newMessage))
	}

	/**
	 * Websocket data receivers
	 */
	override fun onMessage(webSocket: WebSocket, text: String) {
		try {
			val contactBrief = gson.fromJson(text, ContactBrief::class.java)
			// Gson doesn't support strict parsing
			if (contactBrief.id == 0) throw JsonSyntaxException("")
			contactBriefListeners.forEach {
				it(contactBrief)
			}
		} catch (e: JsonSyntaxException) {}

		try {
			val message = gson.fromJson(text, Message::class.java)
			// Gson doesn't support strict parsing
			if (message.fromUserId == 0) throw JsonSyntaxException("")
			messageListeners.forEach {
				it(message)
			}
		} catch (e: JsonSyntaxException) {}
	}

	/**
	 * Auto restart when connection is closed
	 */
	override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
		webSocket.close(1000, null)
		if (SessionProvider.session != null) {
			start()
		}
	}
}