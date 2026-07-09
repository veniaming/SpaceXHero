package com.example.spacexhero.domain.repository

import androidx.paging.PagingData
import com.example.spacexhero.domain.model.Launch
import com.example.spacexhero.domain.model.LaunchDetails
import kotlinx.coroutines.flow.Flow

interface LaunchRepository {
    fun getLaunchesPagingData(forceRefresh: Boolean = false): Flow<PagingData<Launch>>
    suspend fun getLaunchById(id: String): Result<LaunchDetails>
}

