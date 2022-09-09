package com.candle.streams_player_mvvm.ui.login

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import com.candle.streams_player_mvvm.R
import com.candle.streams_player_mvvm.model.LoggedInUser
import com.candle.streams_player_mvvm.model.Stream
import com.candle.streams_player_mvvm.ui.MainActivity
import com.candle.streams_player_mvvm.util.DataState
import com.candle.streams_player_mvvm.util.PreferenceHelper
import com.candle.streams_player_mvvm.util.PreferenceHelper.isAdmin
import com.candle.streams_player_mvvm.util.PreferenceHelper.user
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        val username = username
        val password = password
        val login = login
        val loading = loading

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)


        subscribeObservers()

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                loginViewModel.login(username.text.toString(), password.text.toString())
            }
        }
    }

    private fun subscribeObservers() {

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.dataState.observe(this, Observer { dataState ->
            when (dataState) {
                is DataState.Success<LoggedInUser> -> {
                    displayLoadingDialog(false)
                    navigateToMainActivity(dataState.data)
                }
                is DataState.Loading -> {
                    displayLoadingDialog(true)
                }
                is DataState.Error -> {
                    displayLoadingDialog(false)
                    displayError(dataState.exception.message)
                }
            }
        })
    }

    private fun navigateToMainActivity(data: LoggedInUser) {
        updateUiWithUser(data)
        val intent = Intent(this,MainActivity :: class.java)
        startActivity(intent)
        finish()
    }

    private fun displayLoadingDialog(isLoading: Boolean) {
        if(isLoading)
         loading.visibility = View.VISIBLE
        else
            loading.visibility = View.GONE

    }


    private fun displayError(message: String?) {
        if (message.isNullOrEmpty()) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Unknown error", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateUiWithUser(model: LoggedInUser) {
        val welcome = getString(R.string.welcome)
        val displayName = model.userId
        PreferenceHelper.customPreference(this).user = model.userId
        PreferenceHelper.customPreference(this).isAdmin = model.isAdmin
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}