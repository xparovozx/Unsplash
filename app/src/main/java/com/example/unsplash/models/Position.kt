package com.example.unsplash.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Position(
    val latitude: Double? = 0.0,
    val longitude: Double? = 0.0
)