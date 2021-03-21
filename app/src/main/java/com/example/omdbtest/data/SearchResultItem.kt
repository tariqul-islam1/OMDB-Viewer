package com.example.omdbtest.data

import java.io.Serializable

data class SearchResultItem(
    val Poster: String,
    val Title: String,
    val Type: String,
    val Year: String,
    val imdbID: String
): Serializable