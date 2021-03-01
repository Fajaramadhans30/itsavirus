package com.test.itsavirustest.network

import com.test.itsavirustest.model.RestaurantMenuModel
import com.test.itsavirustest.model.RestaurantModel
import io.reactivex.Observable
import java.sql.Timestamp

class RestaurantRepository (private val service: Service) {

    fun getRestaurant(timestamp: Timestamp): Observable<List<RestaurantModel>> {
        return service.getRestaurantData(timestamp)
    }

    fun getRestauranttById(id:String): Observable<List<RestaurantMenuModel>> {
        return service.getRestaurantMenusById(id)
    }

    fun getRestaurantByNear(latitude:Double, longitude: Double): Observable<List<RestaurantModel>> {
        return service.getRestaurantDataByNear(latitude, longitude)
    }

    fun getRestaurantFilterByPriceRange(min:Int, max: Int): Observable<List<RestaurantModel>> {
        return service.getDishaByPriceRange(min, max)
    }

    fun getRestaurantByTop(sort_type:String, limit: Int): Observable<List<RestaurantModel>> {
        return service.getRestaurantByTop(sort_type, limit)
    }

    fun getRestaurantBySearchDishName(dish_name:String): Observable<List<RestaurantModel>> {
        return service.getRestaurantBySearchDishName(dish_name)
    }

    fun getRestaurantBySearchRelevance(option:String, name:String): Observable<List<RestaurantModel>> {
        return service.getRestaurantBySearcRelevance(option, name)
    }
}