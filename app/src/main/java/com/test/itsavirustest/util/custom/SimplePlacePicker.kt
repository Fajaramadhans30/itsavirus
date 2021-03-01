package com.test.itsavirustest.util.custom

interface SimplePlacePicker {
    companion object {
        const val PACKAGE_NAME = "com.test.itsavirustest"
        const val RECEIVER = "$PACKAGE_NAME.RECEIVER"
        const val RESULT_DATA_KEY =
            "$PACKAGE_NAME.RESULT_DATA_KEY"
        const val LOCATION_LAT_EXTRA =
            "$PACKAGE_NAME.LOCATION_lAT_EXTRA"
        const val LOCATION_LNG_EXTRA =
            "$PACKAGE_NAME.LOCATION_LNG_EXTRA"
        const val SELECTED_ADDRESS =
            "$PACKAGE_NAME.SELECTED_ADDRESS"
        const val LANGUAGE = "$PACKAGE_NAME.LANGUAGE"
        const val API_KEY = "$PACKAGE_NAME.API_KEY"
        const val COUNTRY = "$PACKAGE_NAME.COUNTRY"
        const val SUPPORTED_AREAS =
            "$PACKAGE_NAME.SUPPORTED_AREAS"
        const val SUCCESS_RESULT = 0
        const val FAILURE_RESULT = 1
        const val SELECT_LOCATION_REQUEST_CODE = 22
    }
}