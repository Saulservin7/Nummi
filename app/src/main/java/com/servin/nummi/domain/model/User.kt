package com.servin.nummi.domain.model

data class User(
    val uid: String,
    val name: String?,
    val email: String?,
    val isAnonymous: Boolean,
)
