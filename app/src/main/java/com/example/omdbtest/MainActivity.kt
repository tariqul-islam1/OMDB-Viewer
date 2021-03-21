package com.example.omdbtest

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.omdbtest.network.OMDBService
import com.example.omdbtest.ui.PaginationListener
import com.example.omdbtest.ui.PaginationListener.Companion.PAGE_START
import com.example.omdbtest.ui.SearchAdapter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener,
    OnEditorActionListener {

    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var searchResultsRV: RecyclerView
    private lateinit var noResultMessageTV: TextView
    private lateinit var omdbService: OMDBService
    private lateinit var searchInputET: EditText
    private lateinit var adapter: SearchAdapter
    private var currentPage: Int = PAGE_START
    private var isLastPage = false
    private var isLoading = false
    private var totalItems = 0
    private var itemCount = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        userSearchKeyNew = "sniper"
        initViews()
        setListenersToViews()
//        makeOMDBCall("sniper")
    }

    private fun initViews() {
        this.noResultMessageTV = findViewById(R.id.noResultMessage)
        this.searchResultsRV = findViewById(R.id.recyclerView)
        this.swipeRefresh = findViewById(R.id.swipeRefresh)
        this.searchInputET = findViewById(R.id.searchInput)
        this.compositeDisposable = CompositeDisposable()
        this.omdbService = OMDBService.create()
    }

    private fun setListenersToViews() {
        this.adapter = SearchAdapter(ArrayList())
        this.searchResultsRV.adapter = adapter
        this.swipeRefresh.setOnRefreshListener(this)
        this.searchResultsRV.setHasFixedSize(true)
        this.searchInputET.setOnEditorActionListener(this)
        val linearLayoutManager = LinearLayoutManager(this)
        this.searchResultsRV.layoutManager = linearLayoutManager
        this.searchResultsRV.addOnScrollListener(object : PaginationListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isLoading = true
                currentPage++
                makeOMDBCall(searchInputET.text.toString())
            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }
        })
    }

    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onRefresh() {
        val searchKey = searchInputET.text.toString()
        if (searchKey.isNotEmpty()) {
            clearMovieList()
            makeOMDBCall(searchKey)
        } else {
            val disposable = Observable.timer(200, TimeUnit.MICROSECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    swipeRefresh.isRefreshing = false
                }
            this.compositeDisposable.add(disposable)
        }
    }

    private fun clearMovieList() {
        itemCount = 0
        currentPage = PAGE_START
        isLastPage = false
        adapter.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        this.compositeDisposable.dispose()
    }

    override fun onEditorAction(tv: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            hideKeyboard()
            clearMovieList()
            makeOMDBCall(tv?.text.toString())
            return true
        }
        return false
    }

    fun search(view: View) {
        hideKeyboard()
        clearMovieList()
        makeOMDBCall(searchInputET.text.toString())
    }

    private fun makeOMDBCall(searchKey: String) {
        if (searchKey.trim().isEmpty()) {
            noResultMessageTV.visibility = View.VISIBLE
            return
        }
        val disposable =
            this.omdbService.searchMovies(
                OMDBService._API_KEY,
                searchKey,
                this.currentPage.toString()
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
//                .delaySubscription(1, TimeUnit.SECONDS)
                .map { it ->
                    it.totalResults?.let {
                        this.totalItems = it.toInt()
                    }
                    it.Search
                }
                .subscribe({ res ->
                    res?.let {
                        noResultMessageTV.visibility = View.INVISIBLE
                        itemCount += it.size
                        if (currentPage != PAGE_START) adapter.removeLoading()
                        adapter.addItems(it)
                        swipeRefresh.isRefreshing = false
                        if (this.itemCount < this.totalItems) {
                            adapter.addLoading()
                        } else {
                            isLastPage = true
                        }
                        isLoading = false
                    }
                }, { error ->
                    println(error.message)
//                    adapter.removeLoading()
                    noResultMessageTV.visibility = View.VISIBLE
                })
        this.compositeDisposable.add(disposable)
    }
}