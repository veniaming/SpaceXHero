package com.example.spacexhero.presentation.launchdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spacexhero.domain.usecase.GetLaunchByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LaunchDetailsViewModel @Inject constructor(
    private val getLaunchByIdUseCase: GetLaunchByIdUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LaunchDetailsState())
    val state: StateFlow<LaunchDetailsState> = _state.asStateFlow()

    private val _effect = Channel<LaunchDetailsEffect>()
    val effect = _effect.receiveAsFlow()

    fun handleEvent(event: LaunchDetailsEvent) {
        when (event) {
            is LaunchDetailsEvent.LoadLaunchDetails -> loadLaunchDetails(event.launchId)
            is LaunchDetailsEvent.OnBackPressed -> navigateBack()
        }
    }

    private fun loadLaunchDetails(launchId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = getLaunchByIdUseCase(launchId)
            result.onSuccess { launchDetails ->
                _state.value = _state.value.copy(
                    launchDetails = launchDetails,
                    isLoading = false
                )
            }
            result.onFailure { error ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = error.message
                )
                _effect.send(LaunchDetailsEffect.ShowError(error.message ?: "Unknown error"))
            }
        }
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _effect.send(LaunchDetailsEffect.NavigateBack)
        }
    }
}

