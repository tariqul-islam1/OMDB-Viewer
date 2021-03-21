package com.example.omdbtest.data

data class SearchResponse(
    val Response: String,
    val Search: List<SearchResultItem>?,
    val totalResults: String?
)