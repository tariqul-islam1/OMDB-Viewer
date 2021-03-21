package com.example.omdbtest.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var mCurrentPosition = 0
    protected abstract fun clear()
    open fun onBind(position: Int) {
        mCurrentPosition = position
        clear()
    }
}