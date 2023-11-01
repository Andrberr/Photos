package com.example.photos.ui.photos

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.util.Base64
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.photos.R
import com.example.photos.databinding.FragmentPhotosBinding
import com.example.photos.databinding.InputNameDialogLayoutBinding
import com.example.photos.domain.models.PhotoModel
import com.example.photos.ui.view_model.PhotosViewModel
import com.example.photos.ui.base.BaseFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.pixelcarrot.base64image.Base64Image
import com.yandex.mapkit.geometry.Point
import dagger.hilt.android.AndroidEntryPoint
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class PhotosFragment : BaseFragment<FragmentPhotosBinding>() {

    private val photosAdapter = PhotosAdapter()
    private lateinit var photosLayoutManager: GridLayoutManager

    val photosViewModel by activityViewModels<PhotosViewModel>()

    private val locationRequest = createLocationRequest()

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireContext())
    }

    private val locationCallback: LocationCallback by lazy {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    photosViewModel.location = Point(location.latitude, location.longitude)
                    showInputNameDialog()
                    stopLocationUpdates()
                    break
                }
            }
        }
    }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(
                Manifest.permission.ACCESS_FINE_LOCATION,
                false
            ) -> {
                startLocationUpdates()
            }

            permissions.getOrDefault(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                false
            ) -> {
                startLocationUpdates()
            }

            else -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.no_location_permission),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private var selectedFolderUri: String? = null

    private val savePhotoToFolder =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    selectedFolderUri = result.data?.dataString
                    photosViewModel.location?.let { savePhoto(it) }
                }
            }
        }

    private val takePhoto =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val bitmap = result.data?.extras?.get(DATA) as Bitmap

                Base64Image.encode(bitmap) { base64 ->
                    base64?.let { base64Img ->
                        photosViewModel.base64Img = base64Img
                        launchLocationPermission()
                    }
                }
            }
        }

    override fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPhotosBinding {
        return FragmentPhotosBinding.inflate(inflater, container, false)
    }

    override fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews()
        initObservers()
        binding.progressBar.isVisible = true
        photosViewModel.getPhotos()
    }

    private fun initViews() {
        photosAdapter.itemClick = { photoId ->
            photosViewModel.photoId = photoId
            navigateToPhoto()
        }
        photosAdapter.itemDelete = { id ->
            binding.progressBar.isVisible = true
            photosViewModel.deletePhotoFromDataBase(id)
            photosViewModel.getPhotos()
        }
        photosLayoutManager = GridLayoutManager(context, COLUMNS)
        binding.photosRecycler.apply {
            adapter = photosAdapter
            layoutManager = photosLayoutManager
        }
        binding.addButton.setOnClickListener {
            takePhoto()
        }
        initSearchBarView()
    }

    private fun initSearchBarView() {
        binding.searchBar.onSearchListener = { query ->
            binding.progressBar.isVisible = true
            photosViewModel.getPhotosByDate(query)
        }

        binding.searchBar.onClearListener = {
            binding.progressBar.isVisible = true
            photosViewModel.getPhotos()
        }
    }

    private fun initObservers() {
        photosViewModel.photos.observe(viewLifecycleOwner) { photos ->
            binding.progressBar.isVisible = false
            binding.progress.isVisible = false
            photosAdapter.submitList(photos)
        }
        photosViewModel.errorGet.observe(viewLifecycleOwner) { isError ->
            handleError(isError, getString(R.string.get_error))
        }
        photosViewModel.errorSave.observe(viewLifecycleOwner) { isError ->
            handleError(isError, getString(R.string.save_error))
        }
        photosViewModel.errorDelete.observe(viewLifecycleOwner) { isError ->
            handleError(isError, getString(R.string.delete_error))
        }
        photosViewModel.errorGetPhotoModel.observe(viewLifecycleOwner) { isError ->
            handleError(isError, getString(R.string.get_error))
        }
    }

    private fun handleError(isError: Boolean, text: String) {
        binding.progressBar.isVisible = false
        if (isError) {
            showError(text)
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun takePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            takePhoto.launch(intent)
        }
    }

    private fun openDocumentTree() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        savePhotoToFolder.launch(intent)
    }

    private fun savePhoto(location: Point) {
        if (selectedFolderUri != null) {
            val base64Img = photosViewModel.base64Img
            if (base64Img != null) {
                val documentUri = Uri.parse(selectedFolderUri)
                val documentFile = DocumentFile.fromTreeUri(requireContext(), documentUri)

                if (documentFile != null && documentFile.exists()) {
                    val dateTime = getDate()
                    val imageFileName = photosViewModel.photoName
                    if (imageFileName != null) {
                        val imageFile = documentFile.createFile(MIME_TYPE, imageFileName)

                        try {
                            val outputStream: OutputStream? =
                                imageFile?.let {
                                    requireContext().contentResolver.openOutputStream(
                                        it.uri
                                    )
                                }
                            val imageBytes = Base64.decode(base64Img, Base64.DEFAULT)
                            outputStream?.write(imageBytes)
                            outputStream?.close()
                            photosViewModel.savePhotoToDataBase(
                                PhotoModel(
                                    DEFAULT_ID,
                                    imageFileName,
                                    base64Img,
                                    dateTime,
                                    location.latitude,
                                    location.longitude
                                )
                            )
                            photosViewModel.getPhotos()
                        } catch (e: Exception) {
                            showError(getString(R.string.save_to_folder_error))
                        }
                    }
                } else {
                    showError(getString(R.string.no_folder))
                }
            }
        }
    }

    private fun showInputNameDialog() {
        val builder = AlertDialog.Builder(requireContext())

        val dialogLayout = InputNameDialogLayoutBinding.inflate(layoutInflater, null, false)
        builder.setView(dialogLayout.root)
        val alertDialog = builder.create()

        alertDialog.window?.apply {
            val layoutParams = attributes
            layoutParams.gravity = Gravity.CENTER
            attributes = layoutParams
        }

        dialogLayout.addButton.setOnClickListener {
            val name = dialogLayout.titleInput.text.toString()
            if (name.isNotEmpty()) {
                photosViewModel.photoName = name
                alertDialog.dismiss()
                openDocumentTree()
            }
        }
        alertDialog.show()
    }

    private fun getDate(): String {
        val sdf = SimpleDateFormat(DATE_FORMAT, Locale(RU))
        return sdf.format(Calendar.getInstance().time)
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(
            requireContext(),
            errorMessage,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun launchLocationPermission() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun createLocationRequest(): LocationRequest {
        return LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, TIME_INTERVAL
        ).build()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun navigateToPhoto() {
        val action = PhotosFragmentDirections.actionPhotosFragmentToWatchPhotoFragment()
        findNavController().navigate(action)
    }

    companion object {
        private const val COLUMNS = 3
        private const val DATA = "data"
        private const val TIME_INTERVAL = 20000L
        private const val DEFAULT_ID = 0
        private const val RU = "ru"
        private const val MIME_TYPE = "image/jpeg"
        private const val DATE_FORMAT = "dd.MM.yyyy HH:mm:ss"
    }
}