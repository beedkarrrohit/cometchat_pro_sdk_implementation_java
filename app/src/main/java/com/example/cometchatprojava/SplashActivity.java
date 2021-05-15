package com.example.cometchatprojava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.cometchat.pro.core.CometChat;
import com.example.cometchatprojava.cometChatActivity.CometChatActivity;
import com.example.cometchatprojava.login_activity.LoginActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(CometChat.getLoggedInUser() == null){
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }else{
                    startActivity(new Intent(SplashActivity.this, CometChatActivity.class));
                }
                finish();
            }
        },1000);
    }
}