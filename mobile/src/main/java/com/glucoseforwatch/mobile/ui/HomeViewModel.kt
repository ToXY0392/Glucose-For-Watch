package com.glucoseforwatch.mobile.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Exposes [HomeUiState] and refreshes on demand. */
class HomeViewModel(
    private val loader: HomeStateLoader = HomeStateLoader(),
) : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState?>(null)
    val uiState: StateFlow<HomeUiState?> = _uiState.asStateFlow()

    fun refresh(context: Context) {
        viewModelScope.launch {
            _uiState.value = loader.load(context)
        }
    }
}
