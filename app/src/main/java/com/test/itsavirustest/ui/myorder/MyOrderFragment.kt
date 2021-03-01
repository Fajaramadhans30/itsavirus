package com.test.itsavirustest.ui.myorder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.itsavirustest.R
import com.test.itsavirustest.databinding.FragmentMyorderBinding
import com.test.itsavirustest.model.OrderRealmModel
import com.test.itsavirustest.ui.adapter.MyOrderAdapter
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults

class MyOrderFragment : Fragment() {

    private lateinit var dashboardViewModel: MyOrderViewModel
    private lateinit var binding: FragmentMyorderBinding
    private lateinit var myOrderRv: RecyclerView
    private lateinit var myOrderList: ArrayList<OrderRealmModel>
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var realm: Realm

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
                ViewModelProvider(this).get(MyOrderViewModel::class.java)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_myorder, container, false)

        myOrderRv = binding.rvMyOrder
        myOrderList = ArrayList()
        myOrderList.clear()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*REALM*/
        Realm.init(view.context);
        val configuration = RealmConfiguration.Builder()
            .name("Myorder.db")
            .deleteRealmIfMigrationNeeded()
            .schemaVersion(0)
            .build()
        Realm.setDefaultConfiguration(configuration)
        realm = Realm.getDefaultInstance()

        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        myOrderRv.layoutManager = linearLayoutManager

        getMyOrder()
    }

    private fun getMyOrder() {
        myOrderList.clear()
        myOrderList = ArrayList()

        val result: RealmResults<OrderRealmModel> = realm.where<OrderRealmModel>(OrderRealmModel::class.java).findAll()
        myOrderRv.adapter = view?.context?.let { MyOrderAdapter(it, result) }
        myOrderRv.adapter!!.notifyDataSetChanged()
    }

    companion object {
        fun newInstance(): MyOrderFragment = newInstance()
    }
}