package com.test.itsavirustest.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.test.itsavirustest.model.RestaurantMenuModel
import com.test.itsavirustest.network.RestaurantRepository
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class RestaurantMenuViewModel(
    private val compositeDisposable: CompositeDisposable,
    private val repository: RestaurantRepository,
    private val backgroundScheduler: Scheduler,
    private val mainScheduler: Scheduler
) : ViewModel() {
    private var listRestaurantMenu = MutableLiveData<MutableList<RestaurantMenuModel>>()


    @SuppressLint("NullSafeMutableLiveData")
    fun setListRestaurantMenu(id: String) {
        compositeDisposable.add(
            repository.getRestauranttById(id)
                .observeOn(backgroundScheduler)
                .subscribeOn(mainScheduler)
                .subscribe({ RestaurantMenuViewModel ->
//                    listRestaurant.postValue(RestaurantViewModel.size as ArrayList<RestaurantMenuModel>)

                    listRestaurantMenu.postValue(RestaurantMenuViewModel as ArrayList<RestaurantMenuModel>?)

                }, { error ->
                    println("error message " + error.message)
                    listRestaurantMenu.postValue(null)
                }
                )
        )
    }

    fun getListRestaurantMenu(): LiveData<MutableList<RestaurantMenuModel>> {
        return listRestaurantMenu
    }


//    fun setListRestaurantById(id: String) {
//        compositeDisposable.add(
//            repository.getRestauranttById(id)
//                .observeOn(backgroundScheduler)
//                .subscribeOn(mainScheduler)
//                .subscribe({
//                    restaurantData.postValue(it)
//                }, { error ->
//                    restaurantData.postValue(null)
//                    println("error message " + error.message)
//                }
//                )
//        )
//    }
//
    class ViewModelRestaurantMenuFactory(
        private val compositeDisposable: CompositeDisposable,
        private val repository: RestaurantRepository,
        private val backgroundScheduler: Scheduler,
        private val mainScheduler: Scheduler
    ) : ViewModelProvider.NewInstanceFactory() {
        @SuppressWarnings("unchecked")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return RestaurantMenuViewModel(
                compositeDisposable,
                repository,
                backgroundScheduler,
                mainScheduler
            ) as T
        }
    }

}