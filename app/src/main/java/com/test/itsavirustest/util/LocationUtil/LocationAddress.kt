package com.test.itsavirustest.util.LocationUtil

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import java.io.IOException
import java.util.*


object LocationAddress {
    private const val TAG = "LocationAddress"
    fun getAddressFromLocation(
        latitude: Double, longitude: Double,
        context: Context?, handler: Handler?
    ) {
        val thread: Thread = object : Thread() {
            override fun run() {
                val geocoder = Geocoder(context, Locale.getDefault())
                var result: String? = null
                try {
                    val addressList: List<Address>? = geocoder.getFromLocation(
                        latitude, longitude, 1
                    )
                    if (addressList != null && addressList.size > 0) {
                        val address: Address = addressList[0]
                        val sb = StringBuilder()
                        for (i in 0 until address.getMaxAddressLineIndex()) {
                            sb.append(address.getAddressLine(i)).append("\n")
                        }
                        sb.append(address.getLocality()).append("\n")
                        sb.append(address.getPostalCode()).append("\n")
                        sb.append(address.getCountryName())
                        result = sb.toString()
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "Unable connect to Geocoder", e)
                } finally {
                    val message: Message = Message.obtain()
                    message.setTarget(handler)
                    if (result != null) {
                        message.what = 1
                        val bundle = Bundle()
                        result = """Latitude: $latitude Longitude: $longitude

Address:
$result"""
                        bundle.putString("address", result)
                        message.setData(bundle)
                    } else {
                        message.what = 1
                        val bundle = Bundle()
                        result = """Latitude: $latitude Longitude: $longitude
 Unable to get address for this lat-long."""
                        bundle.putString("address", result)
                        message.setData(bundle)
                    }
                    message.sendToTarget()
                }
            }
        }
        thread.start()
    }
}