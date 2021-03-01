package com.test.itsavirustest.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.test.itsavirustest.R
import com.test.itsavirustest.databinding.ItemListMenuBinding
import com.test.itsavirustest.model.RestaurantMenuModel

class RestaurantMenuAdapter(private val mListener: (RestaurantMenuModel) -> Unit) :
    RecyclerView.Adapter<RestaurantMenuAdapter.RestaurantMenuViewHolder?>() {

    private val items = mutableListOf<RestaurantMenuModel>()
    private lateinit var context: Context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantMenuViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        val binding = DataBindingUtil.inflate<ItemListMenuBinding>(
            inflater, R.layout.item_list_menu, parent, false
        )
        return RestaurantMenuViewHolder(binding)
    }

    fun addListMenuRestaurant(posters: List<RestaurantMenuModel>) {
        items.clear()
        items.addAll(posters)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RestaurantMenuViewHolder, position: Int) {
        val item = items[position]
        holder.binding.apply {
            restaurantmenu = item
            holder.binding.tvFoodname.text = this.restaurantmenu?.name
            holder.binding.tvPrice.text = this.restaurantmenu?.price.toString()

            holder.itemView.setOnClickListener {
                mListener(item)
            }

        }


    }

    override fun getItemCount(): Int = items.size

    class RestaurantMenuViewHolder(val binding: ItemListMenuBinding) :
        RecyclerView.ViewHolder(binding.root)

}