package com.example.mystoryapp.ui.story

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.mystoryapp.R
import com.example.mystoryapp.ViewModelFactory
import com.example.mystoryapp.data.Result
import com.example.mystoryapp.databinding.ActivityStoryBinding
import com.example.mystoryapp.ui.main.MainActivity
import com.example.mystoryapp.util.getImageUri
import com.example.mystoryapp.util.reduceFileImage
import com.example.mystoryapp.util.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class StoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryBinding
    private var currentImageUri: Uri? = null
    private var isShowLocation: Boolean = false
    private var lat: Double? = null
    private var lon: Double? = null
    private val viewModel by viewModels<StoryViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private val fineLocation = Manifest.permission.ACCESS_FINE_LOCATION
    private val coarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()

        binding.btnGallery.setOnClickListener {
            launcherGallery.launch(PickVisualMediaRequest((ActivityResultContracts.PickVisualMedia.ImageOnly)))
        }

        binding.btnCamera.setOnClickListener {
            currentImageUri = getImageUri(this)
            launcherIntentCamera.launch(currentImageUri)
        }

        binding.btnUpload.setOnClickListener {
            if (currentImageUri == null || binding.edtDescription.text.isNullOrEmpty()) {
                showToast(getString(R.string.isEdtEmpty))
            } else {
                addStories()
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.switchLocation.setOnCheckedChangeListener { _, isChecked ->
            isShowLocation = if (isChecked) {
                getMyLocation()
                true
            } else {
                false
            }
        }
    }

    private fun addStories() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            val description = binding.edtDescription.text.toString()

            showLoading(true)

            val descriptionBody = description.toRequestBody("text/plain".toMediaType())
            val latBody = lat.toString().toRequestBody("text/plain".toMediaType())
            val lonBody = lon.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )
            viewModel.addStories(multipartBody, descriptionBody, latBody, lonBody, isShowLocation).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            showLoading(true)
                        }

                        is Result.Success -> {
                            showLoading(false)
                            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                            builder
                                .setCancelable(false)
                                .setMessage(getString(R.string.story_created))
                                .setTitle(getString(R.string.success))
                                .setPositiveButton(getString(R.string.ok)) { _, _ ->
                                    val intent = Intent(this, MainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                    finish()
                                }
                            val dialog: AlertDialog = builder.create()
                            dialog.show()
                        }

                        is Result.Error -> {
                            showLoading(false)
                            showToast(result.error)
                        }
                    }
                }
            }
        }
    }

    private fun getMyLocation() {
        if (checkPermission(fineLocation) && checkPermission((coarseLocation))) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                lat = location?.latitude
                lon = location?.longitude
            }

        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    fineLocation,
                    coarseLocation
                )
            )
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permission ->
            when {
                permission[fineLocation] ?: false -> {
                    getMyLocation()
                }
                permission[coarseLocation] ?: false -> {
                    getMyLocation()
                }
                else -> {
                    binding.switchLocation.isChecked = false
                    showToast(getString(R.string.permission_not_granted))
                }
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            showToast(getString(R.string.no_media_selected))
        }
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
            binding.ivStory.setImageURI(it)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        with(binding) {
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            btnUpload.isEnabled = !isLoading
            btnCamera.isEnabled = !isLoading
            btnGallery.isEnabled = !isLoading
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) setupView()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }
}