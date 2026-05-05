package dev.saragones3.genogramia.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.saragones3.genogramia.domain.usecase.CheckSessionUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class SplashViewModel(
    private val checkSession: CheckSessionUseCase,
) : ViewModel() {
    val uiState: StateFlow<SplashUiState> =
        flow {
            emit(SplashUiState.Loading)
            val user = checkSession()
            emit(
                if (user != null) {
                    SplashUiState.NavigateToAuthenticatedHome
                } else {
                    SplashUiState.NavigateToGuestHome
                },
            )
        }.stateIn(viewModelScope, SharingStarted.Eagerly, SplashUiState.Loading)
}
