package com.scantrust.sdk.stsdk_demo;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.scantrust.mobile.android_sdk.camera.config.ConsumerParams;
import com.scantrust.mobile.android_sdk.controllers.DoublePingFlowController;
import com.scantrust.mobile.android_sdk.core.FrameData;
import com.scantrust.mobile.android_sdk.def.CodeSpecialState;
import com.scantrust.mobile.android_sdk.def.DoublePingState;
import com.scantrust.mobile.android_sdk.def.MatcherResultBase;
import com.scantrust.mobile.android_sdk.util.BaseMatcher;
import com.scantrust.mobile.android_sdk.util.ModelSettingsManager;
import com.scantrust.mobile.android_sdk.util.STRegularMatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScanActivity extends AppCompatActivity {
    private static final String TAG = ScanActivity.class.getSimpleName();
    public DoublePingFlowController controller;
    private static final int PERMISSION_REQUEST_CODE = 999;
    private RelativeLayout mPreviewFrameLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "start scan activity");
        setContentView(R.layout.camera_layout);
        mPreviewFrameLayout = findViewById(R.id.surface_view);
        if (ContextCompat.checkSelfPermission(
                ScanActivity.this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED) {
            initModelSettingManagerAndMore();
        } else {
            // You can directly ask for the permission.
            requestPermissions(
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initModelSettingManagerAndMore();
                } else {
                    requestPermissions(
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSION_REQUEST_CODE);
                }
        }
    }


    private void initModelSettingManagerAndMore() {
        Log.d(TAG, "start to initModelSettingManagerAndMore");
        //need to init ModelSettingsManager to get if phone supported, isZoomConsistent
        //set staging URL as example
        String baseUrl = "https://api.staging.scantrust.io";
        ModelSettingsManager.INSTANCE.getSetting(baseUrl, new ModelSettingsManager.Callback() {
            @Override
            public void onSuccess() {
                initCamera();
            }

            @Override
            public void onError(int errorCode, @NonNull String errorMessage) {
                String errorMsgWithSupportEmail = "[" + errorCode + "]" + errorMessage + "\n\n";
                showErrorDialog(errorMsgWithSupportEmail);
            }
        });

    }

    private void showErrorDialog(String errorMessage) {

        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(ScanActivity.this);
        materialAlertDialogBuilder.setCancelable(false)
                .setTitle("Error")
                .setMessage(errorMessage)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        finish();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

    }

    private void initCamera() {
        ArrayList<BaseMatcher> matcherList = new ArrayList<>();
        try {
            controller = new DoublePingFlowController.Builder(ScanActivity.this, ModelSettingsManager.INSTANCE, callback)
                    .scanEngineParams(new ConsumerParams.Builder().matchers(matcherList).build())
                    .build();

            if (controller != null) {
                controller.startCamera();
                final View previewView = controller.getPreviewView();
                mPreviewFrameLayout.addView(previewView);
                previewView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        // Make the focus on the touch position
                        controller.doAutoFocus(motionEvent.getX(), motionEvent.getY(), previewView.getWidth(), previewView.getHeight());
                        view.performClick();
                        return true;
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private DoublePingFlowController.DoublePingCallback callback = new DoublePingFlowController.DoublePingCallback() {

        @Override
        public void onReadyForAuth() {

        }

        @Override
        public void onCameraResult(FrameData frameData, DoublePingState doublePingState) {
            Log.d(TAG, "scanning");
        }

        @Override
        public void onCodeNotCentered() {

        }

        @Override
        public void onZoomedIn() {

        }

        @Override
        public void onConfigurationDone(float v, float v1, float v2) {

        }

        @Override
        public void onParamsNeeded(MatcherResultBase matcherResultBase) {

        }

        @Override
        public void onCodeSpecialState(String s, CodeSpecialState codeSpecialState) {

        }
    };
}
