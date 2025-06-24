package com.tonentreprise.wms.network

import android.content.Context
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository {

    fun loginUser(
        context: Context,
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val request = LoginRequest(email, password)

        val api = RetrofitClient.getInstance(context)
        api.login(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val token = response.body()!!.token
                    // ✅ Enregistrer le token
                    val prefs = context.getSharedPreferences("WMS_PREFS", Context.MODE_PRIVATE)
                    prefs.edit().putString("jwt_token", token).apply()

                    onResult(true, token)
                } else {
                    Log.e("AuthRepo", "Échec login: ${response.code()}")
                    onResult(false, null)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("AuthRepo", "Erreur réseau: ${t.message}")
                onResult(false, null)
            }
        })
    }
}
