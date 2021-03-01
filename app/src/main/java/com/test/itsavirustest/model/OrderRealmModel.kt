package com.test.itsavirustest.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class OrderRealmModel (
    @PrimaryKey
    var id: Int? = null,
    var name: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var balance: Double? = null,
    var distance: Double? = null,
    var total_amount: Int? = null
) : RealmObject()