package com.example.spacexhero.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Launch(
    val id: String,
    val name: String,
    val net: String,
    val imageUrl: String,
)
