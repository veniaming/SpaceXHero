package com.example.spacexhero.presentation.launches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.spacexhero.domain.model.Launch
import com.example.spacexhero.domain.usecase.GetLaunchesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class LaunchesViewModel @Inject constructor(
    private val getLaunchesUseCase: GetLaunchesUseCase
) : ViewModel() {

    private val _effect = Channel<LaunchesEffect>()
    val effect = _effect.receiveAsFlow()

    // Emitting a new value triggers a full refresh of the PagingSource
    private val refreshTrigger = MutableSharedFlow<Boolean>(replay = 1).apply {
        tryEmit(false)
    }

    val launchPagingData: Flow<PagingData<Launch>> = refreshTrigger
        .flatMapLatest { forceRefresh -> getLaunchesUseCase(forceRefresh = forceRefresh) }
        .cachedIn(viewModelScope)

    fun handleEvent(event: LaunchesEvent) {
        when (event) {
            is LaunchesEvent.RefreshLaunches -> {
                viewModelScope.launch {
                    refreshTrigger.emit(true)
                }
            }
            is LaunchesEvent.OnLaunchClicked -> {
                viewModelScope.launch {
                    _effect.send(LaunchesEffect.NavigateToDetails(event.launchId))
                }
            }
        }
    }
}

