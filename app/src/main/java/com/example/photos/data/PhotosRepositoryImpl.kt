package com.example.photos.data

import com.example.photos.data.mappers.PhotoMapper
import com.example.photos.data.sources.DataBaseSource
import com.example.photos.domain.PhotosRepository
import com.example.photos.domain.models.PhotoModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PhotosRepositoryImpl @Inject constructor(
    private val photoMapper: PhotoMapper,
    private val dataBaseSource: DataBaseSource
) : PhotosRepository {

    override suspend fun savePhotoToDataBase(photo: PhotoModel) {
        runBlocking(Dispatchers.IO) {
            dataBaseSource.savePhoto(photoMapper.mapToEntity(photo))
        }
    }

    override suspend fun getPhotosFromDataBase(): List<PhotoModel> =
        withContext(Dispatchers.IO) {
            dataBaseSource.getPhotos().map { photoMapper.mapToModel(it) }
        }

    override suspend fun getPhotosByDate(date: String): List<PhotoModel> =
        withContext(Dispatchers.IO) {
            dataBaseSource.getPhotosByDate(date).map { photoMapper.mapToModel(it) }
        }

    override suspend fun deletePhotoFromDataBase(id: Int) {
        runBlocking(Dispatchers.IO) {
            dataBaseSource.deletePhoto(id)
        }
    }

    override suspend fun getPhotoModel(id: Int): PhotoModel =
        withContext(Dispatchers.IO) {
            photoMapper.mapToModel(dataBaseSource.getPhoto(id))
        }
}