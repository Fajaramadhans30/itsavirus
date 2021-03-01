package com.test.itsavirustest.util.LocationUtil

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import java.io.IOException
import java.util.*

class LocationHelper(private val context: Context) :
    PermissionUtils.PermissionResultCallback {
    private val current_activity: Activity
    private var isPermissionGranted = false
    private var mLastLocation: Location? = null

    /**
     * Method used to get the GoogleApiClient
     */
    // Google client to interact with Google API
    var googleApiCLient: GoogleApiClient? = null
    // list of permissions
    private val permissions =
        ArrayList<String>()
    private val permissionUtils: PermissionUtils

    /**
     * Method to check the availability of location permissions
     */
    fun checkpermission() {
        permissionUtils.check_permission(
            permissions,
            "Need GPS permission for getting your location",
            1
        )
    }

    /**
     * Method to verify google play services on the device
     */
    fun checkPlayServices(): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(
                    current_activity, resultCode,
                    PLAY_SERVICES_REQUEST
                ).show()
            } else {
                showToast("This device is not supported.")
            }
            return false
        }
        return true
    }

    /**
     * Method to display the location on UI
     */
    val location: Location?
        get() {
            if (isPermissionGranted) {
                try {
                    mLastLocation = LocationServices.FusedLocationApi
                        .getLastLocation(googleApiCLient)
                    return mLastLocation
                } catch (e: SecurityException) {
                    e.printStackTrace()
                }
            }
            return null
        }

    fun getAddress(latitude: Double, longitude: Double): Address? {
        val geocoder: Geocoder
        val addresses: List<Address>
        geocoder = Geocoder(context, Locale.getDefault())
        try {
            addresses = geocoder.getFromLocation(
                latitude,
                longitude,
                1
            ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            return addresses[0]
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * Method used to build GoogleApiClient
     */
    fun buildGoogleApiClient() {
        googleApiCLient = GoogleApiClient.Builder(context)
            .addConnectionCallbacks((current_activity as ConnectionCallbacks))
            .addOnConnectionFailedListener((current_activity as OnConnectionFailedListener))
            .addApi(LocationServices.API).build()
        googleApiCLient?.connect()
        val mLocationRequest = LocationRequest()
        mLocationRequest.interval = 10000
        mLocationRequest.fastestInterval = 5000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(mLocationRequest)
        val result =
            LocationServices.SettingsApi.checkLocationSettings(googleApiCLient, builder.build())
        result.setResultCallback { locationSettingsResult ->
            val status =
                locationSettingsResult.status
            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS ->                         // All location settings are satisfied. The client can initialize location requests here
                    mLastLocation = location
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    status.startResolutionForResult(
                        current_activity,
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (e: SendIntentException) {
                    // Ignore the error.
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                }
            }
        }
    }

    /**
     * Method used to connect GoogleApiClient
     */
    fun connectApiClient() {
        googleApiCLient!!.connect()
    }

    /**
     * Handles the permission results
     */
    fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<out String>,
        @NonNull grantResults: IntArray?
    ) {
        permissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults!!)
    }

    /**
     * Handles the activity results
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> when (resultCode) {
                Activity.RESULT_OK ->                         // All required changes were successfully made
                    mLastLocation = location
                Activity.RESULT_CANCELED -> {
                }
                else -> {
                }
            }
        }
    }

    override fun PermissionGranted(request_code: Int) {
        Log.i("PERMISSION", "GRANTED")
        isPermissionGranted = true
    }

    override fun PartialPermissionGranted(
        request_code: Int,
        granted_permissions: ArrayList<String>?
    ) {
        Log.i("PERMISSION PARTIALLY", "GRANTED")
    }

    override fun PermissionDenied(request_code: Int) {
        Log.i("PERMISSION", "DENIED")
    }

    override fun NeverAskAgain(request_code: Int) {
        Log.i("PERMISSION", "NEVER ASK AGAIN")
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val PLAY_SERVICES_REQUEST = 1000
        private const val REQUEST_CHECK_SETTINGS = 2000
    }

    init {
        current_activity = context as Activity
        permissionUtils = PermissionUtils(context, this)
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
    }
}