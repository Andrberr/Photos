package com.example.photos.di

import android.content.Context
import androidx.room.Room
import com.example.photos.data.database.PhotosDataBase
import com.example.photos.data.database.dao.PhotosDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class DataBaseModule {
    @Provides
    fun provideDataBase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, PhotosDataBase::class.java, "photos").build()

    @Provides
    fun providePhotosDao(db: PhotosDataBase): PhotosDao = db.getPhotosDao()
}