package com.example.cometchatprojava.cometChatActivity.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.ConversationsRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.helpers.CometChatHelper;
import com.cometchat.pro.models.Action;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.Conversation;
import com.cometchat.pro.models.CustomMessage;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.MediaMessage;
import com.cometchat.pro.models.MessageReceipt;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.models.TypingIndicator;
import com.cometchat.pro.models.User;
import com.example.cometchatprojava.R;
import com.example.cometchatprojava.adapters.ConversationListAdapter;
import com.example.cometchatprojava.adapters.ItemClickListener;
import com.example.cometchatprojava.chatActivity.ChatScreenActivity;
import com.example.cometchatprojava.cometChatActivity.CometChatActivity;
import com.example.cometchatprojava.databinding.FragmentConversationBinding;
import com.example.cometchatprojava.databinding.RecyclerBinding;
import com.example.cometchatprojava.databinding.RecyclerItemRowBinding;
import com.example.cometchatprojava.databinding.SearchLayoutBinding;
import com.example.cometchatprojava.diffUtil.DiffUtils;
import com.example.cometchatprojava.viewModels.ConversationViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConversationFragment extends Fragment implements ItemClickListener, TextWatcher {
    private FragmentConversationBinding binding;
    private RecyclerBinding subbinding;
    private SearchLayoutBinding searchBinding;
    private ConversationListAdapter adapter;
    private static String uid;
    private ConversationViewModel viewModel;
    private static final String TAG = "ConversationFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentConversationBinding.bind(inflater.inflate(R.layout.fragment_conversation, container, false));
        subbinding = RecyclerBinding.bind(binding.getRoot());
        searchBinding = SearchLayoutBinding.bind(binding.getRoot());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new ConversationListAdapter(new DiffUtils<Conversation>(), this);
        subbinding.recycler.setAdapter(adapter);
        searchBinding.search.addTextChangedListener(this);
        uid = CometChat.getLoggedInUser().getUid();
        viewModel = new ViewModelProvider(this).get(ConversationViewModel.class);
        viewModel.getList().observe(getViewLifecycleOwner(), conversations -> {
            adapter.submitList(conversations);
            adapter.notifyDataSetChanged();
        });
        viewModel.getUnreadCount().observe(getViewLifecycleOwner(),count ->{
            Log.e(TAG, "onViewCreated: "+ count);
            if(count > 0){
                ((CometChatActivity) requireActivity()).getAllUnreadCount(count);
            }else{
                ((CometChatActivity) requireActivity()).removeBadge();
            }
        });

    }

    public void getConversations() {
        ConversationsRequest conversationsRequest = new ConversationsRequest.ConversationsRequestBuilder().setLimit(30).build();
        viewModel.fetConversations(conversationsRequest);

    }

    @Override
    public void onItemClick(int position) {
        Conversation conversation = adapter.getCurrentList().get(position);
        Intent intent = new Intent(getActivity(), ChatScreenActivity.class);
        intent.putExtras(createIntentBundle(conversation));
        startActivity(intent);
    }

    private Bundle createIntentBundle(Conversation conversation){
        Bundle bundle = new Bundle();
        if(conversation.getConversationType().equals(CometChatConstants.CONVERSATION_TYPE_USER)){
            User user = (User) conversation.getConversationWith();
            bundle.putString("name",user.getName());
            bundle.putString("id",user.getUid());
            bundle.putString("avatar",user.getAvatar());
            bundle.putString("status",user.getStatus());
            bundle.putString("selfuid",uid);
            bundle.putString("type",conversation.getConversationType());
        }else{
            Group group = (Group) conversation.getConversationWith();
            bundle.putString("name",group.getName());
            bundle.putString("id",group.getGuid());
            bundle.putString("avatar",group.getIcon());
            bundle.putInt("member_counts",group.getMembersCount());
            bundle.putString("selfuid",uid);
            bundle.putString("type",conversation.getConversationType());
        }
            return bundle;
    }

    @Override
    public void onResume() {
        super.onResume();
        getConversations();
        addCometChatListeners();
    }

    @Override
    public void onPause() {
        super.onPause();
        removeCometChatListeners();
    }

    public void addCometChatListeners(){
        CometChat.addMessageListener(TAG, new CometChat.MessageListener() {
            @Override
            public void onTextMessageReceived(TextMessage textMessage) {
                Log.e(TAG, "onTextMessageReceivedConversation: "+textMessage);
                updateConversations(textMessage);
            }

            @Override
            public void onMediaMessageReceived(MediaMessage mediaMessage) {
                updateConversations(mediaMessage);
            }

            @Override
            public void onCustomMessageReceived(CustomMessage customMessage) {
                updateConversations(customMessage);
            }

            @Override
            public void onTypingStarted(TypingIndicator typingIndicator) {
                super.onTypingStarted(typingIndicator);
            }

            @Override
            public void onTypingEnded(TypingIndicator typingIndicator) {
                super.onTypingEnded(typingIndicator);
            }

            @Override
            public void onMessagesDelivered(MessageReceipt messageReceipt) {
                super.onMessagesDelivered(messageReceipt);
            }

            @Override
            public void onMessagesRead(MessageReceipt messageReceipt) {
                super.onMessagesRead(messageReceipt);
            }

            @Override
            public void onMessageEdited(BaseMessage baseMessage) {
                super.onMessageEdited(baseMessage);
            }

            @Override
            public void onMessageDeleted(BaseMessage baseMessage) {
                super.onMessageDeleted(baseMessage);
            }
        });

        CometChat.addGroupListener(TAG, new CometChat.GroupListener() {
            @Override
            public void onGroupMemberJoined(Action action, User user, Group group) {
                super.onGroupMemberJoined(action, user, group);
            }

            @Override
            public void onGroupMemberLeft(Action action, User user, Group group) {
                super.onGroupMemberLeft(action, user, group);
            }

            @Override
            public void onGroupMemberKicked(Action action, User user, User user1, Group group) {
                super.onGroupMemberKicked(action, user, user1, group);
            }

            @Override
            public void onGroupMemberBanned(Action action, User user, User user1, Group group) {
                super.onGroupMemberBanned(action, user, user1, group);
            }

            @Override
            public void onGroupMemberUnbanned(Action action, User user, User user1, Group group) {
                super.onGroupMemberUnbanned(action, user, user1, group);
            }

            @Override
            public void onGroupMemberScopeChanged(Action action, User user, User user1, String s, String s1, Group group) {
                super.onGroupMemberScopeChanged(action, user, user1, s, s1, group);
            }

            @Override
            public void onMemberAddedToGroup(Action action, User user, User user1, Group group) {
                super.onMemberAddedToGroup(action, user, user1, group);
            }
        });
    }

    private void updateConversations(BaseMessage baseMessage) {
        Conversation conversation = CometChatHelper.getConversationFromMessage(baseMessage);
        viewModel.updateConversationList(conversation);
    }

    private void removeCometChatListeners(){
        CometChat.removeMessageListener(TAG);
        CometChat.removeGroupListener(TAG);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if(adapter != null && adapter.getCurrentList().size() >0){
            if(charSequence.length() > 0){
                String characters = charSequence.toString();
                List<Conversation> searchList = new ArrayList<>();
                for(Conversation conversation : adapter.getCurrentList()){
                    if(conversation.getConversationWith() instanceof  User){
                        User user =(User) conversation.getConversationWith();
                        if(user.getName().toLowerCase().contains(characters.toLowerCase())){
                            searchList.add(conversation);
                        }
                    }else{
                        Group group =(Group) conversation.getConversationWith();
                        if(group.getName().toLowerCase().contains(characters.toLowerCase())){
                            searchList.add(conversation);
                        }
                    }
                }
                if(searchList.size()>0){
                    Log.e(TAG, "onTextChanged: no empty called");
                    adapter.submitList(searchList);
                    adapter.notifyDataSetChanged();
                }
            }else {
                getConversations();
            }
        }

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    //private void
}