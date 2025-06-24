package com.tonentreprise.wms.network

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // Récupération du token depuis SharedPreferences
        val prefs = context.getSharedPreferences("WMS_PREFS", Context.MODE_PRIVATE)
        val token = prefs.getString("jwt_token", null)

        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}
