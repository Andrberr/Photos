package com.example.photos.data.sources

import com.example.photos.data.database.dao.PhotosDao
import com.example.photos.data.database.entities.PhotoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DataBaseSource @Inject constructor(
    private val photosDao: PhotosDao
) {
    suspend fun getPhotos() = withContext(Dispatchers.IO) { photosDao.getAll() }

    suspend fun getPhotosByDate(date: String) =
        withContext(Dispatchers.IO) { photosDao.getPhotosByDate(date) }

    fun savePhoto(photo: PhotoEntity) =
        runBlocking(Dispatchers.IO) { photosDao.insertPhoto(photo) }

    fun deletePhoto(id: Int) = runBlocking(Dispatchers.IO) { photosDao.deletePhoto(id) }

    suspend fun getPhoto(id: Int) = withContext(Dispatchers.IO) { photosDao.getPhotoById(id) }
}