package com.test.itsavirustest.util.custom_map

import android.app.IntentService
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.ResultReceiver
import android.util.Log
import com.test.itsavirustest.R
import java.io.IOException
import java.util.*

class FetchAddressIntentService
/**
 * Creates an IntentService.  Invoked by your subclass's constructor.
 */
    : IntentService("FetchAddressIntentService") {
    protected var receiver: ResultReceiver? = null
    override fun onHandleIntent(intent: Intent?) {
        if (intent == null) {
            return
        }
        receiver = intent.getParcelableExtra(SimplePlacePicker.RECEIVER)
        val latitude =
            intent.getDoubleExtra(SimplePlacePicker.LOCATION_LAT_EXTRA, -1.0)
        val longitude =
            intent.getDoubleExtra(SimplePlacePicker.LOCATION_LNG_EXTRA, -1.0)
        val language = intent.getStringExtra(SimplePlacePicker.LANGUAGE)
        var errorMessage = ""
        var addresses: List<Address>? = null
        val locale = Locale(language)
        val geocoder = Geocoder(this, locale)
        try {
            addresses = geocoder.getFromLocation(
                latitude,
                longitude,
                1
            )
        } catch (ioException: IOException) {
            // Catch network or other I/O problems.
            errorMessage = getString(R.string.service_not_available)
            Log.e(TAG, errorMessage, ioException)
        }
        // Handle case where no address was found.
        if (addresses == null || addresses.size == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found)
                Log.e(TAG, errorMessage)
            }
            deliverResultToReceiver(SimplePlacePicker.FAILURE_RESULT, errorMessage)
        } else {
            val result = StringBuilder()
            val address = addresses[0]

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for (i in 0..address.maxAddressLineIndex) {
                if (i == address.maxAddressLineIndex) {
                    result.append(address.getAddressLine(i))
                } else {
                    result.append(address.getAddressLine(i) + ",")
                }
            }
            Log.i(
                TAG,
                getString(R.string.address_found)
            )
            Log.i(TAG, "address : $result")
            deliverResultToReceiver(
                SimplePlacePicker.SUCCESS_RESULT,
                result.toString()
            )
        }
    }

    private fun deliverResultToReceiver(resultCode: Int, message: String) {
        val bundle = Bundle()
        bundle.putString(SimplePlacePicker.RESULT_DATA_KEY, message)
        receiver!!.send(resultCode, bundle)
    }

    companion object {
        val TAG = FetchAddressIntentService::class.java.simpleName
    }
}