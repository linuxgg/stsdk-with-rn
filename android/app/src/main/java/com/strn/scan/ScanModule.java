package com.strn.scan;

import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.scantrust.sdk.stsdk_demo.ScanActivity;

import java.util.HashMap;
import java.util.Map;

public class ScanModule extends ReactContextBaseJavaModule {
    private static ReactApplicationContext reactContext;


    public ScanModule(ReactApplicationContext context) {
        super(context);
        reactContext = context;
    }

    @NonNull
    @Override
    public String getName() {
        return "STScan";
    }

    @ReactMethod
    public void start() {
        Toast.makeText(getReactApplicationContext(), "scan auth", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(getCurrentActivity(), ScanActivity.class);
        getCurrentActivity().startActivity(i);
    }
}
