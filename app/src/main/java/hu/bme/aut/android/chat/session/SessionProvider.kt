package hu.bme.aut.android.chat.session

import android.content.SharedPreferences
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
	fun logout() {
		session = null
	}
}