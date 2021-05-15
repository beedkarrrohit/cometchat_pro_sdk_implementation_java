package com.example.cometchatprojava;

import android.app.Application;
import android.widget.Toast;

import com.cometchat.pro.core.Call;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.example.cometchatprojava.CallingScreen.CallingListeners;
import com.example.cometchatprojava.CallingScreen.CallingScreen;
import com.example.cometchatprojava.utils.Constants;


public class Initializer extends Application{
    private static final String TAG = "Initializer";
    @Override
    public void onCreate() {
        super.onCreate();
        CometChat.init(this, Constants.app_id, Constants.appSettings, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                Toast.makeText(Initializer.this, "CometChat Initialized Successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(CometChatException e) {
                Toast.makeText(Initializer.this, "Fail to initialize CometChat", Toast.LENGTH_SHORT).show();
            }
        });
        CallingListeners.addCallListeners(TAG,this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        CallingListeners.removeCallListeners(TAG);
    }
}
