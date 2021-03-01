package com.test.itsavirustest

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewAnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.mancj.materialsearchbar.MaterialSearchBar
import com.mancj.materialsearchbar.MaterialSearchBar.OnSearchActionListener
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter
import com.skyfishjy.library.RippleBackground
import com.test.itsavirustest.ui.home.HomeFragment
import com.test.itsavirustest.util.custom_map.CustomButton
import com.test.itsavirustest.util.custom_map.CustomTextView
import com.test.itsavirustest.util.custom_map.FetchAddressIntentService
import com.test.itsavirustest.util.custom_map.SimplePlacePicker
import java.util.*

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    //location
    private var mMap: GoogleMap? = null
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private var mLAstKnownLocation: Location? = null
    private var locationCallback: LocationCallback? = null
    private val DEFAULT_ZOOM = 17f

    //places
    private var placesClient: PlacesClient? = null
    private var predictionList: List<AutocompletePrediction>? = null

    //views
    private var materialSearchBar: MaterialSearchBar? = null
    private var mapView: View? = null
    private var rippleBg: RippleBackground? = null
    private var mDisplayAddressTextView: CustomTextView? = null
    private var mProgressBar: ProgressBar? = null
    private var mSmallPinIv: ImageView? = null

    //variables
    private var addressOutput: String? = null
    private var addressResultCode = 0
    private var isSupportedArea = false
    private var currentMarkerPosition: LatLng? = null

    //receiving
    private var mApiKey: String? = "AIzaSyCDGsZkq76sM7DnXhfywMmCbtwhBBYAm8E"
    private var mSupportedArea: Array<String>? = arrayOf()
    private var mCountry: String? = ""
    private var mLanguage: String? = "en"

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        initViews()
        receiveIntent()
        initMapsAndPlaces()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(intent.getStringExtra(getString(R.string.find_your_location)))

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun initViews() {
        materialSearchBar = findViewById(R.id.searchBar)
        val submitLocationButton: CustomButton = findViewById(R.id.submit_location_button)
        rippleBg = findViewById(R.id.ripple_bg)
        mDisplayAddressTextView = findViewById(R.id.tv_display_marker_location)
        mProgressBar = findViewById(R.id.progress_bar)
        mSmallPinIv = findViewById(R.id.small_pin)
        val icPin = findViewById<View>(R.id.ic_pin)
        Handler().postDelayed({ revealView(icPin) }, 1000)
        submitLocationButton.setOnClickListener { submitResultLocation() }
    }

    private fun receiveIntent() {
        val intent = intent
        if (intent.hasExtra(SimplePlacePicker.API_KEY)) {
            mApiKey = intent.getStringExtra(SimplePlacePicker.API_KEY)
        }
        if (intent.hasExtra(SimplePlacePicker.COUNTRY)) {
            mCountry = intent.getStringExtra(SimplePlacePicker.COUNTRY)
        }
        if (intent.hasExtra(SimplePlacePicker.LANGUAGE)) {
            mLanguage = intent.getStringExtra(SimplePlacePicker.LANGUAGE)
        }
        if (intent.hasExtra(SimplePlacePicker.SUPPORTED_AREAS)) {
            mSupportedArea = intent.getStringArrayExtra(SimplePlacePicker.SUPPORTED_AREAS)
        }
    }

    private fun initMapsAndPlaces() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        Places.initialize(this, mApiKey!!)
        placesClient = Places.createClient(this)
        val token = AutocompleteSessionToken.newInstance()
        val mapFragment =
            fragmentManager.findFragmentById(R.id.map_fragment) as MapFragment
        mapFragment.getMapAsync(this)
        mapView = mapFragment.view
        materialSearchBar?.setOnSearchActionListener(object : OnSearchActionListener {
            override fun onSearchStateChanged(enabled: Boolean) {}
            override fun onSearchConfirmed(text: CharSequence) {
                startSearch(text.toString(), true, null, true)
            }

            override fun onButtonClicked(buttonCode: Int) {
                if (buttonCode == MaterialSearchBar.BUTTON_BACK) {
                    materialSearchBar?.disableSearch()
                    materialSearchBar?.clearSuggestions()
                }
            }
        })
        materialSearchBar?.addTextChangeListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                val predictionsRequest =
                    FindAutocompletePredictionsRequest.builder()
                        .setCountry(mCountry)
                        .setSessionToken(token)
                        .setQuery(s.toString())
                        .build()
                placesClient?.findAutocompletePredictions(predictionsRequest)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val predictionsResponse =
                                task.result
                            if (predictionsResponse != null) {
                                predictionList =
                                    predictionsResponse.autocompletePredictions
                                val suggestionsList: MutableList<String?> =
                                    ArrayList()
                                for (i in predictionList?.indices!!) {
                                    val prediction =
                                        predictionList!![i]
                                    suggestionsList.add(prediction.getFullText(null).toString())
                                }
                                materialSearchBar?.updateLastSuggestions(suggestionsList)
                                Handler().postDelayed({
                                    if (materialSearchBar?.isSuggestionsVisible!!) {
                                        materialSearchBar?.showSuggestionsList()
                                    }
                                }, 1000)
                            }
                        } else {
                            Log.i(
                                TAG,
                                "prediction fetching task unSuccessful"
                            )
                        }
                    }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        materialSearchBar?.setSuggestionsClickListener(object :
            SuggestionsAdapter.OnItemViewClickListener {
            override fun OnItemClickListener(
                position: Int,
                v: View
            ) {
                if (position >= predictionList?.size!!) {
                    return
                }
                val selectedPrediction = predictionList!![position]
                val suggestion =
                    materialSearchBar?.lastSuggestions?.get(position)?.toString()
                materialSearchBar?.text = suggestion
                Handler().postDelayed({ materialSearchBar?.clearSuggestions() }, 1000)
                val imm =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(
                    materialSearchBar?.windowToken,
                    InputMethodManager.HIDE_IMPLICIT_ONLY
                )
                val placeId = selectedPrediction.placeId
                val placeFields = Arrays.asList(
                    Place.Field.LAT_LNG,
                    Place.Field.NAME,
                    Place.Field.ADDRESS
                )
                val fetchPlaceRequest =
                    FetchPlaceRequest.builder(placeId, placeFields).build()
                placesClient?.fetchPlace(fetchPlaceRequest)
                    ?.addOnSuccessListener { fetchPlaceResponse ->
                        val place = fetchPlaceResponse.place
                        Log.i(
                            TAG,
                            "place found " + place.name + place.address
                        )
                        val latLng = place.latLng
                        if (latLng != null) {
                            mMap?.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    latLng,
                                    DEFAULT_ZOOM
                                )
                            )
                        }
                        rippleBg?.startRippleAnimation()
                        Handler().postDelayed({ rippleBg?.stopRippleAnimation() }, 2000)
                    }
                    ?.addOnFailureListener { e ->
                        if (e is ApiException) {
                            val apiException = e
                            apiException.printStackTrace()
                            val statusCode = apiException.statusCode
                            Log.i(
                                TAG,
                                "place not found" + e.message
                            )
                            Log.i(
                                TAG,
                                "status code : $statusCode"
                            )
                        }
                    }
            }

            override fun OnItemDeleteListener(
                position: Int,
                v: View
            ) {
            }
        })
    }

    private fun submitResultLocation() {
        // if the process of getting address failed or this is not supported area , don't submit
        if (addressResultCode == SimplePlacePicker.FAILURE_RESULT || !isSupportedArea) {
            Toast.makeText(this@MapActivity, R.string.failed_select_location, Toast.LENGTH_SHORT)
                .show()
        } else {
            val data = Intent()
            data.putExtra("data_alamat", addressOutput)
            data.putExtra(SimplePlacePicker.LOCATION_LAT_EXTRA, currentMarkerPosition?.latitude)
            data.putExtra(SimplePlacePicker.LOCATION_LNG_EXTRA, currentMarkerPosition?.longitude)

            val mapAdress = intent.extras?.getString("data_alamat")

            setResult(Activity.RESULT_OK, data)

//            Log.d(TAG, "submitResultLocation: $addressOutput")
//            val bundle = Bundle()
//            bundle.putString(SimplePlacePicker.SELECTED_ADDRESS, addressOutput)
//            // set Fragmentclass Arguments
//            Log.d(TAG, "submitResultLocation 22222: $bundle")
            val fragobj = HomeFragment()

            val bundle = Bundle()
            bundle.putString("data_alamat", mapAdress)
            fragobj.arguments = bundle

            finish()
        }
    }

    @SuppressLint("MissingPermission")
    /*
      is triggered when the map is loaded and ready to display
      @param GoogleMap
     *
     * */  override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap?.isMyLocationEnabled = true
        //enable location button
        mMap?.uiSettings?.isMyLocationButtonEnabled = true
        mMap?.uiSettings?.isCompassEnabled = false

        //move location button to the required position and adjust params such margin
        if (mapView != null && mapView?.findViewById<View?>("1".toInt()) != null) {
            val locationButton =
                (mapView?.findViewById<View>("1".toInt())
                    ?.parent as View).findViewById<View>("2".toInt())
            val layoutParams =
                locationButton.layoutParams as RelativeLayout.LayoutParams
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            layoutParams.setMargins(0, 0, 60, 500)
        }
        val locationRequest = LocationRequest.create()
        locationRequest.interval = 1000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder =
            LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(this)
        val task =
            settingsClient.checkLocationSettings(builder.build())

        //if task is successful means the gps is enabled so go and get device location amd move the camera to that location
        task.addOnSuccessListener { deviceLocation }

        //if task failed means gps is disabled so ask user to enable gps
        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(this@MapActivity, 51)
                } catch (e1: SendIntentException) {
                    e1.printStackTrace()
                }
            }
        }
        mMap?.setOnMyLocationButtonClickListener {
            if (materialSearchBar?.isSuggestionsVisible!!) {
                materialSearchBar?.clearSuggestions()
            }
            if (materialSearchBar?.isSearchEnabled!!) {
                materialSearchBar?.disableSearch()
            }
            false
        }
        mMap?.setOnCameraIdleListener {
            mSmallPinIv?.visibility = View.GONE
            mProgressBar?.visibility = View.VISIBLE
            Log.i(TAG, "changing address")
            //                ToDo : you can use retrofit for this network call instead of using services
            //hint: services is just for doing background tasks when the app is closed no need to use services to update ui
            //best way to do network calls and then update user ui is Retrofit .. consider it
            startIntentService()
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 51) {
            if (resultCode == Activity.RESULT_OK) {
                deviceLocation
            }
        }
    }//remove location updates in order not to continues check location unnecessarily

    /**
     * is triggered whenever we want to fetch device location
     * in order to get device's location we use FusedLocationProviderClient object that gives us the last location
     * if the task of getting last location is successful and not equal to null ,
     * apply this location to mLastLocation instance and move the camera to this location
     * if the task is not successful create new LocationRequest and LocationCallback instances and update lastKnownLocation with location result
     */
    @get:SuppressLint("MissingPermission")
    private val deviceLocation: Unit
        private get() {
            mFusedLocationProviderClient?.lastLocation
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        mLAstKnownLocation = task.result
                        if (mLAstKnownLocation != null) {
                            mMap?.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        mLAstKnownLocation?.latitude!!,
                                        mLAstKnownLocation?.longitude!!
                                    ), DEFAULT_ZOOM
                                )
                            )
                        } else {
                            val locationRequest = LocationRequest.create()
                            locationRequest.interval = 1000
                            locationRequest.fastestInterval = 5000
                            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                            locationCallback = object : LocationCallback() {
                                override fun onLocationResult(locationResult: LocationResult) {
                                    super.onLocationResult(locationResult)
                                    if (locationResult == null) {
                                        return
                                    }
                                    mLAstKnownLocation = locationResult.lastLocation
                                    mMap?.moveCamera(
                                        CameraUpdateFactory.newLatLngZoom(
                                            LatLng(
                                                mLAstKnownLocation?.latitude!!,
                                                mLAstKnownLocation?.longitude!!
                                            ), DEFAULT_ZOOM
                                        )
                                    )
                                    //remove location updates in order not to continues check location unnecessarily
                                    mFusedLocationProviderClient?.removeLocationUpdates(
                                        locationCallback
                                    )
                                }
                            }
                            mFusedLocationProviderClient?.requestLocationUpdates(
                                locationRequest,
                                null
                            )
                        }
                    } else {
                        Toast.makeText(
                            this@MapActivity,
                            "Unable to get last location ",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

    protected fun startIntentService() {
        currentMarkerPosition = mMap?.cameraPosition?.target
        val resultReceiver =
            AddressResultReceiver(Handler())
        val intent = Intent(this, FetchAddressIntentService::class.java)
        intent.putExtra(SimplePlacePicker.RECEIVER, resultReceiver)
        intent.putExtra(SimplePlacePicker.LOCATION_LAT_EXTRA, currentMarkerPosition?.latitude)
        intent.putExtra(SimplePlacePicker.LOCATION_LNG_EXTRA, currentMarkerPosition?.longitude)
        intent.putExtra(SimplePlacePicker.LANGUAGE, mLanguage)
        startService(intent)
    }

    private fun updateUi() {
        mDisplayAddressTextView?.visibility = View.VISIBLE
        mProgressBar?.visibility = View.GONE
        mMap?.clear()
        if (addressResultCode == SimplePlacePicker.SUCCESS_RESULT) {
            //check for supported area
            if (isSupportedArea(mSupportedArea)) {
                //supported
                addressOutput = addressOutput?.replace("Unnamed Road,", "")
                addressOutput = addressOutput?.replace("Unnamed Road،", "")
                addressOutput = addressOutput?.replace("Unnamed Road New,", "")
                mSmallPinIv?.visibility = View.VISIBLE
                isSupportedArea = true
                mDisplayAddressTextView?.text = addressOutput
            } else {
                //not supported
                mSmallPinIv?.visibility = View.GONE
                isSupportedArea = false
                mDisplayAddressTextView?.text = getString(R.string.not_support_area)
            }
        } else if (addressResultCode == SimplePlacePicker.FAILURE_RESULT) {
            mSmallPinIv?.visibility = View.GONE
            mDisplayAddressTextView?.setText(addressOutput)
        }
    }

    private fun isSupportedArea(supportedAreas: Array<String>?): Boolean {
        if (supportedAreas?.isEmpty()!!) return true
        var isSupported = false
        for (area in supportedAreas) {
            if (addressOutput?.contains(area)!!) {
                isSupported = true
                break
            }
        }
        return isSupported
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun revealView(view: View) {
        val cx = view.width / 2
        val cy = view.height / 2
        val finalRadius =
            Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()
        val anim =
            ViewAnimationUtils.createCircularReveal(view, cx, cy, 0f, finalRadius)
        view.visibility = View.VISIBLE
        anim.start()
    }

    internal inner class AddressResultReceiver(handler: Handler?) :
        ResultReceiver(handler) {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
            addressResultCode = resultCode
            if (resultData == null) {
                return
            }

            // Display the address string
            // or an error message sent from the intent service.
            addressOutput = resultData.getString(SimplePlacePicker.RESULT_DATA_KEY)
            if (addressOutput == null) {
                addressOutput = ""
            }
            updateUi()
        }
    }

    companion object {
        private val TAG = MapActivity::class.java.simpleName
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    }
}