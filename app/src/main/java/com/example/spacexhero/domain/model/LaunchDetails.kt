package com.example.spacexhero.domain.model

import kotlinx.serialization.Serializable

@Serializable
class LaunchDetails(
    val id: String,
    val name: String,
    val videoUrl: String
)
