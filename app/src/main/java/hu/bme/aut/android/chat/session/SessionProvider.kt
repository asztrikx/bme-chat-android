package hu.bme.aut.android.chat.session

import android.content.SharedPreferences
import android.view.View
import hu.bme.aut.android.chat.network.rest.NetworkManager
import hu.bme.aut.android.chat.network.rest.handleNetworkError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

/**
 * Singleton for session management.
 * injectPreferences should be called before using it.
 */
object SessionProvider {
	/**
	 * This should be called before accessing session.
	 */
	private lateinit var preferences: SharedPreferences
	fun injectPreferences(preferences: SharedPreferences) {
		SessionProvider.preferences = preferences
		PreferenceManager.loadSession(preferences)
	}

	/**
	 * In order to listen for changes in the session one must add a callback function to this array.
	 */
	var listeners: MutableList<() -> Unit> = mutableListOf({
		PreferenceManager.saveSession(preferences)
	})
	var session: Session? by Delegates.observable(
		initialValue = null,
		onChange = { property, oldValue, newValue ->
			listeners.forEach { it() }
		},
	)

	/**
	 * Removes session.
	 */
	fun logout(parentView: View, parentGetString: (Int) -> String) {
		CoroutineScope(Dispatchers.Main).launch {
			try {
				NetworkManager.logout()
				session = null
			} catch (e: Exception) {
				println(e)
				handleNetworkError(parentView, parentGetString)
				return@launch
			}
		}
	}
}