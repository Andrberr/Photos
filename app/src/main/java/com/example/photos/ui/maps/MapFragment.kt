package com.example.photos.ui.maps

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.photos.BuildConfig
import com.example.photos.R
import com.example.photos.databinding.FragmentMapBinding
import com.example.photos.domain.models.PhotoModel
import com.example.photos.ui.MainActivity
import com.example.photos.ui.base.BaseFragment
import com.example.photos.ui.view_model.PhotosViewModel
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.runtime.image.ImageProvider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapFragment : BaseFragment<FragmentMapBinding>() {

    private val photosViewModel by activityViewModels<PhotosViewModel>()

    private val args: MapFragmentArgs by navArgs()

    private val mapView by lazy {
        binding.mapView
    }

    private val markerClickCallbacks = mutableListOf<MapObjectTapListener>()

    override fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMapBinding {
        return FragmentMapBinding.inflate(inflater, container, false)
    }

    override fun onBackPressed() {}

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (args.isSingle) {
            initSingle()
        } else {
            initMultiple()
        }
        photosViewModel.isMenuInitialized.observe(viewLifecycleOwner) { isInitialized ->
            if (isInitialized) {
                checkCurrentFragment()
            }
        }
    }

    private fun initMultiple() {
        photosViewModel.photos.observe(viewLifecycleOwner) { photos ->
            if (photos.isNotEmpty()) putMarkers(photos)
        }
        photosViewModel.getPhotos()
    }

    private fun initSingle() {
        photosViewModel.photoModel.observe(viewLifecycleOwner) { photo ->
            putMarker(photo, Point(photo.lat, photo.lng))
            moveCamera(Point(photo.lat, photo.lng))
        }
        photosViewModel.getPhotoModel(args.photoId)
    }

    private fun checkCurrentFragment() {
        activity?.let {
            (it as MainActivity).checkCurrentFragment()
        }
    }

    private fun putMarkers(photos: List<PhotoModel>) {
        photos.forEach { photo ->
            putMarker(photo, Point(photo.lat, photo.lng))
        }
        moveCamera(Point(photos[0].lat, photos[0].lng))
    }

    private fun putMarker(photo: PhotoModel, point: Point) {
        val markerSize =
            requireContext().resources.getDimensionPixelSize(R.dimen.map_marker_icon_size)
        Glide.with(requireContext()).asBitmap()
            .load(R.drawable.marker)
            .into(object : CustomTarget<Bitmap>(markerSize, markerSize) {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    val markerClickCallback =
                        MapObjectTapListener { _, _ ->
                            val bottomSheetFragment = PhotoInfoDialogFragment.newInstance(
                                photo.url,
                                photo.name,
                                photo.dateTime,
                                photo.lat.toString(),
                                photo.lng.toString()
                            )
                            bottomSheetFragment.show(
                                childFragmentManager,
                                bottomSheetFragment.tag
                            )
                            true
                        }
                    markerClickCallbacks.add(markerClickCallback)

                    mapView.map.mapObjects.addPlacemark(
                        point,
                        ImageProvider.fromBitmap(resource),
                        IconStyle()
                    ).addTapListener(markerClickCallback)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }

    private fun moveCamera(point: Point) {
        mapView.map.move(
            CameraPosition(point, 11.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0F),
            null
        )
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        markerClickCallbacks.clear()
        super.onStop()
    }
}