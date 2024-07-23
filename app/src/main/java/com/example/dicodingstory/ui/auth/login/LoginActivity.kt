package com.example.dicodingstory.ui.auth.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.example.dicodingstory.component.CustomButton
import com.example.dicodingstory.databinding.ActivityLoginBinding
import com.example.dicodingstory.ui.ViewModelFactory
import com.example.dicodingstory.data.remote.Result
import com.example.dicodingstory.ui.liststory.MainActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val customButton: CustomButton = binding.btnLogin

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = binding.edLoginEmail.text.toString()
                val password = binding.edLoginPassword.text.toString()

                customButton.setEnabledState(email, password, isRegister = false)
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }

        binding.edLoginEmail.addTextChangedListener(textWatcher)
        binding.edLoginPassword.addTextChangedListener(textWatcher)

        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()
            viewModel.login(email, password).observe(this) { result ->
                when (result) {
                    is Result.Loading -> {
                        binding.progressIndicator.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressIndicator.visibility = View.GONE
                        Toast.makeText(
                            this,
                            "Login successful: ${result.data.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        navigateToAnotherPage(MainActivity::class.java)
                    }
                    is Result.Error -> {
                        binding.progressIndicator.visibility = View.GONE
                        Toast.makeText(
                            this,
                            "Login failed: ${result.error}",
                            Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }

        playAnimation()
    }

    private fun navigateToAnotherPage(destination: Class<*>) {
        val intent = Intent(this, destination)
        startActivity(intent)
        finish()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivLogin, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 1f).setDuration(100)
        val message = ObjectAnimator.ofFloat(binding.tvMessage, View.ALPHA, 1f).setDuration(100)
        val emailTextView = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 0f, 1f).setDuration(100)
        val emailEditTextLayout = ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 0f, 1f).setDuration(100)
        val passwordTextView = ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 0f, 1f).setDuration(100)
        val passwordEditTextLayout = ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 0f, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 0f, 1f).setDuration(100)

        val email = AnimatorSet().apply {
            playTogether(emailTextView, emailEditTextLayout)
        }

        val password = AnimatorSet().apply {
            playTogether(passwordTextView, passwordEditTextLayout)
        }

        AnimatorSet().apply {
            playSequentially(title, message, email, password, login)
            startDelay = 100
            start()
        }
    }
}
