package com.example.omdbtest.ui

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


abstract class PaginationListener :
    RecyclerView.OnScrollListener() {

    companion object {
        const val pageStart = 1
        var isLoading = false
        var isLastPage = false
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        recyclerView.layoutManager?.let { lm ->
            val firstVisibleItemPosition =
                (lm as LinearLayoutManager).findFirstVisibleItemPosition()
            if (!isLoading && !isLastPage) {
                if (lm.childCount + firstVisibleItemPosition >= lm.itemCount && firstVisibleItemPosition >= 0) {
                    recyclerView.layoutManager?.let {
                        val view = it.findViewByPosition(it.itemCount - 1)
                        view?.visibility = View.VISIBLE
                        loadMoreItems()
                    }
                }
            }
        }

    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        if (!recyclerView.canScrollVertically(1) && isSearchKeyEmpty()) {
            recyclerView.layoutManager?.let {
                val view = it.findViewByPosition(it.itemCount - 1)
                view?.visibility = View.INVISIBLE
            }
        }
    }

    protected abstract fun loadMoreItems()
    protected abstract fun isSearchKeyEmpty(): Boolean
}