package com.example.cometchatprojava.CallingScreen;

import android.app.Activity;
import android.content.Context;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.Call;
import com.cometchat.pro.core.CometChat;
import com.example.cometchatprojava.utils.CallUtils;

public class CallingListeners {
    public static void addCallListeners(String name, Context context){
        CometChat.addCallListener(name, new CometChat.CallListener() {
            @Override
            public void onIncomingCallReceived(Call call) {
                if(call != null){
                    if(call.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER)){
                        CallUtils.createCallingIntent(context,call.getCallInitiator(),call.getType(),false,call.getSessionId());
                    }else{
                        CallUtils.createCallingIntent(context,call.getCallReceiver(),call.getType(),false,call.getSessionId());
                    }

                }
            }

            @Override
            public void onOutgoingCallAccepted(Call call) {
                if(call != null){
                    CallUtils.startCometChatCall(CallingScreen.activity,CallingScreen.view,call);
                }
            }

            @Override
            public void onOutgoingCallRejected(Call call) {
                if(call != null){
                    CallingScreen.activity.finish();
                }
            }

            @Override
            public void onIncomingCallCancelled(Call call) {
                if(call != null){
                    CallingScreen.activity.finish();
                }
            }
        });
    }
    
    public static void removeCallListeners(String name){
        CometChat.removeCallListener(name);
    }
}
