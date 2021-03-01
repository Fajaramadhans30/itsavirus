package com.test.itsavirustest.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RestaurantModel : Serializable {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("latitude")
    @Expose
    var latitude: Double? = null

    @SerializedName("longitude")
    @Expose
    var longitude: Double? = null

    @SerializedName("balance")
    @Expose
    var balance: Double? = null

    @SerializedName("menu")
    @Expose
    private val menu: List<RestaurantMenuModel>? = null

    @SerializedName("distance")
    @Expose
    var distance: Double? = null

    @SerializedName("total_amount")
    @Expose
    var total_amount: Double? = null
}