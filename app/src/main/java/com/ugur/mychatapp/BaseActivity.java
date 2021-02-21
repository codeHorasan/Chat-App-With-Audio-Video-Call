package com.ugur.mychatapp;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public abstract class BaseActivity extends AppCompatActivity implements ServiceConnection {
    private SinchService.SinchServiceInterface mSinchServiceInterface;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindService();
        /*requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);*/

    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        System.out.println("BASE ACTIVITY ON SERVICE CONNECTED ÜST");
        if (SinchService.class.getName().equals(name.getClassName())) {
            System.out.println("BASE ACTIVITY ON SERVICE CONNECTED ALT");
            mSinchServiceInterface = (SinchService.SinchServiceInterface) service;
            onServiceConnected();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        System.out.println("BASE ACTIVITY ON SERVICE DISCONNECTED ÜST");
        if (SinchService.class.getName().equals(name.getClassName())) {
            System.out.println("BASE ACTIVITY ON SERVICE DISCONNECTED ALT");
            mSinchServiceInterface = null;
            onServiceDisconnected();
        }
    }

    protected void onServiceConnected() {
        System.out.println("BASE ACTIVITY PROTECTED ON SERVICE CONNECTED");
    }

    protected void onServiceDisconnected() {
        System.out.println("BASE ACTIVITY PROTECTED ON SERVICE DISCONNECTED");
    }

    protected SinchService.SinchServiceInterface getSinchServiceInterface() {
        System.out.println("BASE ACTIVITY GET SINCH SERVICE INTERFACE");
        return mSinchServiceInterface;
    }

    private Messenger messenger = new Messenger(new Handler() {
        @Override
        public void handleMessage(Message msg) {
            System.out.println("BASE ACTIVITY HANDLE MESSENGER");
            switch (msg.what) {
                case SinchService.MESSAGE_PERMISSIONS_NEEDED:
                    Bundle bundle = msg.getData();
                    String requiredPermission = bundle.getString(SinchService.REQUIRED_PERMISSION);
                    ActivityCompat.requestPermissions(BaseActivity.this, new String[]{requiredPermission}, 0);
                    break;
            }
        }
    });

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean granted = grantResults.length > 0;
        for (int grantResult : grantResults) {
            granted &= grantResult == PackageManager.PERMISSION_GRANTED;
        }
        if (granted) {
            Toast.makeText(this, "You may now place a call", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "This application needs permission to use your microphone and camera to function properly.", Toast.LENGTH_LONG).show();
        }
        mSinchServiceInterface.retryStartAfterPermissionGranted();
    }

    private void bindService() {
        System.out.println("BASE ACTIVITY ON BIND SERVICE");
        Intent serviceIntent = new Intent(this, SinchService.class);
        serviceIntent.putExtra(SinchService.MESSENGER, messenger);
        getApplicationContext().bindService(serviceIntent, this, BIND_AUTO_CREATE);
    }
}