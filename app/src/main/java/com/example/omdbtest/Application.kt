package com.example.omdbtest

import android.app.Application
import com.example.omdbtest.ui.FontsOverride




class Application: Application() {
    override fun onCreate() {
        super.onCreate()
        FontsOverride.changeDefaultFont(this)
    }
}