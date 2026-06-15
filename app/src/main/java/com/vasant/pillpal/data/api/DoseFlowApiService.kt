package com.vasant.pillpal.data.api

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Path
import retrofit2.http.Header

interface DoseFlowApiService {

    @POST("api/auth/signup")
    suspend fun signUp(@Body request: AuthRequest): AuthResponse

    @POST("api/auth/signin")
    suspend fun signIn(@Body request: AuthRequest): AuthResponse

    @GET("api/medicines")
    suspend fun getMedicines(@Header("Authorization") token: String): List<MedicineDto>

    @POST("api/medicines")
    suspend fun createMedicine(
        @Header("Authorization") token: String,
        @Body medicine: MedicineDto
    ): MedicineDto

    @PUT("api/medicines/{id}")
    suspend fun updateMedicine(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body medicine: MedicineDto
    ): MedicineDto

    @DELETE("api/medicines/{id}")
    suspend fun deleteMedicine(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Map<String, String>

    @POST("api/medicines/sync")
    suspend fun syncMedicines(
        @Header("Authorization") token: String,
        @Body request: SyncRequest
    ): List<MedicineDto>
}

data class AuthRequest(val email: String, val password: String)
data class UserDto(val id: String, val email: String)
data class AuthResponse(val token: String, val user: UserDto)
data class MedicineDto(
    val id: Int,
    val medName: String,
    val time: Long,
    val dosage: String,
    val note: String?,
    val isCompleted: Boolean,
    val medType: String?
)
data class SyncRequest(val medicines: List<MedicineDto>)
