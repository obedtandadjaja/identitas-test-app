package com.example.identitas_test_app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements Camera.PictureCallback, SurfaceHolder.Callback {

    private final String IMAGE_CAPTURED = "image_captured";
    private final String INSTRUCTION_SEEN = "instruction_seen";
    private final int CAPTURE_INTERVAL_PERIOD = 2000;
    private final int MAX_CAPTURE_COUNT = 15;
    private final int DEFAULT_OVERLAY_MARGIN = 40;
    private final double CARD_HEIGHT_WIDTH_RATIO = 1.5;

    private SurfaceView mCameraPreview;
    private Camera mCamera;
    private byte[] mCameraCaptureData;
    private Timer mTimer;
    private CameraManager mCameraManager;
    private RelativeLayout mParentLayout;
    private ImageView mOverlayTop, mOverlayBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mParentLayout = findViewById(R.id.parent);
        mOverlayTop = findViewById(R.id.box_overlay_top);
        mOverlayBottom = findViewById(R.id.box_overlay_bottom);

        // set the overlay top and bottom according to the card size
        int parentWidth = mParentLayout.getWidth() - DEFAULT_OVERLAY_MARGIN * 2;
        int overlayTopBottomHeight =
                mParentLayout.getHeight() - (int) (parentWidth * CARD_HEIGHT_WIDTH_RATIO);
        mOverlayTop.setMinimumHeight(overlayTopBottomHeight);
        mOverlayBottom.setMinimumHeight(overlayTopBottomHeight);

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
            setupCamera();
        }

        if (mTimer == null) {
            setupTimer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        teardown();
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

        // send the image data to the server here
        // expected output JSON schema:
        // { result: { name:, age:, etc.},
        //   error: { message: ..., details: ... } }
        String output = "";
        try {
            JSONObject outputJSON = new JSONObject(output);
            JSONObject error = outputJSON.getJSONObject("error");
            if (error != null) {
                Toast.makeText(MainActivity.this, error.getString("message"), Toast.LENGTH_LONG);
            } else {
                JSONObject result = outputJSON.getJSONObject("result");
                // parse results here

                teardown();
                // send back results here...
            }
        } catch (JSONException e) {
            Toast.makeText(MainActivity.this, "Unexpected error. Please contact support", Toast.LENGTH_SHORT);
        }
    }

    private void captureImage() {
        mCamera.takePicture(null, null, this);
    }

    private void setupCamera() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mCameraManager = (CameraManager) MainActivity.this.getSystemService(Context.CAMERA_SERVICE);
                String cameraId = null;
                if (mCameraManager != null) {
                    cameraId = mCameraManager.getCameraIdList()[0];
                    mCameraManager.setTorchMode(cameraId, true);
                }
            } else {
                mCamera = Camera.open();
                Camera.Parameters params = mCamera.getParameters();
                params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                mCamera.setParameters(params);
            }
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Unable to open camera.", Toast.LENGTH_LONG)
                    .show();
        }
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

    private void teardown() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }
}
