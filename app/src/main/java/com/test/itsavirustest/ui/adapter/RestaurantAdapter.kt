package com.test.itsavirustest.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.test.itsavirustest.R
import com.test.itsavirustest.databinding.ItemListRestaurantBinding
import com.test.itsavirustest.model.RestaurantModel
import com.test.itsavirustest.util.Constant

class RestaurantAdapter(private val mListener: (RestaurantModel) -> Unit) :
    RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder?>() {

    private val items = mutableListOf<RestaurantModel>()
    private lateinit var context: Context
    private lateinit var binding: ItemListRestaurantBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        if (viewType == Constant.VIEW_TYPE_ITEM) {
            binding = DataBindingUtil.inflate(
                inflater, R.layout.item_list_restaurant, parent, false
            )
            return RestaurantViewHolder(binding)
        } else {
            binding = DataBindingUtil.inflate(
                inflater, R.layout.progress_loading, parent, false
            )
        }
        return RestaurantViewHolder(binding)
    }

    fun addListRestaurant(posters: List<RestaurantModel>) {
        items.clear()
        items.addAll(posters)
        Log.d("ISIIII ", "onBindViewHolder: " + items.size)

        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val item = items[position]
        holder.binding.apply {
            restaurant = item
            holder.binding.tvRestoName.text = this.restaurant?.name
            if (restaurant?.balance != null) {
                holder.binding.tvRestoCategory.text = this.restaurant?.balance.toString()
                holder.binding.tvRank.text = this.restaurant?.balance.toString()
            } else {
                holder.binding.tvRestoCategory.text = this.restaurant?.total_amount.toString()
                holder.binding.tvRank.text = this.restaurant?.total_amount.toString()
            }
            holder.itemView.setOnClickListener {
                mListener(item)
            }

        }


    }

    override fun getItemCount(): Int = items.size

    class RestaurantViewHolder(val binding: ItemListRestaurantBinding) :
        RecyclerView.ViewHolder(binding.root)
}
