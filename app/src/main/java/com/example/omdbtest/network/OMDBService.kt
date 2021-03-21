package com.example.omdbtest.network

import com.example.omdbtest.data.SearchResponse
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface OMDBService {
    @GET("?type=movie")
    fun searchMovies(
        @Query("apikey") apikey: String,
        @Query("s") searchKey: String,
        @Query("page") page: String
    ): Observable<SearchResponse>

    companion object {
        const val _API_KEY = "146431c"

        private const val baseURL = "http://www.omdbapi.com/"
        fun create(): OMDBService {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(
                    RxJava2CallAdapterFactory.create())
                .addConverterFactory(
                    GsonConverterFactory.create())
                .baseUrl(baseURL)
                .build()
            return retrofit.create(OMDBService::class.java)
        }
    }
}