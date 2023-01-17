package com.example.excellentreader;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.example.excellentreader.algo.Algo;
import com.google.common.util.concurrent.ListenableFuture;

public class MainActivity extends AppCompatActivity {

    static final int PERM_REQ_CODE = 10;
    static final String[] req_permissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    ImageCapture imageCapture;

    private boolean permissionGranted() {
        for (String s : req_permissions) {
            if (ContextCompat.checkSelfPermission(getBaseContext(), s) == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }

        return true;
    }

    Button conv_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (!permissionGranted()) {
            ActivityCompat.requestPermissions(this, req_permissions, PERM_REQ_CODE);
        }
        else {
            startPreview();
        }

        conv_button = (Button) findViewById(R.id.button);
        conv_button.setOnClickListener(view -> onTakePhoto());
    }

    private void startPreview() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            ProcessCameraProvider cameraProvider = null;
            try {
                cameraProvider = cameraProviderFuture.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(cameraProvider == null) {
                return;
            }

            // Preview
            Preview preview = new Preview.Builder().build();
            PreviewView pv = (PreviewView) findViewById(R.id.viewFinder);
            preview.setSurfaceProvider(pv.getSurfaceProvider());

            // Select back camera as a default
            CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll();

                // Bind use cases to camera
                imageCapture = new ImageCapture.Builder().build();
                cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, imageCapture, preview);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @Override
    public void onRequestPermissionsResult(int rq_id, @NonNull String[] perms, @NonNull int[] results) {
        if (rq_id != PERM_REQ_CODE) {
            super.onRequestPermissionsResult(rq_id, perms, results);
        }

        if (!permissionGranted()) {
            Toast.makeText(this, "Permissions denied, too bad.", Toast.LENGTH_SHORT).show();
            return;
        }

        startPreview();
    }

    void onTakePhoto() {
        System.out.println("Pressed button");

        imageCapture.takePicture(ContextCompat.getMainExecutor(this), new ImageCapture.OnImageCapturedCallback() {
            @ExperimentalGetImage
            public void onCaptureSuccess(@NonNull ImageProxy imageProxy) {
                Image img = imageProxy.getImage();
                if(img == null) {
                    Toast.makeText(MainActivity.this, "Failed to capture the picture", Toast.LENGTH_SHORT).show();
                    return;
                }
                Algo algo = new Algo(img.getWidth(), img.getHeight());

                byte[] lum = new byte[img.getPlanes()[0].getBuffer().remaining()];
                img.getPlanes()[0].getBuffer().get(lum);

                for(int y = 0; y < img.getHeight(); ++y) {
                    for (int x = 0; x < img.getWidth(); ++x) {

                    }
                }

            }
            public void onError(@NonNull ImageCaptureException e) {
                Toast.makeText(MainActivity.this, "Failed to capture the picture", Toast.LENGTH_SHORT).show();
            }
        });
    }
}