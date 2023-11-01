package com.example.photos.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.photos.data.database.entities.PhotoEntity

@Dao
interface PhotosDao {
    @Query("SELECT * FROM photos_table")
    fun getAll(): List<PhotoEntity>

    @Query("SELECT * FROM photos_table WHERE date = :date")
    fun getPhotosByDate(date: String): List<PhotoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPhoto(photo: PhotoEntity)

    @Query("DELETE FROM photos_table WHERE id = :photoId")
    fun deletePhoto(photoId: Int)

    @Query("SELECT * FROM photos_table WHERE id = :photoId")
    fun getPhotoById(photoId: Int): PhotoEntity
}