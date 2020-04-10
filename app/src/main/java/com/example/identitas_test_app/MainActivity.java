package com.example.identitas_test_app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements Camera.PictureCallback, SurfaceHolder.Callback {

    private final String IMAGE_CAPTURED = "image_captured";
    private final String INSTRUCTION_SEEN = "instruction_seen";

    private SurfaceView mCameraPreview;
    private Camera mCamera;
    private byte[] mCameraCaptureData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCameraPreview  = findViewById(R.id.camera_preview);
        final SurfaceHolder surfaceHolder = mCameraPreview.getHolder();
        surfaceHolder.addCallback(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        // save the information whether an image has successfully been captured or not so that
        // when the user comes back, we can resume to the state of the app
        // savedInstanceState.putBoolean(IMAGE_CAPTURED, mCameraCaptureData == null);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // make sure if user comes back to the app and image has been successfully captured
        // then they do not go back to the capturing
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mCamera == null) {
            try {
                mCamera = Camera.open();
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Unable to open camera.", Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (IOException e) {
                Toast.makeText(MainActivity.this, "Unable to start camera preview.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        mCameraCaptureData = data;

        Bitmap bitmap = BitmapFactory.decodeByteArray(mCameraCaptureData, 0, mCameraCaptureData.length);
        // process image bitmap here
    }

    private void captureImage() {
        mCamera.takePicture(null, null, this);
    }
}
