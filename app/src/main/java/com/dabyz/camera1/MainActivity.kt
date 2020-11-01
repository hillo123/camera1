package com.dabyz.camera1

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.hardware.Camera
import android.os.Bundle
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

        var size = params.getSupportedPictureSizes().filter { it.width > 300 && it.height > 300 }.last()
        params.setPictureSize(size.width, size.height)

        var or = params.get("orientation")
        //var o2 = camera.getDisplayOrientation()
        params.set("orientation", "portrait")
        params.setRotation(90)
        camera.parameters = params
        camera.setDisplayOrientation(90)

        camera_preview.addView(CamPreView(this, size.height,size.width, camera))

        button.setOnClickListener {
            camera.takePicture(null, null) { data, _ ->
                val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                captured_image.setImageBitmap(bitmap)
            }
        }
    }

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