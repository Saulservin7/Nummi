package com.servin.nummi.presentation.navigation

sealed class AppScreen(val route:String){
    object LoginScreen : AppScreen("login_screen")
    object RegisterScreen : AppScreen("register_screen")
    object HomeScreen : AppScreen("home_screen")
}