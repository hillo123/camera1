package com.dabyz.camera1

import android.content.Context
import android.hardware.Camera
import android.view.SurfaceHolder
import android.view.SurfaceView

class CamPreView(ctx: Context?) : SurfaceView(ctx) {
    val surfaceHolder: SurfaceHolder? = null

    constructor(ctx: Context?, w: Int, h: Int, camera: Camera) : this(ctx) {
        //holder.setFixedSize(w,h)
        holder.setFixedSize(w*4,h*4) // 960x1280
        //holder.setSizeFromLayout()
        holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                camera.setPreviewDisplay(holder); camera.startPreview()
            }
            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
            override fun surfaceDestroyed(holder: SurfaceHolder) {
                camera.stopPreview(); camera.release()
            }
        })
    }
}
