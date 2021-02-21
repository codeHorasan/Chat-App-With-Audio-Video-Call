package com.ugur.mychatapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.video.VideoCallListener;

import java.util.List;

public class IncomingCallScreenActivity extends BaseActivity {
    private String mCallId;
    private TextView remoteUser;
    private boolean mAcceptVideo = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call_screen);

        Button answer = findViewById(R.id.answerButton);
        answer.setOnClickListener(mClickListener);
        Button decline = findViewById(R.id.declineButton);
        decline.setOnClickListener(mClickListener);

        mCallId = getIntent().getStringExtra(SinchService.CALL_ID);
    }

    @Override
    protected void onServiceConnected() {
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.addCallListener(new SinchCallListener());
            setCallerName(call.getRemoteUserId());
            remoteUser = findViewById(R.id.remoteUser);


        } else {
            finish();
        }
    }

    private void setCallerName(String id) {
        final String[] callerName = new String[1];
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Users").child(id);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                callerName[0] = snapshot.child("Name").getValue(String.class);
                System.out.println("CallerName: " + callerName);
                remoteUser.setText(callerName[0]);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callerName[0] = "NAME ERROR";
            }
        });
    }

    private void answerClicked() {
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.answer();
            Intent intent = new Intent(this, CallScreenActivity.class);
            intent.putExtra(SinchService.CALL_ID, mCallId);
            startActivity(intent);
        } else {
            finish();
        }
    }

    private void declineClicked() {
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.hangup();
        }
        finish();
    }

    private class SinchCallListener implements VideoCallListener {

        @Override
        public void onCallEnded(Call call) {
            CallEndCause cause = call.getDetails().getEndCause();
            finish();
        }

        @Override
        public void onCallEstablished(Call call) {
            System.out.println("CALL ESTABLISHED");
        }

        @Override
        public void onCallProgressing(Call call) {
            System.out.println("CALL PROGRESSING");
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
            // Send a push through your push provider here, e.g. GCM
        }

        @Override
        public void onVideoTrackAdded(Call call) {
            // Display some kind of icon showing it's a video call
            // and pass it to the CallScreenActivity via Intent and mAcceptVideo
            mAcceptVideo = true;
        }
        @Override
        public void onVideoTrackPaused(Call call) {
            // Display some kind of icon showing it's a video call
        }
        @Override
        public void onVideoTrackResumed(Call call) {
            // Display some kind of icon showing it's a video call
        }
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.answerButton:
                    answerClicked();
                    break;
                case R.id.declineButton:
                    declineClicked();
                    break;
            }
        }
    };
}
