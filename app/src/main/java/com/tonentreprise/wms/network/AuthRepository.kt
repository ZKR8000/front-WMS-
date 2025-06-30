package com.tonentreprise.wms.network

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthRepository {

    fun loginUser(
        context: Context,
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val request = LoginRequest(email, password)
        val api = RetrofitClient.getInstance(context)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // ✅ Appel suspend (ne retourne plus Call<>)
                val response = api.login(request)
                val token = response.token

                // ✅ Sauvegarde du token dans SharedPreferences
                val prefs = context.getSharedPreferences("WMS_PREFS", Context.MODE_PRIVATE)
                prefs.edit().putString("jwt_token", token).apply()

                withContext(Dispatchers.Main) {
                    onResult(true, token)
                }
            } catch (e: Exception) {
                Log.e("AuthRepo", "Erreur: ${e.message}")
                withContext(Dispatchers.Main) {
                    onResult(false, null)
                }
            }
        }
    }
}
