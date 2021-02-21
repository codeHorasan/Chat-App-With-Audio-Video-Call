package com.ugur.mychatapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.sinch.android.rtc.calling.Call;
import com.ugur.mychatapp.Classes.User;

public class PlaceCallActivity extends BaseActivity {
    private Button button;
    private String targetId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_call);

        Intent intent = getIntent();
        targetId = intent.getStringExtra("targetId");
        System.out.println("Alınan TargetID: " + targetId + "  My Id: " + User.getInstance().getUuid());

    }

    public void startCall(View view) {
        System.out.println("ARADĞIM: " + getSinchServiceInterface());
        Call call = getSinchServiceInterface().callUserVideo(targetId);
        String callId = call.getCallId();
        System.out.println("CALL ID: " + callId);

        Intent callScreen = new Intent(this, CallScreenActivity.class);
        callScreen.putExtra(SinchService.CALL_ID, callId);
        startActivity(callScreen);
    }

    @Override
    protected void onServiceConnected() {
        System.out.println("PLACE CALL ACTIVITY ON SERVICE CONNECTED");
    }

}
