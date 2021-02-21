package com.ugur.mychatapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import com.ugur.mychatapp.Classes.User;
import com.ugur.mychatapp.R;

import java.util.List;

public class MakeCallActivity extends AppCompatActivity {
    private TextView nameText, emailText, idText;
    private SinchClient sinchClient;
    private Call call;
    private ServiceConnection connection;

    private static final String HOSTNAME = "clientapi.sinch.com";
    private static final String KEY = "545d2392-ad97-42d7-887d-40b72d9fe783";
    private static final String SECRET = "Mpy0Ro0pXEaLBbvFG3MGrw==";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makecall);

        nameText = findViewById(R.id.text_view_name);
        emailText = findViewById(R.id.text_view_email);
        idText = findViewById(R.id.text_view_id);

        User user = User.getInstance();
        nameText.setText(user.getName());
        emailText.setText(user.getEmail());
        idText.setText(user.getUuid());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this ,new String[] {Manifest.permission.READ_PHONE_STATE}, 1);
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
    }


    public void endCall(View view) {
        call.hangup();
    }

    private class SinchCalllistener implements CallListener {

        @Override
        public void onCallProgressing(Call call) {
            System.out.println("Ringing...");
        }

        @Override
        public void onCallEstablished(Call call) {
            System.out.println("Call established...");
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        }

        @Override
        public void onCallEnded(Call endedCall) {
            System.out.println("CAll ended...");
            call = null;
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> list) {

        }
    }

    private class SinchCallClientListener implements CallClientListener {

        @Override
        public void onIncomingCall(CallClient callClient, Call incomingCall) {
            //Servis al
            call = incomingCall;
            //System.out.println("Incoming Call!");
            call.answer();
            call.addCallListener(new SinchCalllistener());
            //System.out.println("Hang up");
        }
    }

    public void makeAudioCall(View view) {
        if (call == null) {
            //Servisi Ver

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            reference.child("Users").child("uCM9dGsIVkcWH0Wo0cgvoExZdkB3").child("Calling").setValue(User.getInstance().getUuid());
            call = sinchClient.getCallClient().callUser("uCM9dGsIVkcWH0Wo0cgvoExZdkB3");
            call.addCallListener(new SinchCalllistener());
        } else {
            call.hangup();
        }
    }

    public void makeVideoCall(View view) {

    }


}