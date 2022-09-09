package com.candle.streams_player_mvvm.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import androidx.lifecycle.viewModelScope
import com.candle.streams_player_mvvm.R
import com.candle.streams_player_mvvm.model.LoggedInUser
import com.candle.streams_player_mvvm.model.Stream
import com.candle.streams_player_mvvm.repository.LoginRepository
import com.candle.streams_player_mvvm.ui.MainStateEvent
import com.candle.streams_player_mvvm.util.DataState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm


    private val _dataState: MutableLiveData<DataState<LoggedInUser>> = MutableLiveData()

    val dataState: LiveData<DataState<LoggedInUser>>
        get() = _dataState


    fun login(username: String, password: String) {

        viewModelScope.launch {
            loginRepository.login(username, password)
                .onEach { dataState ->
                    _dataState.value = dataState
                }
                .launchIn(viewModelScope)
        }

    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}