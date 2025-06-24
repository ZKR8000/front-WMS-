package com.tonentreprise.wms.network

import com.tonentreprise.wms.model.SalesOrderLightDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// ✅ Requête d'authentification
data class LoginRequest(
    val email: String,
    val password: String
)

// ✅ Réponse d'authentification (adaptée à ce que ton backend retourne)
data class LoginResponse(
    val token: String
)

// ✅ Interface Retrofit pour les appels API
interface ApiService {

    @POST("/api/auth/login") // ✅ URL exacte d'après ton backend
    fun login(
        @Body request: LoginRequest
    ): Call<LoginResponse>

    @GET("sales-orders/Light")
    suspend fun getAllSalesOrdersLight(): List<SalesOrderLightDTO>
}
