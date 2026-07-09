package com.example.spacexhero.data.model

import com.example.spacexhero.domain.model.LaunchDetails
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiLaunchDetails(
    val id: String,
    val name: String,
    @SerialName("vidURLs")
    val videoUrls: List<ApiVideoUrl>? = emptyList()
){
    fun toDomain(): LaunchDetails = LaunchDetails(
        id = id,
        name = name,
        videoUrl = videoUrls?.firstOrNull()?.url ?: ""
    )
}


@Serializable
data class ApiVideoUrl(
    val url: String?
)

