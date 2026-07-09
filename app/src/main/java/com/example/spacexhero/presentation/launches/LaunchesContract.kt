package com.example.spacexhero.presentation.launches

sealed class LaunchesEvent {
    object RefreshLaunches : LaunchesEvent()
    data class OnLaunchClicked(val launchId: String) : LaunchesEvent()
}


sealed class LaunchesEffect {
    data class NavigateToDetails(val launchId: String) : LaunchesEffect()
    data class ShowError(val message: String) : LaunchesEffect()
}

