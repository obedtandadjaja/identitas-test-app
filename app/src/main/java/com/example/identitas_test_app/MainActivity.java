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
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements Camera.PictureCallback, SurfaceHolder.Callback {

    private final String IMAGE_CAPTURED = "image_captured";
    private final String INSTRUCTION_SEEN = "instruction_seen";
    private final int CAPTURE_INTERVAL_PERIOD = 2000;
    private final int MAX_CAPTURE_COUNT = 15;

    private SurfaceView mCameraPreview;
    private Camera mCamera;
    private byte[] mCameraCaptureData;
    private Timer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCameraPreview  = findViewById(R.id.camera_preview);
        final SurfaceHolder surfaceHolder = mCameraPreview.getHolder();
        surfaceHolder.addCallback(this);

        setupTimer();
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

        if (mTimer == null) {
            setupTimer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
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

    private void setupTimer() {
        mTimer = new Timer();
        // set to loop every x seconds and capture images
        for(int i = 1; i <= MAX_CAPTURE_COUNT; i++) {
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    captureImage();
                }
            }, 0, CAPTURE_INTERVAL_PERIOD * i);
        }
    }
}
