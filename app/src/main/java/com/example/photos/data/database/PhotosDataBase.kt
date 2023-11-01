package com.example.photos.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.photos.data.database.dao.PhotosDao
import com.example.photos.data.database.entities.PhotoEntity

@Database(
    entities = [PhotoEntity::class],
    version = 1
)
abstract class PhotosDataBase: RoomDatabase() {
    abstract fun getPhotosDao(): PhotosDao
}