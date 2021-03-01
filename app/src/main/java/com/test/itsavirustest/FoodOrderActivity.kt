package com.test.itsavirustest

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.test.itsavirustest.databinding.OrderFoodBinding


class FoodOrderActivity : AppCompatActivity() {
    private lateinit var binding: OrderFoodBinding
    var minteger = 0

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.order_food)

        binding.tvFoodname.text = intent.getStringExtra(getString(R.string.intent_restaurant_name))
        val priceDouble = intent.extras?.getDouble("price")
        val price :String = (priceDouble?.times(1000)).toString()

        Log.d("REAL PRICE ", "onCreate: $price")

//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(intent.getStringExtra(getString(R.string.intent_restaurant_name)))
        supportActionBar?.setSubtitle(Html.fromHtml("<font color='#FFBF00'>Distance from you 0.5 km</font>"))
//        supportActionBar?.setSubtitle(Html.fromHtml("<font color='#FFBF00'>Here ActionBar Subtitle</font>"))

        binding.tvPrice.text = price

        binding.btnDecrease.setOnClickListener {
            decreaseInteger()
        }

        binding.btnIncrease.setOnClickListener {
            increaseInteger()
        }

    }

    fun increaseInteger() {
        minteger += 1
        display(minteger)
    }

    fun decreaseInteger() {
        minteger -= 1
        if(minteger<0){
            minteger=0;
        }
        display(minteger)
    }

    @SuppressLint("SetTextI18n")
    private fun display(number: Int) {
        binding.tvValue.text = "" + number

        Log.d("BERAPA INI ? ", "display: " + number)

        clickBasket(number)
    }

    private fun clickBasket(number: Int) {
        if (number == 0) {
            binding.btnAddToBasket.visibility = View.GONE
        } else {
            binding.btnAddToBasket.visibility = View.VISIBLE
        }
        binding.btnAddToBasket.setOnClickListener {
            confirmDialog(number)
        }


    }

    @SuppressLint("ResourceAsColor")
    private fun confirmDialog(number: Int) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Confirmation")
            builder.setMessage("Are you sure ?")

            builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                    val returnIntent = Intent()
                    val priceDouble = intent.extras?.getDouble("price")
                    val foodName = intent.extras?.getString("name")
                    returnIntent.putExtra("result", number)
                    returnIntent.putExtra("price", priceDouble)
                    returnIntent.putExtra("name", foodName)
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()

            }

            builder.setNegativeButton(android.R.string.no) { dialog, which ->
                Toast.makeText(applicationContext,
                    android.R.string.no, Toast.LENGTH_SHORT).show()
            }
            builder.show()
    }

    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }
}