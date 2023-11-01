package com.example.photos.ui.maps

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.photos.R
import com.example.photos.databinding.FragmentPhotoInfoDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PhotoInfoDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentPhotoInfoDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoInfoDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val url = arguments?.getString(PHOTO_KEY)
        val name = arguments?.getString(NAME_KEY) ?: ""
        val dateTime = arguments?.getString(DATE_KEY) ?: ""
        val lat = arguments?.getString(LAT_KEY) ?: ""
        val lng = arguments?.getString(LNG_KEY) ?: ""
        if (url != null) {
            loadImage(url)
        }
        binding.photoNameView.text = name
        binding.dateView.text = dateTime
        binding.coordinatesView.text = getCoordinatesText(lat, lng)
    }

    private fun loadImage(base64Image: String) {
        val imageBytes = Base64.decode(base64Image, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        binding.photoView.setImageBitmap(bitmap)
    }

    private fun getCoordinatesText(lat: String, lng: String): String {
        return getString(R.string.coordinates) + SPACE + lat + getString(R.string.comma) + SPACE + lng
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance(url: String, name: String, dateTime: String, lat: String, lng: String) =
            PhotoInfoDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(PHOTO_KEY, url)
                    putString(NAME_KEY, name)
                    putString(DATE_KEY, dateTime)
                    putString(LAT_KEY, lat)
                    putString(LNG_KEY, lng)
                }
            }

        private const val PHOTO_KEY = "photo"
        private const val NAME_KEY = "name"
        private const val DATE_KEY = "date_time"
        private const val LAT_KEY = "lat"
        private const val LNG_KEY = "lng"
        private const val SPACE = " "
    }
}