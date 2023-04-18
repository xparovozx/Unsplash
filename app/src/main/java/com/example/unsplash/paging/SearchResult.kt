package com.example.unsplash.paging

import com.example.unsplash.models.Photo
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Collections.emptyList

@Serializable
@JsonClass(generateAdapter = true)
data class SearchResult(
    val total: Int = 0,
    val results: List<Photo> = emptyList()
)