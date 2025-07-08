package com.tonentreprise.wms.network

import com.tonentreprise.wms.model.ProductDTO
import com.tonentreprise.wms.model.SalesOrderLightDTO

import retrofit2.Response
import retrofit2.http.Path

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

    @POST("/api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse


    @GET("/api/sales-orders/light")
    suspend fun getAllSalesOrdersLight(): List<SalesOrderLightDTO>


    interface ProductService {
        @GET("get/{artnum}")
        suspend fun getProductById(@Path("artnum") artnum: String): Response<ProductDTO>
    }


}
