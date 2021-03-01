package com.test.itsavirustest.network

import com.test.itsavirustest.model.RestaurantMenuModel
import com.test.itsavirustest.model.RestaurantModel
import com.test.itsavirustest.util.API_URL
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.sql.Timestamp
import java.util.concurrent.TimeUnit


interface Service {
    @GET("/restaurant-finder/open/{timestamp}")
    fun getRestaurantData(@Path("timestamp") timestamp: Timestamp): Observable<List<RestaurantModel>>

    @GET("/restaurants/{id}/menus")
    fun getRestaurantMenusById(@Path("id") id: String): Observable<List<RestaurantMenuModel>>

    @GET("/restaurant-finder/nearest/{latitude}/{longitude}")
    fun getRestaurantDataByNear(
        @Path("latitude") latitude: Double,
        @Path("longitude") longitude: Double
    ): Observable<List<RestaurantModel>>

    @GET("/restaurant-finder/dish-by-price-range/{min}/{max}")
    fun getDishaByPriceRange(
        @Path("min") min: Int,
        @Path("max") max: Int
    ): Observable<List<RestaurantModel>>

    @GET("/restaurant-finder/top-restaurant/{sort-type}/{limit}")
    fun getRestaurantByTop(
        @Path("sort-type") sort_type: String,
        @Path("limit") max: Int
    ): Observable<List<RestaurantModel>>

    @GET("/restaurant-finder/dish-match/{dish-name}")
    fun getRestaurantBySearchDishName(
        @Path("dish-name") dish_name: String
    ): Observable<List<RestaurantModel>>

    @GET("/restaurant-finder/relevance-name/{option}/{name}")
    fun getRestaurantBySearcRelevance(
        @Path("option") option: String,
        @Path("name") name: String
    ): Observable<List<RestaurantModel>>

    companion object Factory {
        fun create(): Service {

            val logging = HttpLoggingInterceptor()
            // set your desired log level
            logging.apply { logging.level = HttpLoggingInterceptor.Level.BODY }

            val httpClient = OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(BasicAuthInterceptor("1", "hungry12345678"))
            // add logging as last interceptor
            httpClient.addInterceptor(logging)

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(API_URL)
                .client(httpClient.build())
                .build()
            return retrofit.create(Service::class.java)
        }
    }
}
