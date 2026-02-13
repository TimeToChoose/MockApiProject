package com.shineofeidos.mockapiproject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shineofeidos.mockapiproject.data.UserRepository
import com.shineofeidos.mockapiproject.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class Success(val users: List<User>) : UiState()
    data class Error(val message: String) : UiState()
}

class MainViewModel : ViewModel() {
    private val repository = UserRepository()

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun loadUsers() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = repository.getUsers()
            result.onSuccess { users ->
                _uiState.value = UiState.Success(users)
            }.onFailure { exception ->
                _uiState.value = UiState.Error(exception.message ?: "未知错误")
            }
        }
    }
}
