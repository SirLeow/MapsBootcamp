package com.example.mapsbootcamp

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.mapsbootcamp.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation:Location
    private val LOCATION_PERMISSION_RC = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnMarkerClickListener(this)
        mMap.uiSettings.isZoomControlsEnabled = true

        setUpMap()
    }

    @SuppressLint("MissingPermission")
    private fun setUpMap(){
        if (ActivityCompat.checkSelfPermission(this,
                ACCESS_FINE_LOCATION
            )  != PackageManager.PERMISSION_GRANTED
            || (ActivityCompat.checkSelfPermission(this,
                ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this,
            arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_RC)
            return
        }

        mMap.isMyLocationEnabled = true

        mMap.mapType = GoogleMap.MAP_TYPE_HYBRID

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                placeMarkerOnMap(currentLatLng)
                mMap.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng))
            }
        }

    }

    private fun placeMarkerOnMap(location: LatLng){
        val markerOptions = MarkerOptions().position(location)
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(
            BitmapFactory.decodeResource(resources,R.mipmap.ic_user_location)))

        markerOptions.title(getAddress(location))
        mMap.addMarker(markerOptions)
    }

    private fun getAddress(latLng: LatLng): String {
        //val addresses: List<Address>
        val geocoder = Geocoder(this, Locale.getDefault())

        val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        //val city = addresses[0].locality
        //val state = addresses[0].adminArea
        //val country = addresses[0].countryName
        //val postalCode = addresses[0].postalCode
        return addresses[0].getAddressLine(0)
    }

    override fun onMarkerClick(p0: Marker) = false
}