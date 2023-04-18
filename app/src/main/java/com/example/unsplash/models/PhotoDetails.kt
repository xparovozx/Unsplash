package com.example.unsplash.models

import androidx.room.Embedded
import androidx.room.PrimaryKey
import com.example.unsplash.models.UrlsPhoto
import com.example.unsplash.models.Author
import com.example.unsplash.models.Exif
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PhotoDetails (
    @PrimaryKey
    val id: String,
    val likes: Int,
    val downloads: Int,
    @Json(name = "liked_by_user")
    var liked: Boolean,
    val exif: Exif,
    val location: Location,
    val tags: List<Tag>,
    val urls: UrlsPhoto,
    @Json(name = "user")
    val author: Author,
    val links: LinksPhoto
)