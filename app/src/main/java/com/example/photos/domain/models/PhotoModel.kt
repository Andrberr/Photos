package com.example.photos.domain.models

data class PhotoModel(
    val id: Int,
    val name: String,
    val url: String,
    val dateTime: String,
    val lat: Double,
    val lng: Double
)