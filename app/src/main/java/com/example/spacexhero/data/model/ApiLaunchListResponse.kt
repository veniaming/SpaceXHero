package com.example.spacexhero.data.model

import com.example.spacexhero.domain.model.Launch
import kotlinx.serialization.Serializable

@Serializable
data class ApiLaunchListResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<ApiLaunchItem>
)

@Serializable
data class ApiLaunchItem(
    val id: String,
    val name: String,
    val image: String?,
    val net: String?
) {
    fun toDomain(): Launch = Launch(
        id = id,
        name = name,
        net = net ?: "",
        imageUrl = image ?: ""
    )
}

