package com.test.itsavirustest

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.skydoves.whatif.whatIfNotNullOrEmpty
import com.test.itsavirustest.databinding.ListMenuBinding
import com.test.itsavirustest.model.RestaurantMenuModel
import com.test.itsavirustest.network.RestaurantProvider
import com.test.itsavirustest.ui.adapter.RestaurantMenuAdapter
import com.test.itsavirustest.viewmodel.RestaurantMenuViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.text.DecimalFormat


class RestaurantDetailActivity : AppCompatActivity() {
    private lateinit var restaurantMenuViewModel: RestaurantMenuViewModel
    private lateinit var binding: ListMenuBinding

    private var dataListRestaurantMenu: MutableList<RestaurantMenuModel?> = mutableListOf()
    private lateinit var restaurantMenuAdapter: RestaurantMenuAdapter

    private val compositeDisposable = CompositeDisposable()
    private val repository = RestaurantProvider.restaurantProviderRepository()

    private val resultCode = 3

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this, R.layout.list_menu
        )
        restaurantMenuAdapter = RestaurantMenuAdapter {
            val intentDetail = Intent(this@RestaurantDetailActivity, FoodOrderActivity::class.java)
            intentDetail.putExtra(getString(R.string.intent_id), it.id)
            intentDetail.putExtra(getString(R.string.intent_restaurant_name), it.name)
            intentDetail.putExtra("price", it.price)
            startActivityForResult(intentDetail, resultCode)
        }

        binding.rvRestaurantMenuList.apply {
            layoutManager = GridLayoutManager(this@RestaurantDetailActivity, 2)
            binding.rvRestaurantMenuList.adapter = restaurantMenuAdapter
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle("")
//        supportActionBar?.setSubtitle(Html.fromHtml("<font color='#FFBF00'>Here ActionBar Subtitle</font>"))


//        intent.getStringExtra(getString(R.string.intent_restaurant_latitude))
//        intent.getStringExtra(getString(R.string.intent_restaurant_longitude))


        val rating = intent.extras?.getDouble("balance")
        var total = 0.0
        var count = 1000.00
        var average: Double
        if (rating != null) {
            total += rating
        }
        count += 1;
        average = total / count;

        binding.tvRestaurantName.text =
            intent.getStringExtra(getString(R.string.intent_restaurant_name))
        binding.tvRating.text = DecimalFormat("#.#").format(average)

        restaurantMenuViewModel = ViewModelProviders.of(
            this,
            RestaurantMenuViewModel.ViewModelRestaurantMenuFactory(
                compositeDisposable,
                repository,
                AndroidSchedulers.mainThread(),
                Schedulers.io()
            )
        ).get(RestaurantMenuViewModel::class.java)
        intent.getStringExtra(getString(R.string.intent_id))?.let {
            restaurantMenuViewModel.setListRestaurantMenu(it)
        }
        restaurantMenuViewModel.getListRestaurantMenu().observe(this, getListRestaurantMenu)

    }

    private val getListRestaurantMenu =
        Observer<MutableList<RestaurantMenuModel>> { restaurantMenuItems ->
            if (restaurantMenuItems != null) {
                dataListRestaurantMenu.clear()
                binding.rvRestaurantMenuList.visibility = View.VISIBLE
//            binding.progressLoadingId.idLoading.visibility = View.GONE
//            binding.failedLoadId.idError.visibility = View.GONE
                restaurantMenuItems.whatIfNotNullOrEmpty {
                    (restaurantMenuAdapter as? RestaurantMenuAdapter)?.addListMenuRestaurant(it)
                }
            } else {
                binding.rvRestaurantMenuList.visibility = View.GONE
//            binding.progressLoadingId.idLoading.visibility = View.GONE
//            binding.failedLoadId.idError.visibility = View.VISIBLE
            }
//        binding.swipeRefresh.isRefreshing = false
        }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
            if (resultCode == Activity.RESULT_OK) {
                val result : Int? = data?.extras?.getInt("result")
                val result_price = data?.extras?.getDouble("price")
                val price :String = (result_price?.times(1000)).toString()
                val foodName : String = data?.extras?.getString("name").toString()
                Log.d("RETURN MASUK ", "onActivityResult: $result")
                Log.d("RETURN MASUK 2", "onActivityResult: $result_price")
                Log.d("RETURN MASUK 3", "onActivityResult: $foodName")

                binding.cardBasket.visibility = View.VISIBLE
                binding.tvAmount.text = result.toString()+"X"
                binding.tvPrice.text = price

                binding.cardBasket.setOnClickListener {
                    val intentDetail = Intent(this@RestaurantDetailActivity, PlaceOrderActivity::class.java)
                    intentDetail.putExtra(getString(R.string.intent_id), it.id)
                    intentDetail.putExtra("result", result)
                    intentDetail.putExtra(getString(R.string.intent_restaurant_name), foodName)
                    intentDetail.putExtra("price", result_price)
                    startActivity(intentDetail)
                }

            }
    }

    override fun onBackPressed() {
        val builder =
            AlertDialog.Builder(this)
        builder.setTitle("Confirmation")
        builder.setMessage("do you want to change the restaurant?")
        builder.setPositiveButton(
            "Yes"
        ) { dialog, id ->
            super@RestaurantDetailActivity.onBackPressed()
        }
        builder.setNegativeButton(
            "No"
        ) { dialog, id -> }
        builder.show()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    }
}