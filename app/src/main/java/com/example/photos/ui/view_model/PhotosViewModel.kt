package com.example.photos.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photos.domain.PhotosRepository
import com.example.photos.domain.models.PhotoModel
import com.yandex.mapkit.geometry.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotosViewModel @Inject constructor(
    private val repository: PhotosRepository
) : ViewModel() {

    private var _photos = MutableLiveData<List<PhotoModel>>()
    val photos: LiveData<List<PhotoModel>> = _photos

    private var _errorGet = MutableLiveData<Boolean>()
    val errorGet: LiveData<Boolean> = _errorGet

    private var _errorSave = MutableLiveData<Boolean>()
    val errorSave: LiveData<Boolean> = _errorSave

    private var _errorDelete = MutableLiveData<Boolean>()
    val errorDelete: LiveData<Boolean> = _errorDelete

    private var _errorGetPhotoModel = MutableLiveData<Boolean>()
    val errorGetPhotoModel: LiveData<Boolean> = _errorGetPhotoModel

    private var _photoModel = MutableLiveData<PhotoModel>()
    val photoModel: LiveData<PhotoModel> = _photoModel

    private var _isMenuInitialized = MutableLiveData<Boolean>()
    val isMenuInitialized: LiveData<Boolean> = _isMenuInitialized

    var base64Img: String? = null
    var location: Point? = null
    var photoName: String? = null
    var photoId: Int? = null

    private val getPhotosHandler = CoroutineExceptionHandler { _, _ ->
        viewModelScope.launch {
            _errorGet.value = true
        }
    }

    private val savePhotoHandler = CoroutineExceptionHandler { _, _ ->
        viewModelScope.launch {
            _errorSave.value = true
        }
    }

    private val deletePhotoHandler = CoroutineExceptionHandler { _, _ ->
        viewModelScope.launch {
            _errorDelete.value = true
        }
    }

    private val getPhotoModelHandler = CoroutineExceptionHandler { _, _ ->
        viewModelScope.launch {
            _errorGetPhotoModel.value = true
        }
    }

    fun getPhotos() {
        viewModelScope.launch(getPhotosHandler) {
            _photos.value = repository.getPhotosFromDataBase()
            _errorGet.value = false
        }
    }

    fun getPhotosByDate(date: String) {
        viewModelScope.launch(getPhotosHandler) {
            _photos.value = repository.getPhotosByDate(date)
            _errorGet.value = false
        }
    }

    fun savePhotoToDataBase(photo: PhotoModel) {
        viewModelScope.launch(savePhotoHandler) {
            repository.savePhotoToDataBase(photo)
            _errorSave.value = false
        }
    }

    fun deletePhotoFromDataBase(id: Int) {
        viewModelScope.launch(deletePhotoHandler) {
            repository.deletePhotoFromDataBase(id)
            _errorDelete.value = false
        }
    }

    fun getPhotoModel(id: Int) {
        viewModelScope.launch(getPhotoModelHandler) {
            _photoModel.value = repository.getPhotoModel(id)
            _errorGetPhotoModel.value = false
        }
    }

    fun setMenuInitialized(isInitialized: Boolean) {
        _isMenuInitialized.value = isInitialized
    }
}