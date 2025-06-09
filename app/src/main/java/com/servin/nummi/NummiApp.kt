package com.servin.nummi


import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp // <<--- Esta es la anotación para la clase Application
class NummiApp : Application()