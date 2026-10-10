package com.example.mediverify.ui.scan

import android.Manifest
import android.content.pm.PackageManager
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.mediverify.ui.viewmodel.MediVerifyViewModel
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@Composable
fun ScannerScreen(navController: NavController, viewModel: MediVerifyViewModel) {
    val context = LocalContext.current
    var permissionGranted by remember { mutableStateOf(false) }
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> permissionGranted = granted }

    LaunchedEffect(Unit) {
        permissionGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        if (!permissionGranted) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (!permissionGranted) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Camera access required", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("MediVerify needs camera permission to scan medicine barcodes and QR codes.", color = Color(0xFF475569))
                Button(onClick = { requestPermissionLauncher.launch(Manifest.permission.CAMERA) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Retry camera permission")
                }
            }
        }
        return
    }

    CameraPreview(
        onBarcodeDetected = { rawValue ->
            viewModel.lookupFromBarcode(rawValue)
            navController.navigate("result")
        }
    )
}

@Composable
private fun CameraPreview(onBarcodeDetected: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    var lastScanned by remember { mutableStateOf("") }

    DisposableEffect(lifecycleOwner) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val scanner = BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder().setBarcodeFormats(
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_EAN_13,
                Barcode.FORMAT_EAN_8,
                Barcode.FORMAT_UPC_A,
                Barcode.FORMAT_UPC_E,
                Barcode.FORMAT_CODE_128,
                Barcode.FORMAT_CODE_39,
                Barcode.FORMAT_DATA_MATRIX
            ).build()
        )
        val executor = Executors.newSingleThreadExecutor()
        val analyzer = object : ImageAnalysis.Analyzer {
            override fun analyze(imageProxy: ImageProxy) {
                val mediaImage = imageProxy.image ?: run { imageProxy.close(); return }
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                scanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        val first = barcodes.firstOrNull()?.rawValue ?: return@addOnSuccessListener
                        if (first.isNotBlank() && first != lastScanned) {
                            lastScanned = first
                            onBarcodeDetected(first)
                        }
                    }
                    .addOnCompleteListener { imageProxy.close() }
            }
        }

        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(1280, 720))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also { it.setAnalyzer(executor, analyzer) }

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageAnalysis
            )
        }, ContextCompat.getMainExecutor(context))

        onDispose {
            val provider = cameraProviderFuture.get()
            provider.unbindAll()
            scanner.close()
            executor.shutdown()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.72f)
                .padding(16.dp)
                .background(Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 60.dp)
                    .background(Color(0x88FFFFFF), shape = RoundedCornerShape(20.dp))
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text("Align the medicine barcode with the frame.", color = Color.White, modifier = Modifier.padding(bottom = 8.dp))
        }
    }
}
