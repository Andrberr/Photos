package com.example.photos.ui.photos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.photos.databinding.PhotoLayoutBinding
import com.example.photos.domain.models.PhotoModel

class PhotosAdapter : ListAdapter<PhotoModel, PhotosViewHolder>(DiffCallback()) {

    var itemClick: (photoId: Int) -> Unit = {}
    var itemDelete: (id: Int) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosViewHolder {
        val binding = PhotoLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotosViewHolder(binding, itemClick, itemDelete)
    }

    override fun onBindViewHolder(holder: PhotosViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<PhotoModel>() {
        override fun areItemsTheSame(oldPhoto: PhotoModel, newPhoto: PhotoModel): Boolean {
            return oldPhoto.id == newPhoto.id
        }

        override fun areContentsTheSame(oldPhoto: PhotoModel, newPhoto: PhotoModel): Boolean {
            return newPhoto == oldPhoto
        }
    }
}