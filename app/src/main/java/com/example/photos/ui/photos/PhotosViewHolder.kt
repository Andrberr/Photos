package com.example.photos.ui.photos

import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.recyclerview.widget.RecyclerView
import com.example.photos.R
import com.example.photos.databinding.PhotoLayoutBinding
import com.example.photos.domain.models.PhotoModel

class PhotosViewHolder(
    private val binding: PhotoLayoutBinding,
    private val itemClick: (photoId: Int) -> Unit,
    private val itemDelete: (id: Int) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(photo: PhotoModel) {
        loadImage(photo.url)
        binding.photoNameView.text = photo.name
        binding.dateView.text = photo.dateTime
        itemView.setOnClickListener {
            itemClick(photo.id)
        }
        itemView.setOnLongClickListener {
            showDeleteConfirmationDialog(photo.id)
            true
        }
    }

    private fun loadImage(base64Image: String) {
        val imageBytes = Base64.decode(base64Image, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        binding.imageView.setImageBitmap(bitmap)
    }

    private fun showDeleteConfirmationDialog(id: Int) {
        val alertDialogBuilder = AlertDialog.Builder(itemView.context)
        alertDialogBuilder.setMessage(itemView.context.getString(R.string.delete_question))

        alertDialogBuilder.setPositiveButton(itemView.context.getString(R.string.yes)) { _, _ ->
            itemDelete(id)
        }

        alertDialogBuilder.setNegativeButton(itemView.context.getString(R.string.no)) { dialog, _ ->
            dialog.cancel()
        }
        alertDialogBuilder.show()
    }
}