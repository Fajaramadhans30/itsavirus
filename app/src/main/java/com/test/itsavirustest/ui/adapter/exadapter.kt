package com.test.itsavirustest.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.test.itsavirustest.R
import com.test.itsavirustest.databinding.ItemListRestaurantBinding
import com.test.itsavirustest.model.RestaurantModel
import com.test.itsavirustest.util.Constant


class exviewholder(private val items: MutableList<RestaurantModel> = mutableListOf()) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var binding: ItemListRestaurantBinding
//    private val items = mutableListOf<RestaurantModel>()


    @NonNull
    override fun onCreateViewHolder(
        @NonNull parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == Constant.VIEW_TYPE_ITEM) {
            binding = DataBindingUtil.inflate(
                inflater, R.layout.item_list_restaurant, parent, false
            )
            ItemViewHolder(binding.root)
        } else {
            binding = DataBindingUtil.inflate(
                inflater, R.layout.item_list_restaurant, parent, false
            )
            LoadingViewHolder(binding.root)
        }
    }

    fun addListRestaurant(posters: List<RestaurantModel>) {
        items.clear()
        items.addAll(posters)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(
        @NonNull viewHolder: RecyclerView.ViewHolder,
        position: Int
    ) {
        if (viewHolder is ItemViewHolder) {
            populateItemRows(viewHolder, position)
        } else if (viewHolder is LoadingViewHolder) {
            showLoadingView(viewHolder, position)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    /**
     * The following method decides the type of ViewHolder to display in the RecyclerView
     *
     * @param position
     * @return
     */
    override fun getItemViewType(position: Int): Int {
        return Constant.VIEW_TYPE_ITEM
    }

    private inner class ItemViewHolder(@NonNull itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var tvRestoName: TextView = binding.tvRestoName
        var tvRestoCategory: TextView = binding.tvRestoCategory
        var tvRank: TextView = binding.tvRank

    }

    private inner class LoadingViewHolder(@NonNull itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var progressBar: ProgressBar = itemView.findViewById(R.id.progressBar1)

    }

    private fun showLoadingView(
        viewHolder: LoadingViewHolder,
        position: Int
    ) {
        //ProgressBar would be displayed
    }

    private fun populateItemRows(
        viewHolder: ItemViewHolder,
        position: Int
    ) {
        val item = items[position]
        viewHolder.tvRestoName.text = item.name
        viewHolder.tvRestoCategory.text = item.balance.toString()
        viewHolder.tvRank.text = item.balance.toString()
    }

}