package com.example.omdbtest.ui

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.omdbtest.DetailActivity
import com.example.omdbtest.R
import com.example.omdbtest.data.SearchResultItem
import com.squareup.picasso.Picasso

class SearchAdapter(private val searchItems: MutableList<SearchResultItem>) :
    RecyclerView.Adapter<BaseViewHolder>() {
    private val VIEW_TYPE_LOADING = 0
    private val VIEW_TYPE_NORMAL = 1
    private var isLoaderVisible = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        if (viewType == VIEW_TYPE_LOADING) {
            return ProgressHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_loading,
                    parent,
                    false
                )
            )
        } else {
            return ViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.search_list_item,
                    parent,
                    false
                ),
                searchItems
            )
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoaderVisible) {
            if (position == searchItems.size - 1) VIEW_TYPE_LOADING else VIEW_TYPE_NORMAL
        } else {
            VIEW_TYPE_NORMAL
        }
    }

    override fun getItemCount() = searchItems.size

    fun addItems(newSearchItems: List<SearchResultItem>?) {
        newSearchItems?.let {
            searchItems.addAll(newSearchItems)
        }
        notifyDataSetChanged()
    }

    fun addLoading() {
        isLoaderVisible = true
        searchItems.add(SearchResultItem("", "", "", "", ""))
        notifyItemInserted(searchItems.size - 1)
    }

    fun removeLoading() {
        isLoaderVisible = false
        searchItems.let {
            val position = searchItems.size - 1
            val item = getItem(position)
            item.let {
                searchItems.removeAt(position)
                notifyItemRemoved(position)
            }
        }
    }

    fun clear() {
        searchItems.clear()
        notifyDataSetChanged()
    }

    private fun getItem(position: Int): SearchResultItem {
        return searchItems[position]
    }

    class ViewHolder(itemView: View, private val searchItems: MutableList<SearchResultItem>) :
        BaseViewHolder(itemView) {
        var poster: ImageView = itemView.findViewById(R.id.postar)
        var movieTitle: TextView = itemView.findViewById(R.id.movieTitle)
        var releaseYear: TextView = itemView.findViewById(R.id.releaseYear)

        override fun clear() {}
        override fun onBind(position: Int) {
            super.onBind(position)
            movieTitle.text = searchItems[position].Title
            releaseYear.text = searchItems[position].Year
            if(searchItems[position].Poster.isNotEmpty()){
                Picasso.get().load(searchItems[position].Poster).into(poster)
            }
            itemView.setOnClickListener {
                val intent = Intent(it.context, DetailActivity::class.java)
                intent.putExtra(movieKey, searchItems[position])
                it.context.startActivity(intent)
            }
        }
    }

    class ProgressHolder internal constructor(itemView: View) : BaseViewHolder(itemView) {
        override fun clear() {}
    }

    companion object {
        const val movieKey = "movieKey"
    }
}