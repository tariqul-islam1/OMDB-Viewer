package com.example.omdbtest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.omdbtest.data.SearchResultItem
import com.example.omdbtest.network.OMDBService
import com.example.omdbtest.ui.SearchAdapter
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class DetailActivity : AppCompatActivity() {

    private lateinit var detailTitleTV: TextView
    lateinit var detailInfoTV: TextView
    lateinit var bigImageView: ImageView
    lateinit var compositeDisposable: CompositeDisposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        initViews()
        fillViews()
    }

    private fun fillViews() {
        val searchResultItem = intent.getSerializableExtra(SearchAdapter.movieKey) as SearchResultItem
        detailTitleTV.text = searchResultItem.Title
        if(searchResultItem.Poster.isNotEmpty()){
            Picasso.get().load(searchResultItem.Poster).into(bigImageView)
        }
        val disposable = OMDBService.create().getMovieDetails(OMDBService._API_KEY, searchResultItem.imdbID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {

            }
    }

    private fun initViews() {
        detailTitleTV = findViewById(R.id.detailTitle)
        detailInfoTV = findViewById(R.id.detailInfo)
        bigImageView = findViewById(R.id.bigImage)
        this.compositeDisposable = CompositeDisposable()
    }

    fun goBack(view: View) {
        finish()
    }
}