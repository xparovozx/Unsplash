package com.example.unsplash.adapters

import com.example.unsplash.models.Photo

interface AdaptersListener {
    fun onClickItem(photo: Photo)
    fun onClickLike(photo: Photo, position: Int)
    fun onClickInfo(photo: Photo)
}