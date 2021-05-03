package com.example.taxi_app.fragments.screens

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.taxi_app.R
import com.example.taxi_app.databinding.FragmentMapsBinding
import com.example.taxi_app.utilites.APP_ACTIVITY
import com.google.android.gms.common.api.Status
import com.google.android.gms.common.internal.service.Common
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.snackbar.Snackbar
import com.mikepenz.iconics.Iconics.applicationContext
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.fragment_maps.*
import java.io.IOException
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "UNREACHABLE_CODE")
class MapsFragment : Fragment(R.layout.fragment_maps), OnMapReadyCallback {

    private lateinit var binding: FragmentMapsBinding
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val permissionCode = 101

    lateinit var locationCallback: LocationCallback
    val cityname = ""

    private lateinit var  slidingPaneLayout: SlidingPaneLayout
    private lateinit var autocompleteSupportFragment: AutocompleteSupportFragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMapsBinding.bind(view)
        init()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
        val root = layoutInflater.inflate(R.layout.fragment_maps, container, false )
        initView(root)
        init()
    }

    override fun onStart() {
        super.onStart()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(APP_ACTIVITY)
        fetchLocation()
    }

    private fun initView(root: View?){
        slidingPaneLayout = root?.findViewById(R.id.mapFragment) as SlidingPaneLayout

    }


    private fun setRestrictPlaceInCountry(lastLocation: Location) {
        try {
            val geoCoder = Geocoder(requireContext(), Locale.getDefault())
            var addressList = geoCoder.getFromLocation(currentLocation.latitude, currentLocation.longitude, 1)
            if (addressList.size > 0 ) {
                autocompleteSupportFragment.setCountry(addressList[0].countryCode)
            }
        }
        catch (e: IOException){
            e.printStackTrace()
        }
    }

    private fun init(){
        Places.initialize(requireContext(),getString(R.string.google_maps_key))
        autocompleteSupportFragment = childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID,
        Place.Field.ADDRESS,
        Place.Field.LAT_LNG,
        Place.Field.NAME))
        autocompleteSupportFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(p0: Place) {
                Snackbar.make(requireView(),""+ p0.latLng,Snackbar.LENGTH_SHORT).show()

            }
            override fun onError(p0: Status) {
                Snackbar.make(requireView(), p0.statusMessage,Snackbar.LENGTH_SHORT).show()
            }

        })

        locationCallback = object : LocationCallback(){
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                setRestrictPlaceInCountry(p0!!.lastLocation)
            }
        }
    }


    //Fetch user location
    private fun fetchLocation(){
        if (ActivityCompat.checkSelfPermission(
                APP_ACTIVITY, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                APP_ACTIVITY, Manifest.permission.ACCESS_COARSE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions( APP_ACTIVITY,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), permissionCode)
            return

        }
        val task = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener { location ->
            if(location != null){
                currentLocation = location
                Toast.makeText(applicationContext, currentLocation.latitude.toString() + "" +
                        currentLocation.longitude, Toast.LENGTH_LONG).show()
                val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
                mapFragment?.getMapAsync(this)
            }
        }
    }

    //Add tracking
    override fun onMapReady(googleMap: GoogleMap) {
        val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        val markerOptions = MarkerOptions().position(latLng).title("Я тут!")
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 100f))
        googleMap.addMarker(markerOptions)
    }

    //Permission
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        when(requestCode){
            permissionCode -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                fetchLocation()
            }
        }
    }

}