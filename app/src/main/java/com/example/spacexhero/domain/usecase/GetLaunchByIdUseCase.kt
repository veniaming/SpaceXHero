package com.example.spacexhero.domain.usecase

import com.example.spacexhero.domain.model.LaunchDetails
import com.example.spacexhero.domain.repository.LaunchRepository
import javax.inject.Inject

class GetLaunchByIdUseCase @Inject constructor(
    private val repository: LaunchRepository
) {
    suspend operator fun invoke(id: String): Result<LaunchDetails> =
        repository.getLaunchById(id)
}

