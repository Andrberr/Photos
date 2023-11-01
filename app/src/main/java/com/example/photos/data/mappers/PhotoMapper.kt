package com.example.photos.data.mappers

import com.example.photos.data.database.entities.PhotoEntity
import com.example.photos.domain.models.PhotoModel
import javax.inject.Inject

class PhotoMapper @Inject constructor() {
    fun mapToEntity(unmapped: PhotoModel) =
        with(unmapped) {
            PhotoEntity(
                id = id,
                name = name,
                url = url,
                date = dateTime.split(" ")[0],
                time = dateTime.split(" ")[1],
                lat = lat,
                lng = lng
            )
        }

    fun mapToModel(unmapped: PhotoEntity) =
        with(unmapped) {
            PhotoModel(
                id = id,
                name = name,
                url = url,
                dateTime = "$date $time",
                lat = lat,
                lng = lng
            )
        }
}