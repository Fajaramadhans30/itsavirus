package com.test.itsavirustest

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import cn.pedant.SweetAlert.SweetAlertDialog
import com.test.itsavirustest.databinding.PlaceOrderActivityBinding
import com.test.itsavirustest.model.OrderRealmModel
import io.realm.Realm
import io.realm.RealmConfiguration


class PlaceOrderActivity :AppCompatActivity() {
    private lateinit var binding :PlaceOrderActivityBinding
    private lateinit var realm :Realm
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
        supportActionBar?.title = intent.getStringExtra(getString(R.string.intent_restaurant_name))
        supportActionBar?.subtitle = Html.fromHtml("<font color='#F1F1F1'>Distance from you 0.5 km</font>")
        /*REALM*/
        Realm.init(this);
        val configuration = RealmConfiguration.Builder()
            .name("Myorder.db")
            .deleteRealmIfMigrationNeeded()
            .schemaVersion(0)
            .build()
        Realm.setDefaultConfiguration(configuration)
        realm = Realm.getDefaultInstance()


        binding.btnPlaceOrder.setOnClickListener {
            saveDataToRealm(foodName, result)
        }


    }

    private fun saveDataToRealm(foodName: String, result: Int?) {
        try {

            realm.beginTransaction()
            val currentIdNumber :Number? = realm.where(OrderRealmModel::class.java).max("id")
            val nextID :Int

            nextID = if (currentIdNumber == null) {
                1
            } else {
                currentIdNumber.toInt() + 1
            }

            val orderRealmModel = OrderRealmModel()
            orderRealmModel.name = foodName
            orderRealmModel.total_amount = result
            orderRealmModel.id = nextID

            realm.copyToRealmOrUpdate(orderRealmModel)
            realm.commitTransaction()

            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("You can't cancel it if it's already ordered!")
                .setConfirmText("Order!")
                .setConfirmClickListener { sDialog ->
                    val intentDetail = Intent(this, MainActivity::class.java)
                    startActivity(intentDetail)
                    sDialog.dismissWithAnimation()
                }
                .setCancelButton(
                    "Cancel"
                ) { sDialog -> sDialog.dismissWithAnimation() }
                .show()

//            Toast.makeText(this, "Order Added Successfully", Toast.LENGTH_SHORT).show()
        } catch (e:Exception) {
            Log.d("MASUK KESINI KAH ?", "saveDataToRealm: " + e)
            Toast.makeText(this, "GAGALLL $e", Toast.LENGTH_SHORT).show()

        }
    }
}