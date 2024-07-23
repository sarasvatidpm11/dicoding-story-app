package com.example.dicodingstory.ui.newstory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.dicodingstory.R
import com.example.dicodingstory.databinding.ActivityNewStoryBinding
import com.example.dicodingstory.ui.ViewModelFactory
import com.example.dicodingstory.ui.auth.pref.AuthenticationViewModel
import com.example.dicodingstory.utils.getImageUri
import com.example.dicodingstory.utils.reduceFileSize
import com.example.dicodingstory.utils.uriToFile
import com.example.dicodingstory.data.remote.Result
import com.example.dicodingstory.ui.liststory.MainActivity
import com.example.dicodingstory.ui.welcome.WelcomeActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class NewStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewStoryBinding
    private val viewModel: NewStoryViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private val authenticationViewModel: AuthenticationViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private var currentImageUri: Uri? = null
    private var lat: Double? = null
    private var lon: Double? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
                setMyLocation()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        if (!allPermissionsGranted(REQUIRED_PERMISSION)) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        authenticationViewModel.getSession().observe(this) { loginResult ->
            if (loginResult.token.isNotEmpty()) {
                binding.btnGallery.setOnClickListener {
                    startGallery()
                }

                binding.btnCamera.setOnClickListener {
                    startCamera()
                }

                binding.cbLocation.setOnClickListener {
                    if (binding.cbLocation.isChecked) {
                        setMyLocation()
                    } else {
                        lat = null
                        lon = null
                    }
                }
                binding.btnUpload.setOnClickListener {
                    uploadStory()
                }
            } else {
                navigateToAnotherPage(WelcomeActivity::class.java)
            }
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ivPreview.setImageURI(it)
        }
    }

    private fun uploadStory() {
        currentImageUri?.let { uri ->
            val file = uriToFile(uri, applicationContext).reduceFileSize()
            val description = binding.edDescription.text.toString()
            viewModel.uploadStory(file, description, lat, lon).observe(this) { result ->
                when (result) {
                    is Result.Loading -> {
                        binding.progressIndicator.visibility = View.VISIBLE
                    }

                    is Result.Success -> {
                        binding.progressIndicator.visibility = View.GONE
                        Toast.makeText(
                            this,
                            "Story uploaded successfully!",
                            Toast.LENGTH_LONG
                        ).show()
                        navigateToAnotherPage(MainActivity::class.java)
                        supportFragmentManager.beginTransaction()
                            .addToBackStack(null)
                            .commit()
                    }

                    is Result.Error -> {
                        binding.progressIndicator.visibility = View.GONE
                        Toast.makeText(
                            this,
                            "Failed to upload story: ${result.error}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        } ?: showToast(getString(R.string.warning_picture_cannot_empty))
    }

    private fun setMyLocation() {
        try {
            if (allPermissionsGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
                fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        lat = location.latitude
                        lon = location.longitude
                    } else {
                        Toast.makeText(
                            this,
                            getString(R.string.location_not_found),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
            Toast.makeText(this, "SecurityException: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun navigateToAnotherPage(destination: Class<*>) {
        val intent = Intent(this, destination)
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE
    }
}
