package com.example.lab4

import android.content.Context
import android.opengl.GLSurfaceView


class MySurfaceView(context: Context) : GLSurfaceView(context) {
    private val renderer: MyRenderer

    init {
        setEGLContextClientVersion(2)
        renderer = MyRenderer(context)
        setRenderer(renderer)
        renderMode = RENDERMODE_CONTINUOUSLY
    }
}