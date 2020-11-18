package com.dabyz.camera1

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.Camera
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); setContentView(R.layout.activity_main)
        requestPermissions()
    }

    fun init() {
        var camera = Camera.open()
        var params = camera.parameters

        var size = params.getSupportedPictureSizes().filter { it.width > 300 && it.height > 300 }
            .minBy { it.width + it.height }!!
        Log.e(null, "${size.width}-${size.height}")
        params.setPictureSize(size.width, size.height)

        //var or = params.get("orientation")
        //var o2 = camera.getDisplayOrientation()
        //params.set("orientation", "portrait")
        //params.setRotation(90)
        camera.parameters = params
        camera.setDisplayOrientation(90)

        Log.e(null, "with = ${windowManager.defaultDisplay.width}")  //1080
        /*
            640-480 camera => 480-640  primeros 300 de x y primeros 300 de y
            1080 ancho screen
            1728 es 60% mas grande

            480 --- 100
            1080 * x =  => x = 2,25

            300 - 100
            480 - x = 480 * 100 / 300 = 160

            w = 1080 * 1.6
            h = 640 * 1080 / 480 * 1,6 = 2304
            ws * 160/100
         */
        var cf = 1080 / 480

        fun w(w: Int): Int = windowManager.defaultDisplay.width * w / 300
        fun h(h: Int, w: Int): Int = h * windowManager.defaultDisplay.width / w //* w * 100 / 300

        var o = camera_preview


        //o.holder.setFixedSize(1728,1080) //640-480
        //o.holder.setFixedSize(400,1080)
        o.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                camera.setPreviewDisplay(holder);
                camera.startPreview()
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
            override fun surfaceDestroyed(holder: SurfaceHolder) {
                camera.stopPreview(); camera.release()
            }
        })

        //camera_preview.addView(CamPreView(this, size.height,size.width, camera))


        button.setOnClickListener {
            camera.takePicture(null, null) { data, _ ->
                val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                var bmp = Bitmap.createBitmap(bitmap, 0, 180, 300, 300)

                bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, Matrix().apply { postRotate(90F) }, true)

                camera_preview.visibility = View.GONE
                button.visibility = View.GONE
                captured_image.visibility = View.VISIBLE
                captured_image.setImageBitmap(bmp)

            }
        }
    }

    fun rotate(bmp: Bitmap) =
        Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, Matrix().apply { postRotate(90F) }, true)


    private fun requestPermissions() {
        var permissions = listOf(Manifest.permission.CAMERA).filterNot { ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }
            .toTypedArray()
        if (permissions.isNotEmpty()) ActivityCompat.requestPermissions(this, permissions, 0) else init()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0 && grantResults.filterNot { it == PackageManager.PERMISSION_GRANTED }.isNotEmpty()) exitProcess(0) else init()
    }
}