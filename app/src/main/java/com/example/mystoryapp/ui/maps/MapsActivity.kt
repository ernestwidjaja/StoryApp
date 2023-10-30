package com.example.mystoryapp.ui.maps

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.mystoryapp.R
import com.example.mystoryapp.ViewModelFactory
import com.example.mystoryapp.data.Result
import com.example.mystoryapp.data.api.response.ListStoryItem
import com.example.mystoryapp.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val boundsBuilder = LatLngBounds.Builder()
    private val viewModel by viewModels<MapsViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private val fineLocation = Manifest.permission.ACCESS_FINE_LOCATION
    private val coarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        with(mMap.uiSettings) {
            isZoomControlsEnabled = true
            isIndoorLevelPickerEnabled = true
            isCompassEnabled = true
            isMapToolbarEnabled = true
        }
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))

        getMyLocation()
        getStoriesLocation()
    }


    private fun getStoriesLocation() {
        viewModel.getStoriesLocation().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                    }

                    is Result.Success -> {
                        addManyMarker(result.data)
                    }

                    is Result.Error -> {
                        showToast(result.error)
                    }
                }
            }
        }
    }

    private fun addManyMarker(stories: List<ListStoryItem>) {
        stories.forEach {
            val latLng = LatLng(it.lat!!, it.lon!!)
            val title = it.name
            val description = it.description
            mMap.addMarker(
                MarkerOptions()
                .position(latLng)
                .title(title)
                .snippet(description)
            )
            boundsBuilder.include(latLng)
        }

        val bounds: LatLngBounds = boundsBuilder.build()
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                300
            )
        )
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permission ->
            when {
                permission[fineLocation] ?: false -> {
                    getMyLocation()
                    showToast(getString(R.string.permission_granted))
                }
                permission[coarseLocation] ?: false -> {
                    getMyLocation()
                    showToast(getString(R.string.permission_granted))
                }
                else -> {
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


    private fun getMyLocation() {
        if (checkPermission(fineLocation) && checkPermission((coarseLocation))) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    fineLocation,
                    coarseLocation
                )
            )
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