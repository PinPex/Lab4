package com.example.lab4

import android.os.Bundle
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    private var mGLSurfaceView: MySurfaceView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mGLSurfaceView = MySurfaceView(this)
        setContentView(mGLSurfaceView)
    }
}
