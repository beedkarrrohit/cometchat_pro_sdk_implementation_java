package com.example.cometchatprojava.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.Call;
import com.cometchat.pro.core.CallSettings;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.AppEntity;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.User;
import com.cometchat.pro.rtc.model.AudioMode;
import com.example.cometchatprojava.CallingScreen.CallingScreen;

import java.util.List;

public class CallUtils {
    private static final String TAG = "CallUtils";
    public static void initiateCall(Context context,String receiverId,String receiverType,String callType){
        Call call = new Call(receiverId,receiverType,callType);
        CometChat.initiateCall(call, new CometChat.CallbackListener<Call>() {
            @Override
            public void onSuccess(Call call) {
                createCallingIntent(context,call.getCallReceiver(),call.getType(),true,call.getSessionId());
            }

            @Override
            public void onError(CometChatException e) {
                Log.e(TAG, "onError: "+e.getMessage());
            }
        });
    }

    public static void createCallingIntent(Context context, AppEntity appEntity,String type,boolean isOutgoing,String sessionId){
        Intent intent = new Intent(context, CallingScreen.class);
        intent.putExtras(createIntentBundle(appEntity,sessionId));
        intent.setAction(type);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(isOutgoing){
            intent.setType("outgoing");
        }else{
            intent.setType("incoming");
        }
        context.startActivity(intent);
    }

    public static void startCometChatCall(Activity activity, RelativeLayout view, Call call){
        CallSettings callSettings = new CallSettings.CallSettingsBuilder(activity,view).setSessionId(call.getSessionId()).build();
        CometChat.startCall(callSettings, new CometChat.OngoingCallListener() {
            @Override
            public void onUserJoined(User user) {

            }

            @Override
            public void onUserLeft(User user) {

            }

            @Override
            public void onError(CometChatException e) {

            }

            @Override
            public void onCallEnded(Call call) {
                activity.finish();
            }

            @Override
            public void onUserListUpdated(List<User> list) {

            }

            @Override
            public void onAudioModesUpdated(List<AudioMode> list) {

            }
        });
    }

    private static Bundle createIntentBundle(AppEntity appEntity,String sessionId){
        Bundle bundle = new Bundle();
        if(appEntity instanceof User){
            User user = (User) appEntity;
            bundle.putString("name",user.getName());
            bundle.putString("id",user.getUid());
            bundle.putString("avatar",user.getAvatar());
            bundle.putString("session_id",sessionId);
        }else{
            Group group =(Group) appEntity;
            bundle.putString("name",group.getName());
            bundle.putString("id",group.getGuid());
            bundle.putString("avatar",group.getIcon());
            bundle.putString("session_id",sessionId);
        }
        return bundle;
    }
}
