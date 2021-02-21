package com.ugur.mychatapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;
import com.squareup.picasso.Picasso;
import com.ugur.mychatapp.Classes.User;
import com.ugur.mychatapp.R;

import java.util.List;

public class AudioCallActivity extends AppCompatActivity {
    String pairId;
    String pairName;
    Uri pairImageUri;
    String callId;

    private String callerID;
    private String recipientID;
    private Call call;
    private SinchClient sinchClient;

    private static final String HOSTNAME = "clientapi.sinch.com";
    private static final String KEY = "545d2392-ad97-42d7-887d-40b72d9fe783";
    private static final String SECRET = "Mpy0Ro0pXEaLBbvFG3MGrw==";

    ImageView imageView;
    TextView textView;
    Button acceptButton;
    Button declineButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_call);

        Intent intent = getIntent();
        try {
            pairImageUri = Uri.parse(intent.getStringExtra("pairImageUri"));
        } catch (Exception e) {
            System.out.println("HATAAAA URIIIII");
        }
        pairId = intent.getStringExtra("pairId");
        pairName = intent.getStringExtra("pairName");
        callId = intent.getStringExtra("callId");
        if (intent.getStringExtra("targetId") != null) {
            recipientID = pairId;
            callerID = User.getInstance().getUuid();
        } else {
            recipientID = User.getInstance().getUuid();
            callerID = pairId;
        }

        imageView = findViewById(R.id.audio_caller_image_view);
        textView = findViewById(R.id.audio_caller_name);
        acceptButton = findViewById(R.id.audio_call_accept_button);
        declineButton = findViewById(R.id.audio_call_decline_button);

        if (User.getInstance().getUuid().equals(callerID)) {
            acceptButton.setVisibility(View.INVISIBLE);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this ,new String[] {Manifest.permission.READ_PHONE_STATE}, 1);
        }

        //Control call id
        if (callId == null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(recipientID).child("Calls").child(callerID);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    callId = snapshot.child("CallId").getValue(String.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

        sinchClient = Sinch.getSinchClientBuilder()
                .context(this)
                .userId(User.getInstance().getUuid())
                .applicationKey(KEY)
                .applicationSecret(SECRET)
                .environmentHost(HOSTNAME)
                .build();

        sinchClient.setSupportCalling(true);
        sinchClient.startListeningOnActiveConnection();
        sinchClient.start();

        sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());

        try {
            Picasso.with(getApplicationContext())
                    .load(pairImageUri)
                    .placeholder(R.drawable.image_place_holder)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }

        textView.setText("Audio Call with " + pairName);
    }

    public void acceptCall(View view) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference();
        try {
            reference.child("Calls").child(callId).child("Start").setValue("Yes");
        } catch (Exception e) {
            System.out.println("Diğer Garip Hata: " + e.getMessage() + "  " + e.getLocalizedMessage());
        }
        call = sinchClient.getCallClient().callUser(callerID);
        call.addCallListener(new SinchCallListener());
        acceptButton.setVisibility(View.INVISIBLE);
        declineButton.setText("End Call");
    }

    public void declineCall(View view) {
        try {
            call.hangup();
        } catch (Exception e) {
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference();
        try {
            reference.child("Calls").child(callId).removeValue();
        } catch (Exception e) {
            System.out.println("GARİP HATA " + e.getLocalizedMessage() + " " + e.getMessage());
        }
        reference.child("Users").child(recipientID).child("Calls").child(callerID).removeValue();
        finish();
    }

    private class SinchCallListener implements CallListener {

        @Override
        public void onCallProgressing(Call call) {
            System.out.println("Call Processing...");
        }

        @Override
        public void onCallEstablished(Call call) {
            System.out.println("Call Established...");
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
            if (User.getInstance().getUuid().equals(callerID)) {
                acceptButton.setVisibility(View.INVISIBLE);
                declineButton.setText("End Call");
            }
        }

        @Override
        public void onCallEnded(Call endedCall) {
            call = null;
            System.out.println("Call Ended...");
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            MainActivity.worked = false;
            finish();
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> list) {
        }
    }

    private class SinchCallClientListener implements CallClientListener {

        @Override
        public void onIncomingCall(CallClient callClient, Call incomingCall) {
            /*System.out.println("Incoming Audio Call is  video offered: " +  incomingCall.getDetails().isVideoOffered());
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference("Calls").child(callId).child("CallerId");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    System.out.println("!!!!! " + snapshot.getValue(String.class) + "  USer: " + User.getInstance().getUuid());
                    if (snapshot.getValue(String.class) != null && snapshot.getValue(String.class).equals(User.getInstance().getUuid())) {
                        call = incomingCall;
                        StyleableToast.makeText(getApplicationContext(), "Call Incoming! ", R.style.SuccessToast).show();
                        call.answer();
                        call.addCallListener(new SinchCallListener());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });*/
            if (incomingCall.getDetails().isVideoOffered()) {
                System.out.println("AUDIO CALL ACTIVITY VIDEO OFFERED");
            } else {
                call = incomingCall;
                StyleableToast.makeText(getApplicationContext(), "Call Incoming! ", R.style.SuccessToast).show();
                call.answer();
                call.addCallListener(new SinchCallListener());
            }


        }
    }
}