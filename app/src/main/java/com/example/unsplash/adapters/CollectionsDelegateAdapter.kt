package com.example.unsplash.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.unsplash.models.CollectionPhoto
import com.example.unsplash.databinding.CollectionItemBinding
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate

class CollectionsDelegateAdapter(
    private val onItemCliked: (position: Int) -> Unit,
):
    AbsListItemAdapterDelegate<CollectionPhoto, CollectionPhoto, CollectionsDelegateAdapter.RepositoryHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): RepositoryHolder {
        return RepositoryHolder(onItemCliked, CollectionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun isForViewType(
        item: CollectionPhoto,
        items: MutableList<CollectionPhoto>,
        position: Int
    ): Boolean {
        return true
    }

    override fun onBindViewHolder(
        item: CollectionPhoto,
        holder: RepositoryHolder,
        payloads: MutableList<Any>
    ) {
        holder.bind(item)
    }

    class RepositoryHolder(
        onItemCliked: (position: Int) -> Unit,
        binding: CollectionItemBinding
        ): RecyclerView.ViewHolder(binding.root){
        init {
            binding.root.setOnClickListener {
                onItemCliked(absoluteAdapterPosition)
            }
        }

        fun bind (collection: CollectionPhoto) {
        }
    }
}