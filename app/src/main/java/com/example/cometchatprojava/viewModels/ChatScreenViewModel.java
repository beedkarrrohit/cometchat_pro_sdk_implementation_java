package com.example.cometchatprojava.viewModels;


import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.MessagesRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.MediaMessage;
import com.cometchat.pro.models.MessageReceipt;
import com.cometchat.pro.models.TextMessage;

import java.util.ArrayList;
import java.util.Collections.*;
import java.util.List;

import static java.util.Collections.reverse;

public class ChatScreenViewModel extends ViewModel {
    private static final String TAG = "ChatScreenViewModel";
    private final MutableLiveData<List<BaseMessage>> list = new MutableLiveData<>();

    public LiveData<List<BaseMessage>> getList(){
        return list;
    }

    public void fetchMessages(String id,String type,int limit){
        Log.e(TAG, "fetMessages: called");
        MessagesRequest messagesRequest;
        if(type.equals(CometChatConstants.CONVERSATION_TYPE_USER)){
            Log.e(TAG, "fetchMessages: ifCalled");
            messagesRequest = new MessagesRequest.MessagesRequestBuilder().setUID(id).setLimit(limit).build();
        }else {
            Log.e(TAG, "fetchMessages: else called");
            messagesRequest = new MessagesRequest.MessagesRequestBuilder().setGUID(id).setLimit(limit).build();
        }
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> baseMessages) {
                if(baseMessages != null && baseMessages.size() > 0){
                    Log.e(TAG, "onSuccess: "+baseMessages );
                    list.setValue(baseMessages);
                    BaseMessage baseMessage = baseMessages.get(baseMessages.size() -1);
                    if(!baseMessage.getSender().getUid().equals(CometChat.getLoggedInUser().getUid()) && baseMessage.getReadAt() == 0L){
                        Log.e(TAG, "onSuccess: Called");
                        markAsRead(baseMessage,baseMessage.getReceiverType());
                    }
                }
            }
            @Override
            public void onError(CometChatException e) {
                Log.e(TAG, "onError: "+ e);
            }
        });
    }

    public void sendMessage(BaseMessage baseMessage){
        Log.e(TAG, "sendMessage: called");
        if(baseMessage instanceof TextMessage){
            TextMessage textMessage = (TextMessage) baseMessage;
            CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
                @Override
                public void onSuccess(TextMessage textMessage) {
                    Log.e(TAG, "onSuccess: ");
                    addMessageToList(textMessage);
                }
                @Override
                public void onError(CometChatException e) {
                    Log.e(TAG, "onError: "+e);
                }
            });
        }else if(baseMessage instanceof MediaMessage){
            Log.e(TAG, "onSuccessMediaMessage called ");
            MediaMessage mediaMessage = (MediaMessage) baseMessage;
            CometChat.sendMediaMessage(mediaMessage, new CometChat.CallbackListener<MediaMessage>() {
                @Override
                public void onSuccess(MediaMessage mediaMessage) {
                    Log.e(TAG, "onSuccessMediaMessage: "+mediaMessage);
                    addMessageToList(mediaMessage);
                }

                @Override
                public void onError(CometChatException e) {
                    Log.e(TAG, "onError: send Media Message "+ e.getMessage());
                }
            });
        }
    }
    public void addMessageToList(BaseMessage baseMessage){
        Log.e(TAG, "addMessageToList: "+baseMessage);
        if(list.getValue() != null){
            List<BaseMessage> newList = new ArrayList<>(list.getValue());
            newList.add(baseMessage);
            list.setValue(newList);
            if(!baseMessage.getSender().getUid().equals(CometChat.getLoggedInUser().getUid())){
                markAsRead(baseMessage,baseMessage.getReceiverType());
            }
        }
    }
    public void setMessageDelivery(MessageReceipt messageReceipt){
        if(list.getValue() != null){
            List<BaseMessage> newList = new ArrayList<>(list.getValue());
            reverse(newList);
            for(BaseMessage baseMessage : newList){
                if(baseMessage.getDeliveredAt() == 0L){
                    int index = newList.indexOf(baseMessage);
                    newList.get(index).setDeliveredAt(messageReceipt.getDeliveredAt());
                }
            }
            reverse(newList);
            list.setValue(newList);
        }
    }

    public void setReadReceipts(MessageReceipt messageReceipt){
        if(list.getValue() != null){
            List<BaseMessage> newList = new ArrayList<>(list.getValue());
            reverse(newList);
            for(BaseMessage baseMessage : newList){
                if(baseMessage.getReadAt()==0L){
                    int index = newList.indexOf(baseMessage);
                    newList.get(index).setReadAt(messageReceipt.getReadAt());
                }
            }
            reverse(newList);
            list.setValue(newList);
        }
    }

    public void markAsRead(BaseMessage baseMessage,String type){
        Log.e(TAG, "onSuccess: Called 2");
        if(type.equals(CometChatConstants.RECEIVER_TYPE_USER)){
            CometChat.markAsRead(baseMessage.getId(),baseMessage.getSender().getUid(),baseMessage.getReceiverType());
        }else {
            CometChat.markAsRead(baseMessage.getId(),baseMessage.getReceiverUid(),baseMessage.getReceiverType());
        }

    }
}
