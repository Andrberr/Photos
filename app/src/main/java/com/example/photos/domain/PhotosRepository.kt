package com.example.photos.domain

import com.example.photos.domain.models.PhotoModel

interface PhotosRepository {
    suspend fun savePhotoToDataBase(photo: PhotoModel)
    suspend fun getPhotosFromDataBase(): List<PhotoModel>
    suspend fun getPhotosByDate(date: String): List<PhotoModel>
    suspend fun deletePhotoFromDataBase(id: Int)
    suspend fun getPhotoModel(id: Int): PhotoModel
}