package com.example.unsplash.models

import com.example.unsplash.models.ProfileImage
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DetailUserProfile(
    val id: String,
    val username: String,
    val name: String,
    @Json(name = "profile_image")
    val urlProfileImages: ProfileImage
)