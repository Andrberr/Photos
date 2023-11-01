package com.example.photos.ui.watch_photo

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.example.photos.BuildConfig
import com.example.photos.R
import com.example.photos.databinding.FragmentWatchPhotoBinding
import com.example.photos.domain.models.PhotoModel
import com.example.photos.ui.base.BaseFragment
import com.example.photos.ui.maps.MapKitInitializer
import com.example.photos.ui.view_model.PhotosViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WatchPhotoFragment : BaseFragment<FragmentWatchPhotoBinding>() {

    private val photosViewModel by activityViewModels<PhotosViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        onBackPressed()
        MapKitInitializer.initialize(BuildConfig.YANDEX_API_KEY, requireContext())
        _binding = setBinding(inflater, container)
        return binding.root
    }

    override fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentWatchPhotoBinding {
        return FragmentWatchPhotoBinding.inflate(inflater, container, false)
    }

    override fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    navigate(WatchPhotoFragmentDirections.actionWatchPhotoFragmentToPhotosFragment())
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        photosViewModel.photoModel.observe(viewLifecycleOwner) { photoModel ->
            initViews(photoModel)
        }
        photosViewModel.photoId?.let { photosViewModel.getPhotoModel(it) }
    }

    private fun initViews(photoModel: PhotoModel) {
        loadImage(photoModel.url)
        with(binding) {
            photoNameView.text = photoModel.name
            dateView.text = photoModel.dateTime
            coordinatesView.text = getCoordinatesText(photoModel.lat, photoModel.lng)
            showOnMapBtn.setOnClickListener {
                navigate(
                    WatchPhotoFragmentDirections.actionWatchPhotoFragmentToMapFragment(
                        true,
                        photoModel.id
                    )
                )
            }
        }
    }

    private fun loadImage(base64Image: String) {
        val imageBytes = Base64.decode(base64Image, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        binding.photoView.setImageBitmap(bitmap)
    }

    private fun getCoordinatesText(lat: Double, lng: Double): String {
        return getString(R.string.coordinates) + SPACE + lat + getString(R.string.comma) + SPACE + lng
    }

    private fun navigate(action: NavDirections) {
        findNavController().navigate(action)
    }

    companion object {
        private const val SPACE = " "
    }
}