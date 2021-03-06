package com.example.cometchatprojava.viewModels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.Call;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.ConversationsRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Action;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.Conversation;
import com.cometchat.pro.models.TextMessage;
import com.example.cometchatprojava.adapters.ConversationListAdapter;

import java.util.ArrayList;
import java.util.List;

public class ConversationViewModel extends ViewModel {
    private static final String TAG = "ConversationViewModel";
    private final MutableLiveData<List<Conversation>> list = new MutableLiveData<>();
    private MutableLiveData<Integer> count = new MutableLiveData<>();
    public LiveData<Integer> getUnreadCount(){
        return count;
    }
    public LiveData<List<Conversation>> getList(){
        return list;
    }

    public void fetConversations(ConversationsRequest conversationsRequest){
        conversationsRequest.fetchNext(new CometChat.CallbackListener<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                if(conversations != null){
                    list.setValue(conversations);
                    int counts = 0;
                    for(Conversation conversation : conversations){
                        counts = counts + conversation.getUnreadMessageCount();
                    }
                    count.setValue(counts);
                }
            }

            @Override
            public void onError(CometChatException e) {

            }
        });
    }

    public void updateConversationList(Conversation conversation){
        List<Conversation> currentList = (List<Conversation>) list.getValue();
        if(currentList.contains(conversation)){
            Conversation oldConversation = currentList.get(currentList.lastIndexOf(conversation));
            currentList.remove(oldConversation);
            String catergory = conversation.getLastMessage().getCategory();
            if(catergory.equals(CometChatConstants.CATEGORY_ACTION) || catergory.equals(CometChatConstants.CATEGORY_CUSTOM)
                || conversation.getLastMessage().getEditedAt() != 0L || conversation.getLastMessage().getDeletedAt() != 0L){
                conversation.setUnreadMessageCount(oldConversation.getUnreadMessageCount());
                Log.e(TAG, "updateConversationList: if called");

            }else {
                Log.e(TAG, "updateConversationList: else called");
                conversation.setUnreadMessageCount(oldConversation.getUnreadMessageCount()+1);
                if(count.getValue() != null){
                    count.setValue(count.getValue() + 1);
                }else{
                    count.setValue(1);
                }

            }
            currentList.add(0,conversation);
            list.setValue(currentList);
        }else{
            currentList.add(0,conversation);
            list.setValue(currentList);
        }
    }
}
