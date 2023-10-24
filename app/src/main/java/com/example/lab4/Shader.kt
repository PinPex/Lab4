package com.example.lab4

import android.opengl.GLES20

import java.nio.FloatBuffer


class Shader(vertexShaderCode: String, fragmentShaderCode: String) {
    private var program_Handle = 0

    init {
        createProgram(vertexShaderCode, fragmentShaderCode)
    }

    private fun createProgram(vertexShaderCode: String, fragmentShaderCode: String) {
        val vertexShader_Handle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER)
        GLES20.glShaderSource(vertexShader_Handle, vertexShaderCode)
        GLES20.glCompileShader(vertexShader_Handle)
        val fragmentShader_Handle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER)
        GLES20.glShaderSource(fragmentShader_Handle, fragmentShaderCode)
        GLES20.glCompileShader(fragmentShader_Handle)
        program_Handle = GLES20.glCreateProgram()
        GLES20.glAttachShader(program_Handle, vertexShader_Handle)
        GLES20.glAttachShader(program_Handle, fragmentShader_Handle)
        GLES20.glLinkProgram(program_Handle)
    }

    fun linkVertexBuffer(vertexBuffer: FloatBuffer?) {
        GLES20.glUseProgram(program_Handle)
        val a_vertex_Handle = GLES20.glGetAttribLocation(program_Handle, "a_vertex")
        GLES20.glEnableVertexAttribArray(a_vertex_Handle)
        GLES20.glVertexAttribPointer(
            a_vertex_Handle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer
        )
    }

    fun linkNormalBuffer(normalBuffer: FloatBuffer?) {
        GLES20.glUseProgram(program_Handle)
        val a_normal_Handle = GLES20.glGetAttribLocation(program_Handle, "a_normal")
        GLES20.glEnableVertexAttribArray(a_normal_Handle)
        GLES20.glVertexAttribPointer(
            a_normal_Handle, 3, GLES20.GL_FLOAT, false, 0, normalBuffer
        )
    }

    fun linkColorBuffer(colorBuffer: FloatBuffer?) {
        GLES20.glUseProgram(program_Handle)
        val a_color_Handle = GLES20.glGetAttribLocation(program_Handle, "a_color")
        GLES20.glEnableVertexAttribArray(a_color_Handle)
        GLES20.glVertexAttribPointer(
            a_color_Handle, 4, GLES20.GL_FLOAT, false, 0, colorBuffer
        )
    }

    fun linkModelViewProjectionMatrix(modelViewProjectionMatrix: FloatArray?) {
        GLES20.glUseProgram(program_Handle)
        val u_modelViewProjectionMatrix_Handle =
            GLES20.glGetUniformLocation(program_Handle, "u_modelViewProjectionMatrix")
        GLES20.glUniformMatrix4fv(
            u_modelViewProjectionMatrix_Handle, 1, false, modelViewProjectionMatrix, 0
        )
    }

    fun linkCamera(xCamera: Float, yCamera: Float, zCamera: Float) {
        GLES20.glUseProgram(program_Handle)
        val u_camera_Handle = GLES20.glGetUniformLocation(program_Handle, "u_camera")
        GLES20.glUniform3f(u_camera_Handle, xCamera, yCamera, zCamera)
    }

    fun linkLightSource(xLightPosition: Float, yLightPosition: Float, zLightPosition: Float) {
        GLES20.glUseProgram(program_Handle)
        val u_lightPosition_Handle = GLES20.glGetUniformLocation(program_Handle, "u_lightPosition")
        GLES20.glUniform3f(u_lightPosition_Handle, xLightPosition, yLightPosition, zLightPosition)
    }

    fun useProgram() {
        GLES20.glUseProgram(program_Handle)
    }
}