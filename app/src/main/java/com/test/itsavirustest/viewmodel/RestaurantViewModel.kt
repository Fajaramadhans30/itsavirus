package com.test.itsavirustest.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.test.itsavirustest.model.RestaurantMenuModel
import com.test.itsavirustest.model.RestaurantModel
import com.test.itsavirustest.network.RestaurantRepository
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import java.sql.Timestamp
import java.util.*
import kotlin.properties.Delegates

class RestaurantViewModel(
    private val compositeDisposable: CompositeDisposable,
    private val repository: RestaurantRepository,
    private val backgroundScheduler: Scheduler,
    private val mainScheduler: Scheduler
) : ViewModel() {
    private var listRestaurant = MutableLiveData<MutableList<RestaurantModel>>()

    private var latitude: Double = -8.819846
    private var longitude: Double = 115.131785

    var currentTime = Calendar.getInstance().time
    var longTime: Long = currentTime.time
    val timestamp = Timestamp(longTime)
    @SuppressLint("NullSafeMutableLiveData")
    fun setListRestaurant() {
        Log.d("INI TIMESTAMP", "setListRestaurant: $timestamp")
        compositeDisposable.add(
            repository.getRestaurant(timestamp)
                .observeOn(backgroundScheduler)
                .subscribeOn(mainScheduler)
                .subscribe({ RestaurantViewModel ->
                    listRestaurant.postValue(RestaurantViewModel as ArrayList<RestaurantModel>?)

                }, { error ->
                    println("error message " + error.message)
                    listRestaurant.postValue(null)
                }
                )
        )
    }

    fun getListRestaurant(): LiveData<MutableList<RestaurantModel>> {
        return listRestaurant
    }

    @SuppressLint("NullSafeMutableLiveData")
    fun setListRestaurantByNear() {
        compositeDisposable.add(
            repository.getRestaurantByNear(latitude, longitude)
                .observeOn(backgroundScheduler)
                .subscribeOn(mainScheduler)
                .subscribe({ RestaurantViewModel ->
                    listRestaurant.postValue(RestaurantViewModel as ArrayList<RestaurantModel>?)

                }, { error ->
                    println("error message " + error.message)
                    listRestaurant.postValue(null)
                }
                )
        )
    }

    fun getListRestaurantByNear(): LiveData<MutableList<RestaurantModel>> {
        return listRestaurant
    }

    @SuppressLint("NullSafeMutableLiveData")
    fun setListRestaurantFilterByPriceRange(
        intMinValue: Int,
        intMaxValue: Int
    ) {
        Log.d("INI MIN MAX ", "setListRestaurant: $intMinValue $intMaxValue")
        compositeDisposable.add(
            repository.getRestaurantFilterByPriceRange(intMinValue,intMaxValue)
                .observeOn(backgroundScheduler)
                .subscribeOn(mainScheduler)
                .subscribe({ RestaurantViewModel ->
                    listRestaurant.postValue(RestaurantViewModel as ArrayList<RestaurantModel>?)

                }, { error ->
                    println("error message " + error.message)
                    listRestaurant.postValue(null)
                }
                )
        )
    }

    fun getListRestaurantFilterByPriceRange(): LiveData<MutableList<RestaurantModel>> {
        return listRestaurant
    }

    @SuppressLint("NullSafeMutableLiveData")
    fun setListRestaurantByTop(
        sort_type: String,
        limit: Int
    ) {
        compositeDisposable.add(
            repository.getRestaurantByTop(sort_type,limit)
                .observeOn(backgroundScheduler)
                .subscribeOn(mainScheduler)
                .subscribe({ RestaurantViewModel ->
                    listRestaurant.postValue(RestaurantViewModel as ArrayList<RestaurantModel>?)

                }, { error ->
                    println("error message " + error.message)
                    listRestaurant.postValue(null)
                }
                )
        )
    }

    fun getListRestaurantByTop(): LiveData<MutableList<RestaurantModel>> {
        return listRestaurant
    }

    @SuppressLint("NullSafeMutableLiveData")
    fun setListRestaurantSearchByDishName(
        dish_name: String
    ) {
        compositeDisposable.add(
            repository.getRestaurantBySearchDishName(dish_name)
                .observeOn(backgroundScheduler)
                .subscribeOn(mainScheduler)
                .subscribe({ RestaurantViewModel ->
                    listRestaurant.postValue(RestaurantViewModel as ArrayList<RestaurantModel>?)

                }, { error ->
                    println("error message " + error.message)
                    listRestaurant.postValue(null)
                }
                )
        )
    }

    fun getListRestaurantSearchByDishName(): LiveData<MutableList<RestaurantModel>> {
        return listRestaurant
    }

    @SuppressLint("NullSafeMutableLiveData")
    fun setListRestaurantSearchRelevance(
        option: String,
        name: String
    ) {
        compositeDisposable.add(
            repository.getRestaurantBySearchRelevance(option, name)
                .observeOn(backgroundScheduler)
                .subscribeOn(mainScheduler)
                .subscribe({ RestaurantViewModel ->
                    listRestaurant.postValue(RestaurantViewModel as ArrayList<RestaurantModel>?)

                }, { error ->
                    println("error message " + error.message)
                    listRestaurant.postValue(null)
                }
                )
        )
    }


    fun getListRestaurantSearchRelevance(): LiveData<MutableList<RestaurantModel>> {
        return listRestaurant
    }

    class ViewModelRestaurantFactory(
        private val compositeDisposable: CompositeDisposable,
        private val repository: RestaurantRepository,
        private val backgroundScheduler: Scheduler,
        private val mainScheduler: Scheduler
    ) : ViewModelProvider.NewInstanceFactory() {
        @SuppressWarnings("unchecked")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return RestaurantViewModel(
                compositeDisposable,
                repository,
                backgroundScheduler,
                mainScheduler
            ) as T
        }
    }
}