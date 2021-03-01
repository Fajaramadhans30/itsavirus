package com.test.itsavirustest.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.test.itsavirustest.R
import com.test.itsavirustest.databinding.ItemListMyorderBinding
import com.test.itsavirustest.databinding.ItemListRestaurantBinding
import com.test.itsavirustest.model.OrderRealmModel
import com.test.itsavirustest.model.RestaurantModel
import com.test.itsavirustest.util.Constant
import io.realm.RealmResults
import kotlinx.android.synthetic.main.item_list_myorder.view.*

class MyOrderAdapter(private val context: Context, private val items :RealmResults<OrderRealmModel>)
                     :RecyclerView.Adapter<MyOrderAdapter.MyOrderViewHolder>() {


    private lateinit var binding: ItemListMyorderBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyOrderViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == Constant.VIEW_TYPE_ITEM) {
            binding = DataBindingUtil.inflate(
                inflater, R.layout.item_list_myorder, parent, false
            )
            return MyOrderViewHolder(binding)
        } else {
            binding = DataBindingUtil.inflate(
                inflater, R.layout.progress_loading, parent, false
            )
        }
        return MyOrderViewHolder(binding)
    }

//    fun addMyOrderlist(posters: List<RestaurantModel>) {
//        items.clear()
//        items.addAll(posters)
//        Log.d("ISIIII ", "onBindViewHolder: " + items.size)
//
//        notifyDataSetChanged()
//    }

    override fun onBindViewHolder(holder: MyOrderViewHolder, position: Int) {
        val item = items[position]
        holder.binding.apply {
            holder.binding.tvFoodname.text = item?.name
            holder.binding.tvTotalMount.text = item?.total_amount.toString() + " Item"
        }


    }

    override fun getItemCount(): Int = items.size

    class MyOrderViewHolder(val binding: ItemListMyorderBinding) :
        RecyclerView.ViewHolder(binding.root)
}