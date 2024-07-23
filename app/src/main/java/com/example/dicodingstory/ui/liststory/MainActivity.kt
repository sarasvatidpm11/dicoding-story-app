package com.example.dicodingstory.ui.liststory

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingstory.data.local.entity.StoryEntity
import com.example.dicodingstory.databinding.ActivityMainBinding
import com.example.dicodingstory.ui.ViewModelFactory
import com.example.dicodingstory.ui.auth.pref.AuthenticationViewModel
import com.example.dicodingstory.ui.detailstory.DetailStoryActivity
import com.example.dicodingstory.ui.maps.MapsActivity
import com.example.dicodingstory.ui.newstory.NewStoryActivity
import com.example.dicodingstory.ui.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private lateinit var adapter: StoryAdapter
    private val viewModel: ListStoryViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private val authenticationViewModel: AuthenticationViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        adapter = StoryAdapter()
        binding?.rvStory?.layoutManager = LinearLayoutManager(this)
        binding?.rvStory?.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter { adapter.retry() }
        )

        authenticationViewModel.getSession().observe(this) { loginResult ->
            Log.d("MainActivity", "Observed session: token=${loginResult.token}")
            if (loginResult.token.isNotEmpty()) {
                setStoryList()
            } else {
                navigateToAnotherPage(WelcomeActivity::class.java)
                finish()
            }
        }

        binding?.fabAdd?.setOnClickListener {
            navigateToAnotherPage(NewStoryActivity::class.java)
        }

        binding?.ivLogout?.setOnClickListener {
            authenticationViewModel.clearSession()
        }

        binding?.ivMaps?.setOnClickListener {
            navigateToAnotherPage(MapsActivity::class.java)
        }

        adapter.setOnItemClickCallback(object : StoryAdapter.OnItemClickCallback {
            override fun onItemClicked(storyEntity: StoryEntity) {
                navigateToDetailStory(storyEntity)
            }
        })
    }

    private fun setStoryList() {
        viewModel.getStories().observe(this){ stories->
            adapter.submitData(lifecycle,stories)
            if(stories == null){
                binding?.tvNoData?.visibility=View.VISIBLE
            }else{
                binding?.tvNoData?.visibility = View.GONE
                Toast.makeText(
                    this,
                    "Stories loaded successfully!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun navigateToAnotherPage(destination: Class<*>) {
        val intent = Intent(this, destination)
        startActivity(intent)
    }

    private fun navigateToDetailStory(data : StoryEntity) {
        val intent = Intent(this, DetailStoryActivity::class.java).apply {
            putExtra("photoUrl", data.photoUrl)
            putExtra("name", data.name)
            putExtra("description", data.description)
        }
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}
