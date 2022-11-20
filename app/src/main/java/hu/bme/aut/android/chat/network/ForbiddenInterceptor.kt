package hu.bme.aut.android.chat.network

import hu.bme.aut.android.chat.session.SessionProvider
import okhttp3.Interceptor
import okhttp3.Response
import java.util.*

/**
 * Handles invalid session state
 */
class ForbiddenInterceptor : Interceptor {
	override fun intercept(chain: Interceptor.Chain): Response {
		val request = chain.request()
		val response = chain.proceed(request)
		if (response.code() == 401) {
			// do not call logout as we don't have a correct session
			SessionProvider.session = null
		}
		return response
	}
}