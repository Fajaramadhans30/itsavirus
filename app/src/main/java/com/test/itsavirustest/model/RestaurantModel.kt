package com.test.itsavirustest.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
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

//    @SerializedName("menu")
//    @Expose
//    private val menu: List<RestaurantMenuModel>? = null

    @SerializedName("distance")
    @Expose
    var distance: Double? = null

    @SerializedName("total_amount")
    @Expose
    var total_amount: Double? = null

//    constructor(id: String, name: String?, latitude: Double?, longitude: Double?, balance: Double?,
//                distance: Double?, total_mount: Double?) {
//        this.id = id
//        this.name = name
//        this.latitude = latitude
//        this.longitude = longitude
//        this.balance = balance
//        this.distance = distance
//        this.total_amount = total_mount
//    }
}