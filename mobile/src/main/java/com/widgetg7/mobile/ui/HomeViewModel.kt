package com.widgetg7.mobile.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class HomeViewModel(
    private val loader: HomeStateLoader = HomeStateLoader(),
) : ViewModel() {
    private val _uiState = MutableLiveData<HomeUiState>()
    val uiState: LiveData<HomeUiState> = _uiState

    fun refresh(context: Context) {
        viewModelScope.launch {
            _uiState.value = loader.load(context)
        }
    }
}
