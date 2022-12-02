package com.demo.gotranslate.ui.translator.camera

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.demo.gotranslate.R
import com.demo.gotranslate.app.ActivityCallback
import com.demo.gotranslate.app.logGo
import com.demo.gotranslate.app.showToast
import com.demo.gotranslate.base.BaseUI
import com.demo.gotranslate.manager.LanguageManager
import com.demo.gotranslate.manager.PhotoTranslateManager
import com.demo.gotranslate.ui.translator.LanguageUI
import com.demo.gotranslate.util.BitmapUtils
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.activity_camera.iv_back
import kotlinx.android.synthetic.main.activity_camera.iv_change
import kotlinx.android.synthetic.main.activity_camera.view
import kotlinx.android.synthetic.main.activity_text_translator.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class CameraUI:BaseUI(R.layout.activity_camera) {
    private var takeing=false
    private var imageCapture: ImageCapture? = null

    override fun view() {
        immersionBar.statusBarView(view).init()
        setLanguageInfo()

        lifecycleScope.launch(Dispatchers.IO) {
            while (pv_camera.width == 0) delay(20)
            initCamera()
        }

        setListener()
    }

    private fun setListener(){
        iv_back.setOnClickListener { finish() }
        llc_take.setOnClickListener {
            if (!takeing){
                takePhoto()
            }
        }
        iv_change.setOnClickListener {
            LanguageManager.changeLanguage()
            setLanguageInfo()
        }
        llc_left.setOnClickListener { toChooseLanguage(true) }
        llc_right.setOnClickListener { toChooseLanguage(false) }
        llc_choose.setOnClickListener { choosePhoto() }
    }

    private fun choosePhoto(){
        var intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (packageManager.resolveActivity(intent, 0) == null) {
            intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
        }
        ActivityCallback.choosePic=true
        startActivityForResult(intent,1000)
    }

    private fun toChooseLanguage(top:Boolean){
        val intent = Intent(this, LanguageUI::class.java).apply {
            putExtra("top",top)
        }
        startActivityForResult(intent,100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==1000){
            GlobalScope.launch {
                delay(300L)
                ActivityCallback.choosePic=false
            }
            if(null!=data?.data){
                toResultUI(data.data)
            }
            return
        }
        when(resultCode){
            100->{
                setLanguageInfo()
            }
        }
    }


    private fun setLanguageInfo(){
        tv_left_name.text=LanguageManager.topLanguage.name
        tv_right_name.text=LanguageManager.bottomLanguage.name
    }

    private fun takePhoto(){
        val imageCapture = imageCapture ?: return
        takeing=true
        val photoFile = File(cacheDir, "go_translate.jpg")
        val metadata = ImageCapture.Metadata().apply {
            isReversedHorizontal = false
        }
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
            .setMetadata(metadata)
            .build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    takeing=false
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    takeing=false
                    toResultUI(Uri.fromFile(photoFile))
                }
            })
    }

    private fun toResultUI(uri: Uri?){
        if(null==uri) return
        try {
            val bitmapFromContentUri = BitmapUtils.getBitmapFromContentUri(contentResolver, uri)
            scaleBitmap(bitmapFromContentUri)
            PhotoTranslateManager.photoBitmap=bitmapFromContentUri
            startActivity(Intent(this@CameraUI,TakeResultUI::class.java))
        } catch (e: Exception) {
            showToast("take photo fail")
        }
    }

    private fun scaleBitmap(origin: Bitmap?) {
        if (origin == null) return
        val width = origin.width
        val height = origin.height
        val scaleW = pv_camera.width * 1.0F / width
        val scaleH = pv_camera.height * 1.0F / height
        val matrix = Matrix()
        matrix.postScale(scaleW, scaleH)
        Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false)
    }

    private fun initCamera(){
        imageCapture = ImageCapture.Builder()
            .setTargetRotation(pv_camera.display.rotation)
            .setTargetResolution(Size(pv_camera.width, pv_camera.height))
            .build()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(pv_camera.surfaceProvider)
                }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                val camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
                camera.cameraControl
            } catch (exc: Exception) {
            }
        }, ContextCompat.getMainExecutor(this))
    }
}