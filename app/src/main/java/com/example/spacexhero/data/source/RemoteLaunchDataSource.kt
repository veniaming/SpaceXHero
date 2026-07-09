package com.example.spacexhero.data.source

import com.example.spacexhero.data.network.NetworkResult
import com.example.spacexhero.data.network.SpaceXApiService
import com.example.spacexhero.domain.model.Launch
import com.example.spacexhero.domain.model.LaunchDetails
import javax.inject.Inject

class RemoteLaunchDataSource @Inject constructor(
    private val apiService: SpaceXApiService
) {

    private suspend fun <T> safeApiCall(block: suspend () -> T): NetworkResult<T> {
        return try {
            val result = block()
            NetworkResult.Success(result)
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }

    suspend fun getLaunches(offset: Int = 0): List<Launch> {
        when (val result = safeApiCall { apiService.getLaunches(offset = offset) }) {
            is NetworkResult.Success -> {
                return result.data.results.map { it.toDomain() }
            }
            is NetworkResult.Error -> {
                // propagate the exception so repository can handle fallback
                throw result.exception
            }
        }
    }

    suspend fun getLaunchDetails(launchId: String): LaunchDetails {
        when (val result = safeApiCall { apiService.getLaunchDetails(launchId) }) {
            is NetworkResult.Success -> {
                return result.data.toDomain()
            }
            is NetworkResult.Error -> {
                throw result.exception
            }
        }
    }
}


