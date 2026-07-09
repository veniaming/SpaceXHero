package com.example.spacexhero.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.spacexhero.data.paging.LaunchesPagingSource
import com.example.spacexhero.data.source.RemoteLaunchDataSource
import com.example.spacexhero.data.source.cache.LaunchCacheManager
import com.example.spacexhero.domain.model.Launch
import com.example.spacexhero.domain.model.LaunchDetails
import com.example.spacexhero.domain.repository.LaunchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LaunchRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteLaunchDataSource,
    private val cacheManager: LaunchCacheManager
) : LaunchRepository {

    override fun getLaunchesPagingData(forceRefresh: Boolean): Flow<PagingData<Launch>> {
        return Pager(
            config = PagingConfig(
                pageSize = LaunchesPagingSource.PAGE_SIZE,
                enablePlaceholders = false,
                prefetchDistance = 3
            ),
            pagingSourceFactory = {
                LaunchesPagingSource(
                    remoteDataSource = remoteDataSource,
                    cacheManager = cacheManager,
                    forceRefresh = forceRefresh
                )
            }
        ).flow
    }

    override suspend fun getLaunchById(id: String): Result<LaunchDetails> {
        return try {
            val cachedLaunch = cacheManager.getLaunchDetails(id)
            if (cachedLaunch != null) {
                return Result.success(cachedLaunch)
            }

            val launchDetails = try {
                remoteDataSource.getLaunchDetails(id).also { remoteLaunch ->
                    cacheManager.saveLaunchDetails(id, remoteLaunch)
                }
            } catch (e: Exception) {
                throw IllegalArgumentException("Launch not found: ${e.message}")
            }

            Result.success(launchDetails)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

