package hu.bme.aut.android.chat.network.rest

import hu.bme.aut.android.chat.session.SessionProvider
import okhttp3.Interceptor
import okhttp3.Response
import java.util.*

/**
 * Appends session token to request when there is a session
 */
class SessionInterceptor : Interceptor {
	override fun intercept(chain: Interceptor.Chain): Response {
		SessionProvider.session?.let {
			// Base64 token to escape disallowed characters
			val tokenEncoded = Base64.getEncoder().encodeToString(it.token.toByteArray())

			// Create HTTP basic auth header value
			val basicHTTPAuthDecoded = "${it.userId}:${tokenEncoded}"
			val basicHTTPAuthEncoded = Base64.getEncoder().encodeToString(basicHTTPAuthDecoded.toByteArray())

			val request = chain.request()
				.newBuilder()
				.addHeader("Authorization", "Basic $basicHTTPAuthEncoded")
				.build()

			return chain.proceed(request)
		}
		return chain.proceed(chain.request())
	}
}