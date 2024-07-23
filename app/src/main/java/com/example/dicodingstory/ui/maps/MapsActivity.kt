package com.example.dicodingstory.ui.maps

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.example.dicodingstory.R
import com.example.dicodingstory.data.remote.Result
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.dicodingstory.databinding.ActivityMapsBinding
import com.example.dicodingstory.ui.ViewModelFactory
import com.example.dicodingstory.ui.auth.pref.AuthenticationViewModel
import com.example.dicodingstory.ui.welcome.WelcomeActivity
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val viewModel: MapsViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private val authenticationViewModel: AuthenticationViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private val boundsBuilder = LatLngBounds.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setMapStyle()

        authenticationViewModel.getSession().observe(this) { loginResult ->
            if (loginResult.token.isNotEmpty()) {
                viewModel.getStoriesLocation().observe(this) { result ->
                    when (result) {
                        is Result.Loading -> {
                            binding.progressIndicator.visibility = View.VISIBLE
                        }
                        is Result.Success -> {
                            binding.progressIndicator.visibility = View.GONE
                            val stories = result.data
                            stories.forEach { story ->
                                val location = LatLng(story.lat!!, story.lon!!)
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(location)
                                        .title(story.name)
                                        .snippet(story.description)
                                )
                                boundsBuilder.include(location)
                            }
                            val bounds: LatLngBounds = boundsBuilder.build()
                            googleMap.animateCamera(
                                CameraUpdateFactory.newLatLngBounds(
                                    bounds,
                                    resources.displayMetrics.widthPixels,
                                    resources.displayMetrics.heightPixels,
                                    300
                                )
                            )
                            Toast.makeText(
                                this,
                                "Successfully loaded maps",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        is Result.Error -> {
                            binding.progressIndicator.visibility = View.GONE
                            Toast.makeText(
                                this,
                                "Failed to load maps: ${result.error}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            } else {
                navigateToAnotherPage(WelcomeActivity::class.java)
            }
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private fun navigateToAnotherPage(destination: Class<*>) {
        val intent = Intent(this, destination)
        startActivity(intent)
        finish()
    }
}