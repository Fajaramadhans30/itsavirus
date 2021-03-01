package com.test.itsavirustest

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.test.itsavirustest.databinding.PlaceOrderActivityBinding

class PlaceOrderActivity :AppCompatActivity() {
    private lateinit var binding: PlaceOrderActivityBinding

    @SuppressLint("CheckResult", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.place_order_activity)

        val result : Int? = intent?.extras?.getInt("result")
        val foodName : String = intent.getStringExtra(getString(R.string.intent_restaurant_name)).toString()
        val priceDouble = intent.extras?.getDouble("price")
        val price :String = (priceDouble?.times(1000)).toString()


        binding.tvAmount.text = result.toString()+"X"
        binding.tvFoodname.text = foodName
        binding.tvFoodPrice.text = price

        val subtotal : Int? = (priceDouble?.times(result!!))?.toInt()?.times(1000)
        binding.tvSubtotal.text = subtotal.toString()
        binding.tvDeliveryFee.text = "10000"
        binding.tvPriceTotal.text = (subtotal?.plus(10000)).toString()


        Log.d("REAL PRICE ", "onCreate: $result")
        Log.d("REAL PRICE 3 ", "onCreate: $priceDouble")
        Log.d("REAL PRICE 4 ", "onCreate: $foodName")
        Log.d("REAL PRICE 4 ", "onCreate: $subtotal").toString()


        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(intent.getStringExtra(getString(R.string.intent_restaurant_name)))
        supportActionBar?.setSubtitle(Html.fromHtml("<font color='#FFF'>Distance from you 0.5 km</font>"))


//        binding.tvPrice.text = price

//        binding.btnDecrease.setOnClickListener {
//            decreaseInteger()
//        }

//        binding.btnIncrease.setOnClickListener {
//            increaseInteger()
//        }

    }
}