package com.example.photos.di

import com.example.photos.data.PhotosRepositoryImpl
import com.example.photos.domain.PhotosRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun getRepository(repository: PhotosRepositoryImpl): PhotosRepository
}