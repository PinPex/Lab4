package com.example.lab4

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


class MyRenderer(private val context: Context) : GLSurfaceView.Renderer {
    private val xCamera: Float
    private val yCamera: Float
    private val zCamera: Float
    private val xLightPosition = 0f
    private val yLightPosition = 0.5f
    private val zLightPosition = 0f
    private val modelMatrix: FloatArray
    private val viewMatrix: FloatArray
    private val modelViewMatrix: FloatArray
    private val projectionMatrix: FloatArray
    private val modelViewProjectionMatrix: FloatArray
    private val vertexBuffer: FloatBuffer
    private val normalBuffer: FloatBuffer
    private val colorBuffer: FloatBuffer
    private var mShader: Shader? = null

    init {
        modelMatrix = FloatArray(16)
        viewMatrix = FloatArray(16)
        modelViewMatrix = FloatArray(16)
        projectionMatrix = FloatArray(16)
        modelViewProjectionMatrix = FloatArray(16)
        Matrix.setIdentityM(modelMatrix, 0)
        xCamera = 0f
        yCamera = 8f
        zCamera = 0.01f
        Matrix.setLookAtM(
            viewMatrix, 0, xCamera, yCamera, zCamera, 0f, 0f, 0f, 0f, 2f, 0f
        )
        Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        val x1 = -2f
        val y1 = 0f
        val z1 = -2f
        val x2 = -2f
        val y2 = 0f
        val z2 = 2f
        val x3 = 2f
        val y3 = 0f
        val z3 = -2f
        val x4 = 2f
        val y4 = 0f
        val z4 = 2f
        val vertexArray = floatArrayOf(x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4)
        val bvertex = ByteBuffer.allocateDirect(vertexArray.size * 4)
        bvertex.order(ByteOrder.nativeOrder())
        vertexBuffer = bvertex.asFloatBuffer()
        vertexBuffer.position(0)
        vertexBuffer.put(vertexArray)
        vertexBuffer.position(0)
        val nx = 0f
        val ny = 1f
        val nz = 0f
        val normalArray = floatArrayOf(nx, ny, nz, nx, ny, nz, nx, ny, nz, nx, ny, nz)
        val bnormal = ByteBuffer.allocateDirect(normalArray.size * 4)
        bnormal.order(ByteOrder.nativeOrder())
        normalBuffer = bnormal.asFloatBuffer()
        normalBuffer.position(0)
        normalBuffer.put(normalArray)
        normalBuffer.position(0)
        val colorArray = floatArrayOf(
            0f, 0f, 0f, 0f,
            0f, 0f, 0f, 0f,
            0f, 0f, 0f, 0f,
            0f, 0f, 0f, 0f
        )
        val bcolor = ByteBuffer.allocateDirect(colorArray.size * 4)
        bcolor.order(ByteOrder.nativeOrder())
        colorBuffer = bcolor.asFloatBuffer()
        colorBuffer.position(0)
        colorBuffer.put(colorArray)
        colorBuffer.position(0)
    }

    override fun onSurfaceCreated(gl: GL10?, p1: javax.microedition.khronos.egl.EGLConfig?) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glEnable(GLES20.GL_CULL_FACE)
        GLES20.glHint(GLES20.GL_GENERATE_MIPMAP_HINT, GLES20.GL_NICEST)
        val vertexShaderCode = "uniform mat4 u_modelViewProjectionMatrix;" +
                "attribute vec3 a_vertex;" +
                "attribute vec3 a_normal;" +
                "attribute vec4 a_color;" +
                "varying vec3 v_vertex;" +
                "varying vec3 v_normal;" +
                "varying vec4 v_color;" +
                "void main() {" +
                "v_vertex=a_vertex;" +
                "vec3 n_normal=normalize(a_normal);" +
                "v_normal=n_normal;" +
                "v_color=a_color;" +
                "gl_Position = u_modelViewProjectionMatrix * vec4(a_vertex,1.0);" +
                "}"
        val fragmentShaderCode = "precision mediump float;" +
                "uniform vec3 u_camera;" +
                "uniform vec3 u_lightPosition;" +
                "varying vec3 v_vertex;" +
                "varying vec3 v_normal;" +
                "varying vec4 v_color;" +
                "void main() {" +
                "vec3 n_normal = normalize(v_normal);" +
                "vec3 lightvector = normalize(u_lightPosition - v_vertex);" +
                "vec3 lookvector = normalize(u_camera - v_vertex);" +
                "float ambient = 0.4;" +
                "float k_diffuse = 0.8;" +
                "float k_specular = 0.6;" +
                "float diffuse = k_diffuse * max(dot(n_normal, lightvector), 0.0);" +
                "vec3 reflectvector = reflect(-lightvector, n_normal);" +
                "float specular = k_specular * pow(max(dot(lookvector, reflectvector), 0.0), 40.0);" +
                "vec4 one = vec4(1.0,1.0,1.0,1.0);" +
                "vec4 lightColor = (ambient + diffuse + specular) * one;" +
                "gl_FragColor = mix(lightColor, v_color, 0.5);" +
                "}"
        mShader = Shader(vertexShaderCode, fragmentShaderCode)
        mShader!!.linkVertexBuffer(vertexBuffer)
        mShader!!.linkNormalBuffer(normalBuffer)
        mShader!!.linkColorBuffer(colorBuffer)
    }


    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height
        val k = 0.055f
        val left = -k * ratio
        val right = k * ratio
        val bottom = -k
        val near = 0.25f
        val far = 9.0f
        Matrix.frustumM(projectionMatrix, 0, left, right, bottom, k, near, far)
        Matrix.multiplyMM(
            modelViewProjectionMatrix,
            0, projectionMatrix, 0, modelViewMatrix, 0
        )
    }

    override fun onDrawFrame(gl: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        mShader?.linkModelViewProjectionMatrix(modelViewProjectionMatrix)
        mShader?.linkCamera(xCamera, yCamera, zCamera)
        mShader?.linkLightSource(xLightPosition, yLightPosition, zLightPosition)
        mShader?.useProgram()
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
    }
}
