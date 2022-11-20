package hu.bme.aut.android.chat.session

import android.content.SharedPreferences

/**
 * SharedPreferences manager
 */
object PreferenceManager {
	// Preferences keys
	private const val KEY_USERID = "userId"
	private const val KEY_TOKEN = "token"

	/**
	 * Loads the session from SharedPreferences
	 */
	fun loadSession(preferences: SharedPreferences) {
		try {
			if (preferences.contains(KEY_USERID) && preferences.contains(KEY_TOKEN)) {
				val userId = preferences.getInt(KEY_USERID, -1)
				val token = preferences.getString(KEY_TOKEN, null)!!
				SessionProvider.session = Session(userId, token)
			}
		} catch (e: Exception) {
			// Clear preferences if it is malformed
			with (preferences.edit()) {
				remove(KEY_USERID)
				remove(KEY_TOKEN)
			}
			SessionProvider.session = null
		}
	}

	/**
	 * Saves the session into SharedPreferences
	 */
	fun saveSession(preferences: SharedPreferences) {
		with (preferences.edit()) {
			if (SessionProvider.session == null) {
				remove(KEY_USERID)
				remove(KEY_TOKEN)
			}
			SessionProvider.session?.let {
				putInt(KEY_USERID, it.userId)
				putString(KEY_TOKEN, it.token)
			}
			apply()
		}
	}
}