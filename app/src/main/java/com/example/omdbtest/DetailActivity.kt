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

    private lateinit var plotTV: TextView
    private lateinit var writerTV: TextView
    private lateinit var starringTV: TextView
    private lateinit var directorTV: TextView
    private lateinit var ratingInfoTV: TextView
    private lateinit var detailInfoTV: TextView
    private lateinit var detailTitleTV: TextView
    private lateinit var bigImageView: ImageView
    private lateinit var compositeDisposable: CompositeDisposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        initViews()
        fillViews()
    }

    private fun initViews() {
        this.plotTV = findViewById(R.id.plot)
        this.writerTV = findViewById(R.id.writer)
        this.directorTV = findViewById(R.id.director)
        this.starringTV = findViewById(R.id.starring)
        this.ratingInfoTV = findViewById(R.id.rating)
        this.bigImageView = findViewById(R.id.bigImage)
        this.compositeDisposable = CompositeDisposable()
        this.detailInfoTV = findViewById(R.id.detailInfo)
        this.detailTitleTV = findViewById(R.id.detailTitle)
    }

    fun goBack(view: View) {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        this.compositeDisposable.dispose()
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
                val info = java.lang.StringBuilder()
                info.append(it.Rated)
                info.append(" | ")
                info.append(it.Runtime)
                info.append(" | ")
                info.append(it.Genre)
                info.append(" | ")
                info.append(it.Released)
                this.detailInfoTV.text = info.toString()

                val ratingInOneLine = StringBuilder()
                ratingInOneLine.append(System.getProperty("line.separator"))
                for (i in it.Ratings.indices){
                    val ratedBy = if(it.Ratings[i].Source == "Internet Movie Database") "IMDB" else it.Ratings[i].Source
                    ratingInOneLine.append(ratedBy)
                    ratingInOneLine.append(": ")
                    ratingInOneLine.append(it.Ratings[i].Value)
                    ratingInOneLine.append(System.getProperty("line.separator"))
                }
                this.ratingInfoTV.text = ratingInOneLine.toString()
                this.plotTV.text = it.Plot
                this.directorTV.text = it.Director
                this.writerTV.text = it.Writer
                this.starringTV.text = it.Actors
            }
        this.compositeDisposable.add(disposable)
    }
}