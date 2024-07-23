package com.example.dicodingstory.ui.detailstory

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.example.dicodingstory.databinding.ActivityDetailStoryBinding
import com.example.dicodingstory.ui.ViewModelFactory
import com.example.dicodingstory.ui.auth.pref.AuthenticationViewModel
import com.example.dicodingstory.ui.welcome.WelcomeActivity

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding
    private val authenticationViewModel: AuthenticationViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authenticationViewModel.getSession().observe(this) { loginResult ->
            if (loginResult.token.isNotEmpty()) {
                setDetailStory()
            }
            else {
                navigateToAnotherPage(WelcomeActivity::class.java)
            }
        }
    }

    private fun setDetailStory () {
        try {
            val photoUrl = intent.getStringExtra("photoUrl")
            val name = intent.getStringExtra("name")
            val description = intent.getStringExtra("description")

            binding.progressIndicator.visibility = View.VISIBLE

            binding.tvDetailDescription.text = description
            binding.tvDetailName.text = name
            Glide.with(this)
                .load(photoUrl)
                .into(binding.ivDetailPhoto)

            binding.progressIndicator.visibility = View.GONE
        } catch (e: Exception) {
            binding.progressIndicator.visibility = View.GONE
            Toast.makeText(
                this,
                "Error loading story details",
                Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun navigateToAnotherPage(destination: Class<*>) {
        val intent = Intent(this, destination)
        startActivity(intent)
        finish()
    }
}