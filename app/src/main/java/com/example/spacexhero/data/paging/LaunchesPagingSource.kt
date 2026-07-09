package com.example.spacexhero.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.spacexhero.data.source.RemoteLaunchDataSource
import com.example.spacexhero.data.source.cache.LaunchCacheManager
import com.example.spacexhero.domain.model.Launch

class LaunchesPagingSource(
    private val remoteDataSource: RemoteLaunchDataSource,
    private val cacheManager: LaunchCacheManager,
    private val forceRefresh: Boolean = false
) : PagingSource<Int, Launch>() {

    companion object {
        const val PAGE_SIZE = 10
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Launch> {
        val offset = params.key ?: 0
        return try {
            val cached = if (forceRefresh) null else cacheManager.getLaunches(offset)
            val launches: List<Launch> = if (cached != null) {
                cached
            } else {
                // Cache miss (or forced refresh) — fetch from network and persist.
                val domainList = remoteDataSource.getLaunches(offset = offset)
                cacheManager.saveLaunches(offset, domainList)
                domainList
            }

            LoadResult.Page(
                data = launches,
                prevKey = if (offset == 0) null else offset - PAGE_SIZE,
                nextKey = if (launches.size < PAGE_SIZE) null else offset + PAGE_SIZE
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Launch>): Int? {
        return state.anchorPosition?.let { anchor ->
            val closestPage = state.closestPageToPosition(anchor)
            closestPage?.prevKey?.plus(PAGE_SIZE)
                ?: closestPage?.nextKey?.minus(PAGE_SIZE)
        }
    }
}






