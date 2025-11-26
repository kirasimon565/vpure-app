package com.vpure.app

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.vpure.app.api.ApiClient
import com.vpure.app.api.AuthRequest
import com.vpure.app.api.AuthService
import com.vpure.app.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val authService: AuthService by lazy {
        ApiClient.getClient(this).create(AuthService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerButton.setOnClickListener {
            handleRegister()
        }
    }

    private fun handleRegister() {
        val name = binding.nameInput.text.toString().trim()
        val email = binding.emailInput.text.toString().trim()
        val password = binding.passwordInput.text.toString()
        val confirmPassword = binding.confirmPasswordInput.text.toString()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError("All fields are required.")
            return
        }

        if (password != confirmPassword) {
            showError("Passwords do not match.")
            return
        }

        toggleLoading(true)

        lifecycleScope.launch {
            try {
                val response = authService.register(AuthRequest(email, password, name))
                if (response.isSuccessful) {
                    Snackbar.make(binding.root, "Registration successful! Please log in.", Snackbar.LENGTH_LONG).show()
                    // Finish this activity and go back to Login
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    showError("Registration failed: $errorBody")
                }
            } catch (e: Exception) {
                showError("An error occurred: ${e.message}")
            } finally {
                toggleLoading(false)
            }
        }
    }

    private fun toggleLoading(isLoading: Boolean) {
        binding.loading.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.registerButton.isEnabled = !isLoading
        binding.nameInput.isEnabled = !isLoading
        binding.emailInput.isEnabled = !isLoading
        binding.passwordInput.isEnabled = !isLoading
        binding.confirmPasswordInput.isEnabled = !isLoading
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}
