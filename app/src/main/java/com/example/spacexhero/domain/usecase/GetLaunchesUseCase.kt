package com.example.spacexhero.domain.usecase

import androidx.paging.PagingData
import com.example.spacexhero.domain.model.Launch
import com.example.spacexhero.domain.repository.LaunchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLaunchesUseCase @Inject constructor(
    private val repository: LaunchRepository
) {
    operator fun invoke(forceRefresh: Boolean = false): Flow<PagingData<Launch>> =
        repository.getLaunchesPagingData(forceRefresh = forceRefresh)
}

