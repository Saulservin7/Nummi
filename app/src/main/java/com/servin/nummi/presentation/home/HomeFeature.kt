package com.servin.nummi.presentation.home

import androidx.annotation.DrawableRes

data class HomeFeature(
    val title: String,
    @DrawableRes val iconRes: Int,
    val route: String
)