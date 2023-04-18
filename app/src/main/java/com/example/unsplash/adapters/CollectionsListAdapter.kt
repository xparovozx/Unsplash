package com.example.unsplash.adapters

import androidx.recyclerview.widget.DiffUtil
import com.example.unsplash.models.CollectionPhoto
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter

class CollectionsListAdapter(
    onItemCliked: (position: Int) -> Unit
): AsyncListDifferDelegationAdapter<CollectionPhoto>(MovieDiffUtilCalback()){

    init {
        delegatesManager.addDelegate(CollectionsDelegateAdapter(onItemCliked))
    }

    class MovieDiffUtilCalback : DiffUtil.ItemCallback<CollectionPhoto>() {
        override fun areItemsTheSame(oldItem: CollectionPhoto, newItem: CollectionPhoto): Boolean {
           return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CollectionPhoto, newItem: CollectionPhoto): Boolean {
            return oldItem == newItem
        }
    }
}