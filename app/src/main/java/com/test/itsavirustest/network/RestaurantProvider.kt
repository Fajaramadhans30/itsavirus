package com.test.itsavirustest.network

object RestaurantProvider {
    fun restaurantProviderRepository():RestaurantRepository{
        return  RestaurantRepository(Service.create())
    }
}