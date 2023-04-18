package com.example.unsplash.adapters

import androidx.recyclerview.widget.DiffUtil
import com.example.unsplash.models.Photo

object PhotoComparator:  DiffUtil.ItemCallback<Photo>() {
    override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
        return oldItem == newItem
    }
}