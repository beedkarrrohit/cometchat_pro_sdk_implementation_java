package com.example.cometchatprojava.login_activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.example.cometchatprojava.cometChatActivity.CometChatActivity;
import com.example.cometchatprojava.utils.Constants;

class Login {
    static void login(String uid, Context context){
        CometChat.login(uid, Constants.auth_key, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                if(user != null){
                    context.startActivity(new Intent(context, CometChatActivity.class));
                    Activity activity = (Activity) context;
                    activity.finish();
                }
            }

            @Override
            public void onError(CometChatException e) {

            }
        });
    }
}
