package com.test.itsavirustest.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RestaurantMenuModel : Serializable {
    @SerializedName("id")
    @Expose
    var id: String? = null
    @SerializedName("restaurant_id")
    @Expose
    var restaurant_id: String? = null
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("price")
    @Expose
    var price: Double? = null
}