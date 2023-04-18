package com.example.unsplash.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Location(
    val city: String? = " - ",
    val country: String? = " - ",
    val position: Position
)