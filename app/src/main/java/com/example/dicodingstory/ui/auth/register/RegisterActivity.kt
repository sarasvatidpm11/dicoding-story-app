package com.example.dicodingstory.ui.auth.register

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
import com.example.dicodingstory.databinding.ActivityRegisterBinding
import com.example.dicodingstory.data.remote.Result
import com.example.dicodingstory.ui.ViewModelFactory
import com.example.dicodingstory.ui.auth.login.LoginActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val customButton: CustomButton = binding.btnRegister

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val name = binding.edRegisterName.text.toString()
                val email = binding.edRegisterEmail.text.toString()
                val password = binding.edRegisterPassword.text.toString()

                customButton.setEnabledState(email, password, name, isRegister = true)
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }

        binding.edRegisterName.addTextChangedListener(textWatcher)
        binding.edRegisterEmail.addTextChangedListener(textWatcher)
        binding.edRegisterPassword.addTextChangedListener(textWatcher)


        binding.btnRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            viewModel.register(name, email, password).observe(this) { result ->
                when (result) {
                    is Result.Loading -> {
                        binding.progressIndicator.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressIndicator.visibility = View.GONE
                        Toast.makeText(
                            this,
                            "Registration successful: ${result.data.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        navigateToAnotherPage(LoginActivity::class.java)
                    }
                    is Result.Error -> {
                        binding.progressIndicator.visibility = View.GONE
                        Toast.makeText(
                            this,
                            "Registration failed: ${result.error}",
                            Toast.LENGTH_LONG
                        ).show()
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
        ObjectAnimator.ofFloat(binding.ivRegister, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.tvSignup, View.ALPHA, 1f).setDuration(100)
        val nameTextView = ObjectAnimator.ofFloat(binding.tvName, View.ALPHA, 0f, 1f).setDuration(100)
        val nameEditTextLayout = ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 0f, 1f).setDuration(100)
        val emailTextView = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 0f, 1f).setDuration(100)
        val emailEditTextLayout = ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 0f, 1f).setDuration(100)
        val passwordTextView = ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 0f, 1f).setDuration(100)
        val passwordEditTextLayout = ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 0f, 1f).setDuration(100)
        val register = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 0f, 1f).setDuration(100)

        val name = AnimatorSet().apply {
            playTogether(nameTextView, nameEditTextLayout)
        }

        val email = AnimatorSet().apply {
            playTogether(emailTextView, emailEditTextLayout)
        }

        val password = AnimatorSet().apply {
            playTogether(passwordTextView, passwordEditTextLayout)
        }

        AnimatorSet().apply {
            playSequentially(title, name, email, password, register)
            startDelay = 100
            start()
        }
    }
}

