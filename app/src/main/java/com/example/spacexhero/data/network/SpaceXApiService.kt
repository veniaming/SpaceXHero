package com.example.spacexhero.data.network

import com.example.spacexhero.data.model.ApiLaunchDetails
import com.example.spacexhero.data.model.ApiLaunchListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SpaceXApiService {
    @GET("2.0.0/launch/")
    suspend fun getLaunches(
        @Query("lsp__name") launchProvider: String = "SpaceX",
        @Query("offset") offset: Int = 0,
        @Query("ordering") ordering: String = "-net"
    ): ApiLaunchListResponse

    @GET("2.0.0/launch/{id}/")
    suspend fun getLaunchDetails(
        @Path("id") launchId: String
    ): ApiLaunchDetails
}
