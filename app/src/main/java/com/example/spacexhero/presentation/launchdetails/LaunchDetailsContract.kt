package com.example.spacexhero.presentation.launchdetails

import com.example.spacexhero.domain.model.LaunchDetails

sealed class LaunchDetailsEvent {
    data class LoadLaunchDetails(val launchId: String) : LaunchDetailsEvent()
    object OnBackPressed : LaunchDetailsEvent()
}

data class LaunchDetailsState(
    val launchDetails: LaunchDetails? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class LaunchDetailsEffect {
    object NavigateBack : LaunchDetailsEffect()
    data class ShowError(val message: String) : LaunchDetailsEffect()
}

