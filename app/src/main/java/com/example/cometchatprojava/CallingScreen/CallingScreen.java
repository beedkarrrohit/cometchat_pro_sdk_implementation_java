package com.example.cometchatprojava.CallingScreen;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.Manifest.permission;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.Call;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.example.cometchatprojava.R;
import com.example.cometchatprojava.databinding.ActivityCallingScreenBinding;
import com.example.cometchatprojava.utils.CallUtils;
import com.example.cometchatprojava.utils.Helper;

public class CallingScreen extends AppCompatActivity implements View.OnClickListener {

    @SuppressLint("StaticFieldLeak")
    public static Activity activity;
    @SuppressLint("StaticFieldLeak")
    public static RelativeLayout view;
    private String name;
    private String avatar;
    private String id;
    private String sessionId;
    private static boolean videoCall;
    private static boolean incomingCall;
    private ActivityCallingScreenBinding binding;
    private static final String TAG = "CallingScreen";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCallingScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        view = binding.mainView;
        getIntentExtas();
        setupView();
        setupFields();
    }

    private void getIntentExtas(){
        Bundle bundle = getIntent().getExtras();
        name = bundle.getString("name");
        avatar = bundle.getString("avatar");
        id = bundle.getString("id");
        sessionId = bundle.getString("session_id");
        incomingCall = getIntent().getType().equals("incoming");
        videoCall = getIntent().getAction().equals(CometChatConstants.CALL_TYPE_VIDEO);
    }

    private void setupView(){
        if(incomingCall){
            if(videoCall){
                binding.callTypeTitle.setText("Incoming Video Call...");
            }else{
                binding.callTypeTitle.setText("Incoming Call...");
            }
            binding.endCall.setVisibility(View.GONE);
            binding.acceptCall.setVisibility(View.VISIBLE);
            binding.rejectCall.setVisibility(View.VISIBLE);
        }else{
            binding.callTypeTitle.setText("Calling...");
        }
        binding.callingUsername.setText(name);
        Glide.with(binding.getRoot()).load(avatar).placeholder(R.drawable.user).into(binding.callingAvatar);
    }

    private void setupFields(){
        binding.endCall.setOnClickListener(this);
        binding.rejectCall.setOnClickListener(this);
        binding.acceptCall.setOnClickListener(this);
        if(!Helper.permissions(this,permission.RECORD_AUDIO) && !Helper.permissions(this,permission.CAMERA)){
            String[] permissions = {permission.RECORD_AUDIO,permission.CAMERA};
            requestPermissions(permissions,102);
        }
    }

    private void acceptCall(){
        CometChat.acceptCall(sessionId, new CometChat.CallbackListener<Call>() {
            @Override
            public void onSuccess(Call call) {
                if(call != null){
                    CallUtils.startCometChatCall(CallingScreen.this,view,call);
                }
            }

            @Override
            public void onError(CometChatException e) {
                Log.e(TAG, "onError: "+e.getMessage());
            }
        });
    }

    private void rejectCall(String status){
        CometChat.rejectCall(sessionId, status, new CometChat.CallbackListener<Call>() {
            @Override
            public void onSuccess(Call call) {
                finish();
            }
            @Override
            public void onError(CometChatException e) {
                Log.e(TAG, "onError: "+e.getMessage());
                finish();
            }
        });
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.end_call){
            rejectCall(CometChatConstants.CALL_STATUS_CANCELLED);
        }else if(id == R.id.reject_call){
            rejectCall(CometChatConstants.CALL_STATUS_REJECTED);
        }else if(id == R.id.accept_call){
            acceptCall();
        }
    }
}
