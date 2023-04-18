package com.example.unsplash.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.unsplash.R
import com.example.unsplash.databinding.PhotoItemBinding
import com.example.unsplash.models.Photo

class FeedAdapter(
    diffCallback: DiffUtil.ItemCallback<Photo>
) : PagingDataAdapter<Photo, FeedAdapter.PhotoHolder>(diffCallback) {
    private var listener: AdaptersListener? = null
    fun setOnClickListener(onClickListener: AdaptersListener) {
        this.listener = onClickListener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PhotoHolder {
        val itemBinding =
            PhotoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoHolder(
            itemBinding
        )
    }

    override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

   inner class PhotoHolder(
        private val binding: PhotoItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(value: Photo) {
            val photoImageView = itemView.findViewById<ImageView>(R.id.imageView)
            val photographerImageView = itemView.findViewById<ImageView>(R.id.profile_image)
            ViewCompat.setTransitionName(photoImageView, value.id)
            binding.likeCount.text = value.likes.toString()
            binding.authorName.text = value.author.username
            makeButtonUnderPhotoInvisible()
            Glide.with(itemView)
                .load(value.author.profile_image.profile_small)
                .error(R.drawable.avatar_nobody)
                .into(photographerImageView)
            Glide.with(itemView)
                .load(value.urls.small)
                .error(R.drawable.no_image)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        makeButtonUnderPhotoVisible(value)
                        return false
                    }
                })
                .into(photoImageView)
            initButtonsListeners(value)
        }

       private fun initButtonsListeners(photo: Photo?) {
           binding.imageView.setOnClickListener {
               photo?.let { photo -> listener?.onClickItem(photo) }
           }
           binding.likePhotoButton.setOnClickListener {
               photo?.let { photo -> listener?.onClickLike(photo, absoluteAdapterPosition)}
               binding.likePhotoButton.isVisible = false
               binding.unlikePhotoButton.isVisible = true
           }
           binding.unlikePhotoButton.setOnClickListener {
               photo?.let { photo -> listener?.onClickLike(photo, absoluteAdapterPosition)}
               binding.likePhotoButton.isVisible = true
               binding.unlikePhotoButton.isVisible = false
           }
           binding.shadowRectangle.setOnClickListener {
               if (photo != null) {
                   openAuthorProfile(photo)
               }
           }
           binding.profileImage.setOnClickListener {
               if (photo != null) {
                   openAuthorProfile(photo)
               }
           }
           binding.authorLogin.setOnClickListener {
               if (photo != null) {
                   openAuthorProfile(photo)
               }
           }
           binding.authorName.setOnClickListener {
               if (photo != null) {
                   openAuthorProfile(photo)
               }
           }
       }

       fun openAuthorProfile(photoForProfile: Photo) {
           photoForProfile.let { photo -> listener?.onClickInfo(photo)}
       }

        private fun makeButtonUnderPhotoInvisible() {
            binding.imageItemInfo.isVisible = false
            binding.likePhotoButton.isVisible = false
            binding.unlikePhotoButton.isVisible = false
        }

        private fun makeButtonUnderPhotoVisible(value: Photo) {
            binding.likePhotoButton.isVisible = value.liked
            binding.unlikePhotoButton.isVisible = !value.liked
            binding.imageItemInfo.isVisible = true
        }
    }
}