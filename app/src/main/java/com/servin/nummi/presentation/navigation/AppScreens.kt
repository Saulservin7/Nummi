package com.servin.nummi.presentation.navigation

sealed class AppScreens(val route:String){
    object SplashScreen: AppScreens("splash_screen")
    object LoginScreen: AppScreens("login_screen")
    object RegisterScreen: AppScreens("register_screen")
    object HomeScreen: AppScreens("home_screen")
    object AddTransactionScreen: AppScreens("add_transaction_screen")
}