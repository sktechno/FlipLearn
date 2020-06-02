package com.sk.fliplearn

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sk.fliplearn.databinding.ItemViewBinding
import java.util.regex.Pattern


//
// Created by SK(Sk) on 01/06/20.
// Copyright (c) 2020 Sktech. All rights reserved.

class Adapter : RecyclerView.Adapter<Adapter.ViewHolder>(), Filterable {


    private val listFull = ArrayList<Product>()
    private val list = ArrayList<Product>()

    fun addList(list: ArrayList<Product>) {
        listFull.clear()
        this.list.clear()
        this.list.addAll(list)
        this.listFull.addAll(list)
        notifyDataSetChanged()
    }

    /**
     * Called when RecyclerView needs a new [ViewHolder] of the given type to represent
     * an item.
     *
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     *
     *
     * The new ViewHolder will be used to display items of the adapter using
     * [.onBindViewHolder]. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary [View.findViewById] calls.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     * an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return A new ViewHolder that holds a View of the given view type.
     * @see .getItemViewType
     * @see .onBindViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val inflate = LayoutInflater.from(parent.context)
        val binding =
            DataBindingUtil.inflate<ItemViewBinding>(inflate, R.layout.item_view, parent, false)

        return ViewHolder(binding)
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount(): Int {

        return list.size
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the [ViewHolder.itemView] to reflect the item at the given
     * position.
     *
     *
     * Note that unlike [android.widget.ListView], RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the `position` parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use [ViewHolder.getAdapterPosition] which will
     * have the updated adapter position.
     *
     * Override [.onBindViewHolder] instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     * item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = list[position]
        holder.setProduct(product)

    }


    override fun getFilter(): Filter {
        return myFilter
    }


    private val myFilter: Filter = object : Filter() {
        override fun performFiltering(wordSearch: CharSequence?): FilterResults {

            val filterList = ArrayList<Product>()

            if (TextUtils.isEmpty(wordSearch)) {
                filterList.addAll(listFull)
            } else {

                for (product in listFull) {
//                    val stringArray = product.title?.toLowerCase()?.split(" ")
//                    stringArray?.let {
//                        if (it.containsAll(wordArray)) {
//                            filterList.add(product)
//                        }
//                    }

                    val wordArray = wordSearch.toString().toLowerCase().trim().split(" ")
                        for (word in wordArray) {
                            val pattern = Pattern.compile("\\b$word\\b", Pattern.CASE_INSENSITIVE)
                            val matcher = pattern.matcher(product.title!!)
                            if (matcher.find()) {
                                if (!filterList.contains(product))
                                    filterList.add(product)

                            }
                        }


                }

            }

            val filterResult = FilterResults()
            filterResult.values = filterList

            return filterResult
        }

        override fun publishResults(word: CharSequence?, result: FilterResults?) {
            result?.let {

                list.clear()
                list.addAll(it.values as ArrayList<Product>)
                notifyDataSetChanged()

            }

        }
    }

    inner class ViewHolder : RecyclerView.ViewHolder {

        private val binding: ItemViewBinding

        constructor(binding: ItemViewBinding) : super(binding.root) {
            this.binding = binding
        }

        fun setProduct(product: Product) {
            binding.model = product
            binding.executePendingBindings()

        }
    }


}