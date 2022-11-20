package hu.bme.aut.android.chat.network.socket

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import hu.bme.aut.android.chat.contacts.ContactBrief
import hu.bme.aut.android.chat.messages.Message
import hu.bme.aut.android.chat.messages.NewMessage
import hu.bme.aut.android.chat.network.rest.NetworkManager
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import kotlin.reflect.KClass

/**
 * Websocket manager
 */
object WebsocketManager: WebSocketListener() {
	val gson = Gson()

	/**
	 * Websocket message listeners
	 */
	val contactBriefListeners = mutableListOf<(ContactBrief) -> Unit>()
	val messageListeners = mutableListOf<(Message) -> Unit>()

	/**
	 * Creates connection to websocket endpoint
	 */
	private lateinit var client: OkHttpClient
	private lateinit var websocket: WebSocket
	fun start(client: OkHttpClient) {
		this.client = client
		this.websocket = client.newWebSocket(
			Request.Builder().url(NetworkManager.SERVICE_URL + "/ws").build(),
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
			contactBriefListeners.forEach {
				it(contactBrief)
			}
		} catch (e: JsonSyntaxException) {}

		try {
			val message = gson.fromJson(text, Message::class.java)
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
		start(client)
	}
}