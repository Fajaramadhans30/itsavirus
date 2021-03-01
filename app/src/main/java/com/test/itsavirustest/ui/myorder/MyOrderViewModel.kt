package com.test.itsavirustest.ui.myorder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyOrderViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is my order Fragment"
    }
    val text: LiveData<String> = _text
}