package com.test.itsavirustest.util.LocationUtil

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import androidx.core.app.ActivityCompat


class LocationService(context: Context) : Service(), LocationListener {
    private var locationManager: LocationManager? = context
        .getSystemService(LOCATION_SERVICE) as LocationManager?

    var location: Location? = null
    fun getLocation(provider: String?): Location? {
        if (locationManager!!.isProviderEnabled(provider!!)) {
            if (locationManager != null) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    location = locationManager!!.getLastKnownLocation(provider)
                    return location
                }
            }
        }
        return null
    }
    override fun onLocationChanged(location: Location) {
        TODO("Not yet implemented")
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    override fun onBind(arg0: Intent?): IBinder? {
        return null
    }

    companion object {
        private const val MIN_DISTANCE_FOR_UPDATE: Long = 10
        private const val MIN_TIME_FOR_UPDATE = 1000 * 60 * 2.toLong()
    }

}