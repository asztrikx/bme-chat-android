package hu.bme.aut.android.chat.connection

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import kotlin.properties.Delegates

object SessionProvider {
	private const val KEY_USERID = "userId"
	private const val KEY_TOKEN = "token"

	private lateinit var preferences: SharedPreferences
	var listeners: MutableList<() -> Unit> = mutableListOf()

	init {
		listeners.add(::saveSession)
	}

	var session: Session? by Delegates.observable(
		initialValue = null,
		onChange = { property, oldValue, newValue ->
			assert(session == newValue)
			listeners.forEach { it() }
		},
	)

	fun logout() {
		session = null
	}

	fun injectPreferences(preferences: SharedPreferences) {
		this.preferences = preferences
		loadSession()
	}

	private fun loadSession() {
		try {
			if (preferences.contains(KEY_TOKEN)) {
				val userId = preferences.getInt(KEY_USERID, -1)
				val token = preferences.getString(KEY_TOKEN, null)!!
				session = Session(userId, token)
			}
		} catch (e: Exception) {
			with (preferences.edit()) {
				remove(KEY_USERID)
				remove(KEY_TOKEN)
			}
			session = null
		}
	}

	private fun saveSession() {
		with (preferences.edit()) {
			if (session == null) {
				remove(KEY_USERID)
				remove(KEY_TOKEN)
			}
			session?.let {
				putInt(KEY_USERID, it.userId)
				putString(KEY_TOKEN, it.token)
			}
			apply()
		}
	}
}