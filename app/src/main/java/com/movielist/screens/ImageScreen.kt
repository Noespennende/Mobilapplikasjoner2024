package com.movielist.screens

import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.movielist.ui.theme.DarkGray
import com.movielist.ui.theme.DarkGrayTransparent
import com.movielist.ui.theme.Gray
import com.movielist.ui.theme.Purple
import com.movielist.ui.theme.White
import com.movielist.ui.theme.bottomNavBarHeight
import com.movielist.ui.theme.fontFamily
import com.movielist.ui.theme.headerSize
import com.movielist.ui.theme.horizontalPadding
import com.movielist.ui.theme.topPhoneIconsAndNavBarBackgroundHeight
import com.movielist.ui.theme.weightBold
import java.io.ByteArrayInputStream
import java.io.InputStream

@Composable
fun CameraScreen (
    handleImageCapture: (image: Bitmap) -> Unit
) {

    //Implementasjon inspirert av: https://www.youtube.com/watch?v=pPVZambOuG8
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraController = remember { LifecycleCameraController(context) }
    var isFrontfacingCamera by remember { mutableStateOf(false) }

    val handleSwitchCamera = {
        isFrontfacingCamera = !isFrontfacingCamera
        val cameraSelector = if (isFrontfacingCamera){
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        cameraController.cameraSelector = cameraSelector
        cameraController.bindToLifecycle(lifecycleOwner)
    }


    Scaffold (
        modifier = Modifier
            .fillMaxSize()
            .padding(
                bottom = bottomNavBarHeight,
                top = topPhoneIconsAndNavBarBackgroundHeight
            )
        ,
        floatingActionButton = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth(.93f)

            ){
                //Photo button
                ExtendedFloatingActionButton(
                    containerColor = Purple,
                    content = {
                        Text(
                            text = if (isFrontfacingCamera){"Use back camera"} else {"Use front camera"},
                            fontSize = headerSize,
                            fontWeight = weightBold,
                            fontFamily = fontFamily,
                            color = DarkGray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                        )
                    },
                    onClick = {
                        handleSwitchCamera()
                    }
                )

                //Photo button
                ExtendedFloatingActionButton(
                    containerColor = Purple,
                    content = {
                        Text(
                            text = "Take a photo",
                            fontSize = headerSize,
                            fontWeight = weightBold,
                            fontFamily = fontFamily,
                            color = DarkGray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                        )
                    },
                    onClick = {
                        val mainExecutor = ContextCompat.getMainExecutor(context)
                        cameraController.takePicture(mainExecutor, object: ImageCapture.OnImageCapturedCallback() {
                            override fun onCaptureSuccess(image: ImageProxy) {

                                val buffer = image.planes[0].buffer
                                val byteArray = ByteArray(buffer.remaining())
                                buffer.get(byteArray)
                                val inputStream: InputStream = ByteArrayInputStream(byteArray)

                                val exif = ExifInterface(inputStream)

                                //Get device orientation when photo was taken
                                val degreesToRotate = when (
                                    exif.getAttributeInt(
                                        ExifInterface.TAG_ORIENTATION,
                                        ExifInterface.ORIENTATION_UNDEFINED
                                    ))
                                {
                                    ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                                    ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                                    ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                                    else -> 0f
                                }

                                //Rotate image based on camera angle when photo was taken
                                val rotatedBitmap = RotateBitmap(image.toBitmap(), degreesToRotate)

                                //Send image taken
                                handleImageCapture(rotatedBitmap)
                                image.close()
                            }
                        })
                    }
                )
            }

        }
    ){ padding: PaddingValues ->
        AndroidView(
            modifier = Modifier
                .fillMaxSize(),
            factory = { context ->
                PreviewView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                    setBackgroundColor(android.graphics.Color.BLACK)
                    scaleType = PreviewView.ScaleType.FIT_START
                }.also { previewView ->
                    if(isFrontfacingCamera){
                        CameraSelector.DEFAULT_FRONT_CAMERA
                    } else {
                        CameraSelector.DEFAULT_BACK_CAMERA
                        }

                    previewView.controller = cameraController
                    cameraController.bindToLifecycle(lifecycleOwner)

                }
            }
        )
    }

}

@Composable
fun NoPermissionScreen(
    handleRequestPermissionClick: () -> Unit,
    handleCancelClick: () -> Unit
){
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(
                color = DarkGrayTransparent,
                shape = RoundedCornerShape(5.dp)
            )

    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(15.dp),
            modifier = Modifier
                .fillMaxWidth(.7f)
                .background(
                    color = Gray,
                    shape = RoundedCornerShape(5.dp)
                )
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ){
            Text(
                text = "Please grant permission to use camera",
                fontSize = headerSize,
                fontWeight = weightBold,
                fontFamily = fontFamily,
                color = White,
                textAlign = TextAlign.Center,
                modifier = Modifier
            )
            //Grant permission
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(
                        color = Purple,
                        shape = RoundedCornerShape(5.dp)
                    )
                    .padding(vertical = 10.dp, horizontal = 10.dp)
                    .fillMaxWidth()
                    .clickable {
                        handleRequestPermissionClick()
                    }
            ) {
                Text(
                    text = "Grant permission",
                    fontSize = headerSize,
                    fontWeight = weightBold,
                    fontFamily = fontFamily,
                    color = DarkGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }

            //Take a picture
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(
                        color = Purple,
                        shape = RoundedCornerShape(5.dp)
                    )
                    .padding(vertical = 10.dp, horizontal = 10.dp)
                    .fillMaxWidth()
                    .clickable {
                        handleCancelClick()
                    }
            ) {
                Text(
                    text = "Cancel",
                    fontSize = headerSize,
                    fontWeight = weightBold,
                    fontFamily = fontFamily,
                    color = DarkGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
        }
    }
}



fun RotateBitmap(
    bitmap: Bitmap,
    rotationAngle: Float
): Bitmap {
    val matrix =  Matrix()
    matrix.postRotate(rotationAngle)

    return Bitmap.createBitmap(bitmap, 0,0, bitmap.width, bitmap.height, matrix, true)
}

