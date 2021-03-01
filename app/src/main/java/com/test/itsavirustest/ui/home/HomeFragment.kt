package com.test.itsavirustest.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.view.size
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar
import com.google.android.gms.location.*
import com.google.android.material.chip.Chip
import com.skydoves.whatif.whatIfNotNullOrEmpty
import com.test.itsavirustest.MapActivity
import com.test.itsavirustest.R
import com.test.itsavirustest.RestaurantDetailActivity
import com.test.itsavirustest.databinding.FragmentHome2Binding
import com.test.itsavirustest.model.RestaurantModel
import com.test.itsavirustest.network.RestaurantProvider
import com.test.itsavirustest.ui.adapter.RestaurantAdapter
import com.test.itsavirustest.viewmodel.RestaurantViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_home_2.*
import java.util.*


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding: FragmentHome2Binding

    private lateinit var homeRestaurantViewModel: RestaurantViewModel
    private lateinit var homeRestaurantAdapter: RestaurantAdapter


    private var dataListRestaurant: MutableList<RestaurantModel?> = mutableListOf()
    private lateinit var linearLayoutManager: LinearLayoutManager

    private val compositeDisposable = CompositeDisposable()
    private val repository = RestaurantProvider.restaurantProviderRepository()

    var dialog: AlertDialog.Builder? = null
    var dialogView: View? = null
    var inflater: LayoutInflater? = null

    var addressss: String = ""

    //Declaring the needed Variables
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    val PERMISSION_ID = 1010

    var searchOption: String = ""

    val latitude: Double = 0.0
    val longitude: Double = 0.0


    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_2, container, false)
//        homeViewModel.text.observe(viewLifecycleOwner, Observer {
//            binding.tvStreetName.text = it
//        })

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvRestaurantList.layoutManager = linearLayoutManager
        binding.rvRestaurantList.hasFixedSize()
        homeRestaurantAdapter = RestaurantAdapter() {
            val intentDetail = Intent(context, RestaurantDetailActivity::class.java)
            intentDetail.putExtra(getString(R.string.intent_id), it.id)
            intentDetail.putExtra(getString(R.string.intent_restaurant_name), it.name)
            intentDetail.putExtra(getString(R.string.intent_restaurant_latitude), it.latitude)
            intentDetail.putExtra(getString(R.string.intent_restaurant_longitude), it.longitude)
            intentDetail.putExtra("balance", it.balance)
            startActivity(intentDetail)
        }
        binding.rvRestaurantList.adapter = homeRestaurantAdapter

        homeRestaurantViewModel = ViewModelProviders.of(
            this,
            RestaurantViewModel.ViewModelRestaurantFactory(
                compositeDisposable,
                repository,
                AndroidSchedulers.mainThread(),
                Schedulers.io()
            )
        ).get(RestaurantViewModel::class.java)
        homeRestaurantViewModel.setListRestaurant()
        homeRestaurantViewModel.getListRestaurant().observe(viewLifecycleOwner, getRestaurantList)

        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = true
            homeRestaurantViewModel.setListRestaurant()
            homeRestaurantViewModel.getListRestaurant()
                .observe(viewLifecycleOwner, getRestaurantList)
        }

        binding.cardNear.setOnClickListener {
            binding.rvRestaurantList.visibility = View.GONE
            binding.progressBar1.visibility = View.VISIBLE
            homeRestaurantViewModel.setListRestaurantByNear()
            homeRestaurantViewModel.getListRestaurantByNear()
                .observe(viewLifecycleOwner, getRestaurantListByNear)
            Toast.makeText(context, "KLIK NEAR", Toast.LENGTH_SHORT).show()
        }

        binding.cardFilter.setOnClickListener {
            openDialog()

        }

        binding.cardPopular.setOnClickListener {
            binding.rvRestaurantList.visibility = View.GONE
            binding.progressBar1.visibility = View.VISIBLE
            homeRestaurantViewModel.setListRestaurantByTop("total-amount", 10)
            homeRestaurantViewModel.getListRestaurantByTop()
                .observe(viewLifecycleOwner, getRestaurantListByTop)
            Toast.makeText(context, "KLIK NEAR", Toast.LENGTH_SHORT).show()
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(view.context)

//        RequestPermission()
//        getLastLocation()

        Log.d("Debug:", CheckPermission().toString())
        Log.d("Debug:", isLocationEnabled().toString())
        RequestPermission()
        /* fusedLocationProviderClient.lastLocation.addOnSuccessListener{location: Location? ->
                 textView.text = location?.latitude.toString() + "," + location?.longitude.toString()
             }*/
        getLastLocation()

        binding.lnCurrentLocation.setOnClickListener {
            val intentDetail = Intent(it.context, MapActivity::class.java)
            startActivity(intentDetail)
        }

        binding.searchFinder.isFocusableInTouchMode = true;


        binding.searchFinder.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchBy(searchOption)
                return@OnEditorActionListener true
            }
            false
        })

        binding.btnSearchOption.setOnClickListener {
            openSearchOption(binding.root)

        }

        try {
            val message = arguments?.getString("data_alamat")
            if (message != null) {
                Log.d("INI ALAMATNYAAA", "onViewCreated: $message")

            } else {
                Log.d("INI ALAMATNYAAA 2222", "onViewCreated: $message")
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }


    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val mapAddress: String = data?.extras?.getString("data_alamat").toString()
            Log.d(TAG, "onActivityResult: $mapAddress")

        }
    }

    private fun openSearchOption(root: View) {
        val listItems = arrayOf("Search by restaurant", "Search by dish")
        val mBuilder = AlertDialog.Builder(root.context)
        mBuilder.setTitle("Search option")
        mBuilder.setSingleChoiceItems(listItems, -1) { dialogInterface, i ->
            Toast.makeText(root.context, listItems[i], Toast.LENGTH_SHORT).show()
            searchOption = if (listItems[i].equals("Search by restaurant")) {
                "restaurant"
            } else {
                "dish"
            }


            val chip = Chip(context)
            binding.chipGroupSearch.visibility = View.VISIBLE
            chip.text = searchOption
            chip.isCloseIconEnabled = true
            chip.isClickable = true
            chip.isCheckable = false
            chip.setCloseIconTintResource(R.color.white)
            chip.setTextAppearanceResource(R.style.ChipTextStyle_Selected)
            chip.setChipBackgroundColorResource(R.color.colorPrimary)
            binding.chipGroupSearch.addView(chip as View)
            chip.setOnCloseIconClickListener {
                binding.chipGroupSearch.removeView(chip as View)
                searchOption = ""
            }
            dialogInterface.dismiss()

            if (chipGroupSearch.size > 1) {
                binding.chipGroupSearch.removeView(chip as View)
                Toast.makeText(context, "Please remove chips before", Toast.LENGTH_SHORT).show()
            }

            Log.d("CHOOSE SEARCH", "openSearchOption: $searchOption")
            Log.d("CHOOSE SEARCH 222", "openSearchOption: $chip")
            Log.d(TAG, "openSearchOption: " + chip.text)
        }


        // Set the neutral/cancel button click listener
        mBuilder.setNeutralButton("Cancel") { dialog, which ->
            // Do something when click the neutral button
            dialog.cancel()
        }

        val mDialog = mBuilder.create()
        mDialog.show()

    }

    private fun searchBy(searchOption: String) {
        Log.d("ISI SEARCH OPTION", "searchBy: " + searchOption)
        if (binding.searchFinder.requestFocus()) {
            val imm =
                binding.view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?

            // here is one more tricky issue
            // imm.showSoftInputMethod doesn't work well
            // and imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0) doesn't work well for all cases too
            imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }
        val searchName: String = binding.searchFinder.text.toString()

        if (searchOption.equals("restaurant") || searchOption.equals("dish")) {
            binding.searchFinder.clearFocus()
            Log.d("MASUKKK ????", "searchBy: " + "MASUK SINI")
            binding.rvRestaurantList.visibility = View.GONE
            binding.progressBar1.visibility = View.VISIBLE
            homeRestaurantViewModel.setListRestaurantSearchRelevance(searchOption, searchName)
            homeRestaurantViewModel.getListRestaurantSearchByDishName()
                .observe(viewLifecycleOwner, getRestaurantListByTop)
        } else {
            binding.searchFinder.clearFocus()
            Log.d("MASUKKK ????2222", "searchBy: " + "MASUK SINI2")

            binding.rvRestaurantList.visibility = View.GONE
            binding.progressBar1.visibility = View.VISIBLE
            homeRestaurantViewModel.setListRestaurantSearchByDishName(searchName)
            homeRestaurantViewModel.getListRestaurantSearchByDishName()
                .observe(viewLifecycleOwner, getRestaurantListByTop)
        }


        Log.d("APA YANG DICARI", "search: $searchName")
    }

    fun getLastLocation() {
        if (CheckPermission()) {
            if (isLocationEnabled()) {
                if (view?.context?.let {
                        ActivityCompat.checkSelfPermission(
                            it,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    } != PackageManager.PERMISSION_GRANTED && view?.context?.let {
                        ActivityCompat.checkSelfPermission(
                            it,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    } != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        NewLocationData()
                    } else {
                        Log.d("Debug:", "Your Location:$addressss")

//                        binding.tvCurrentLocation.text =
//                            getCityName(location.latitude, location.longitude)
                        binding.tvStreetName.text =
                            getCityName(location.latitude, location.longitude) + ", " + addressss
                    }
                }
            } else {
                Toast.makeText(
                    view?.context,
                    "Please Turn on Your device Location",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            RequestPermission()
        }
    }

    fun NewLocationData() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1
        fusedLocationProviderClient = view?.context?.let {
            LocationServices.getFusedLocationProviderClient(
                it
            )
        }!!
        if (view?.context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED && view?.context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationProviderClient!!.requestLocationUpdates(
            locationRequest, locationCallback, Looper.myLooper()
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var lastLocation: Location = locationResult.lastLocation
            Log.d("Debug:", "your last last location: " + lastLocation.longitude.toString())
//            binding.tvCurrentLocation.text =
//                getCityName(lastLocation.latitude, lastLocation.longitude)
            binding.tvStreetName.text =
                getCityName(lastLocation.latitude, lastLocation.longitude) + ", " + addressss
        }
    }

    private fun CheckPermission(): Boolean {
        //this function will return a boolean
        //true: if we have permission
        //false if not
        if (
            view?.context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            } == PackageManager.PERMISSION_GRANTED ||
            view?.context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            } == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }

        return false

    }

    fun RequestPermission() {
        //this function will allows us to tell the user to requesut the necessary permsiion if they are not garented
        ActivityCompat.requestPermissions(
            view?.context as Activity,
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    fun isLocationEnabled(): Boolean {
        //this function will return to us the state of the location service
        //if the gps or the network provider is enabled then it will return true otherwise it will return false
        var locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Debug:", "You have the Permission")
            }
        }
    }

    private fun getCityName(lat: Double, long: Double): String {
        var cityName: String = ""
        var countryName = ""
        val geoCoder = Geocoder(view?.context, Locale.getDefault())
        val Adress = geoCoder.getFromLocation(lat, long, 3)

        cityName = Adress.get(0).locality
        countryName = Adress.get(0).countryName
        addressss = Adress.get(0).getAddressLine(0)
        Log.d("Debug:", "Your City: " + cityName + " ; your Country " + countryName)
        return cityName
    }


    private fun openDialog() {
        var intMinValue: Number = 0
        var intMaxValue: Number = 0
        dialog = context?.let { AlertDialog.Builder(it) }
        inflater = layoutInflater
        dialogView = inflater?.inflate(R.layout.activity_filter, null);
        dialog!!.setView(dialogView)
        dialog!!.setCancelable(true)
        dialog!!.setTitle("Filter by price range")
        dialog?.setView(dialogView);

        // get seekbar from view
        val rangeSeekbar: CrystalRangeSeekbar =
            dialogView?.findViewById(R.id.rangeSeekbar1) as CrystalRangeSeekbar

        // get min and max text view
        val tvMin = dialogView?.findViewById(R.id.textMin1) as TextView
        val tvMax = dialogView?.findViewById(R.id.textMax1) as TextView

        // set listener
        rangeSeekbar.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            tvMin.text = minValue.toString()
            tvMax.text = maxValue.toString()
        }

        // set final value listener
        rangeSeekbar.setOnRangeSeekbarFinalValueListener { minValue, maxValue ->
            Log.d("CRS=>", "$minValue : $maxValue")

            intMinValue = minValue
            intMaxValue = maxValue
        }
        Handler().postDelayed(Runnable {
            rangeSeekbar.setMinValue(6F).setMaxValue(30F).setMinStartValue(7F).setMaxStartValue(10F)
                .apply()

        }, 5000)

        dialog?.setPositiveButton(
            "Submit"
        ) { dialog, which ->
            Log.d("CRS SIBMIT=>", "${intMinValue.toInt()}")
            Log.d("CRS SUBMIT=>", "${intMaxValue.toInt()}")

            getDishByPriceRange(intMinValue, intMaxValue)
            dialog.dismiss()
        }

        dialog?.setNegativeButton(
            "Cancel"
        ) { dialog, which -> dialog.dismiss() }

        dialog?.show()
    }

    private fun getDishByPriceRange(intMinValue: Number, intMaxValue: Number) {
        homeRestaurantViewModel = ViewModelProviders.of(
            this,
            RestaurantViewModel.ViewModelRestaurantFactory(
                compositeDisposable,
                repository,
                AndroidSchedulers.mainThread(),
                Schedulers.io()
            )
        ).get(RestaurantViewModel::class.java)
        homeRestaurantViewModel.setListRestaurantFilterByPriceRange(
            intMinValue.toInt(),
            intMaxValue.toInt()
        )
        homeRestaurantViewModel.getListRestaurantFilterByPriceRange()
            .observe(viewLifecycleOwner, getRestaurantFilterByPriceRangeList)

    }


    private val getRestaurantList = Observer<MutableList<RestaurantModel>> { restaurantItem ->
        binding.progressBar1.visibility = View.VISIBLE
        binding.rvRestaurantList.visibility = View.GONE
        if (restaurantItem != null) {
            dataListRestaurant.clear()
            binding.rvRestaurantList.visibility = View.VISIBLE
            binding.progressBar1.visibility = View.GONE
            if (restaurantItem.size > 0) {
                restaurantItem.whatIfNotNullOrEmpty {
                    binding.rvRestaurantList.visibility = View.VISIBLE
                    binding.progressBar1.visibility = View.GONE
                    (homeRestaurantAdapter as? RestaurantAdapter)?.addListRestaurant(it)
                }
            }
        } else {
            binding.rvRestaurantList.visibility = View.GONE
            binding.progressBar1.visibility = View.VISIBLE
        }
        binding.swipeRefresh.isRefreshing = false
    }

    private val getRestaurantListByNear = Observer<MutableList<RestaurantModel>> { restaurantItem ->
        if (restaurantItem != null) {
            dataListRestaurant.clear()
            binding.rvRestaurantList.visibility = View.VISIBLE
            binding.progressBar1.visibility = View.GONE
            if (restaurantItem.size > 0) {
                restaurantItem.whatIfNotNullOrEmpty {
                    binding.rvRestaurantList.visibility = View.VISIBLE
                    binding.progressBar1.visibility = View.GONE
                    (homeRestaurantAdapter as? RestaurantAdapter)?.addListRestaurant(it)
                }
            }

        } else {
            binding.rvRestaurantList.visibility = View.GONE
            binding.progressBar1.visibility = View.VISIBLE
        }
        binding.swipeRefresh.isRefreshing = false
    }

    private val getRestaurantListByTop = Observer<MutableList<RestaurantModel>> { restaurantItem ->
        if (restaurantItem != null) {
            dataListRestaurant.clear()
            binding.rvRestaurantList.visibility = View.VISIBLE
            binding.progressBar1.visibility = View.GONE
            if (restaurantItem.size > 0) {
                restaurantItem.whatIfNotNullOrEmpty {
                    binding.rvRestaurantList.visibility = View.VISIBLE
                    binding.progressBar1.visibility = View.GONE
                    (homeRestaurantAdapter as? RestaurantAdapter)?.addListRestaurant(it)
                }
            }

        } else {
            binding.rvRestaurantList.visibility = View.GONE
            binding.progressBar1.visibility = View.VISIBLE
        }
        binding.swipeRefresh.isRefreshing = false
    }

    private val getRestaurantListSearchByDishName =
        Observer<MutableList<RestaurantModel>> { restaurantItem ->
            if (restaurantItem != null) {
                dataListRestaurant.clear()
                binding.rvRestaurantList.visibility = View.VISIBLE
                binding.progressBar1.visibility = View.GONE
                if (restaurantItem.size > 0) {
                    restaurantItem.whatIfNotNullOrEmpty {
                        binding.rvRestaurantList.visibility = View.VISIBLE
                        binding.progressBar1.visibility = View.GONE
                        (homeRestaurantAdapter as? RestaurantAdapter)?.addListRestaurant(it)
                    }
                }

            } else {
                binding.rvRestaurantList.visibility = View.GONE
                binding.progressBar1.visibility = View.VISIBLE
            }
            binding.swipeRefresh.isRefreshing = false
        }

    private val getRestaurantFilterByPriceRangeList =
        Observer<MutableList<RestaurantModel>> { restaurantItem ->
            if (restaurantItem != null) {
                dataListRestaurant.clear()
                binding.rvRestaurantList.visibility = View.VISIBLE
                binding.progressBar1.visibility = View.GONE
                restaurantItem.whatIfNotNullOrEmpty {
                    (homeRestaurantAdapter as? RestaurantAdapter)?.addListRestaurant(it)
                }
            } else {
                binding.rvRestaurantList.visibility = View.GONE
                binding.progressBar1.visibility = View.VISIBLE
            }
            binding.swipeRefresh.isRefreshing = false
        }

    companion object {
        fun newInstance(): HomeFragment = HomeFragment()
        private const val TAG = "HomeFragment"
    }

}
