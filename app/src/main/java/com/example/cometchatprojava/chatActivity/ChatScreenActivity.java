package com.example.cometchatprojava.chatActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.CometChat.*;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Action;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.CustomMessage;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.MediaMessage;
import com.cometchat.pro.models.MessageReceipt;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.models.TypingIndicator;
import com.cometchat.pro.models.User;
import com.example.cometchatprojava.R;
import com.example.cometchatprojava.adapters.MessageAdapter;
import com.example.cometchatprojava.bottomSheet.AttachmentSheet;
import com.example.cometchatprojava.databinding.ActivityChatScreenBinding;
import com.example.cometchatprojava.databinding.CreateTextMessageLayoutBinding;
import com.example.cometchatprojava.diffUtil.DiffUtils;
import com.example.cometchatprojava.utils.CallUtils;
import com.example.cometchatprojava.utils.Helper;
import com.example.cometchatprojava.viewModels.ChatScreenViewModel;
import android.Manifest.permission;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ChatScreenActivity extends AppCompatActivity implements View.OnClickListener, AttachmentSheet.BottomSheetListener,
        TextWatcher, SwipeRefreshLayout.OnRefreshListener {
    private ActivityChatScreenBinding binding;
    private CreateTextMessageLayoutBinding subinding;
    private static String status;
    private static int memberCount;
    private static String avatar;
    private static String name;
    private static String id;
    private static String type;
    private  static String selfuid;
    private static final String TAG = "ChatScreenActivity";
    private ChatScreenViewModel viewModel;
    private MessageAdapter adapter;
    private AttachmentSheet attachmentSheet;
    private LinearLayoutManager linearLayoutManager;
    private boolean isRefreshed;
    private boolean isIncoming;
    private boolean isScrolledRequired;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatScreenBinding.inflate(getLayoutInflater());
        subinding = CreateTextMessageLayoutBinding.bind(binding.getRoot());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.chatScreenToolbar);
        handleIntent();
        setupFields();
    }

    private void setupFields() {
        viewModel = new ViewModelProvider(this).get(ChatScreenViewModel.class);
        Glide.with(this).load(avatar).placeholder(R.drawable.user).into(binding.avatar);
        if(type.equals(CometChatConstants.CONVERSATION_TYPE_USER)){
            Log.e(TAG, "handleIntent: "+ status);
            binding.toolbarUserName.setText(name);
            binding.toolbarStatus.setText(status);
        }else{
            binding.toolbarStatus.setVisibility(View.GONE);
            binding.toolbarUserName.setVisibility(View.GONE);
            binding.toolbarGroupName.setVisibility(View.VISIBLE);
            binding.toolbarGroupName.setText(name);
        }
        linearLayoutManager = new LinearLayoutManager(this);
        binding.swipeRefresh.setOnRefreshListener(this);
        binding.newMessage.setOnClickListener(this);
        subinding.sendBtn.setOnClickListener(this);
        subinding.buttonAttach.setOnClickListener(this);
        subinding.edtMsg.addTextChangedListener(this);
        attachmentSheet = new AttachmentSheet();
        adapter = new MessageAdapter(new DiffUtils<>());
        binding.chatscreenRecycler.setLayoutManager(linearLayoutManager);
        binding.chatscreenRecycler.setAdapter(adapter);
        viewModel.getList().observe(this, new Observer<List<BaseMessage>>() {
            @Override
            public void onChanged(List<BaseMessage> baseMessages) {
                Log.e(TAG, "onChanged: "+baseMessages);
                adapter.submitList(baseMessages);
                adapter.notifyDataSetChanged();
                Log.e(TAG, "onChanged: isRef "+ isRefreshed );
                Log.e(TAG, "onScrollChange: before if " + isIncoming);
                if(adapter.getItemCount()>0 && !isRefreshed && !isIncoming){
                    Log.e(TAG, "onScrollChange: if n" + isIncoming);
                    linearLayoutManager.smoothScrollToPosition(binding.chatscreenRecycler,null,adapter.getItemCount());
                }else{
                    if(isIncoming && isScrolledRequired){
                        linearLayoutManager.smoothScrollToPosition(binding.chatscreenRecycler,null,adapter.getItemCount());
                    }else if(isIncoming && !isScrolledRequired){
                        binding.newMessage.setVisibility(View.VISIBLE);
                    }
                }
                isRefreshed =  false;
            }
        });
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            binding.chatscreenRecycler.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                    if(adapter.getItemCount() - linearLayoutManager.findLastVisibleItemPosition() > 5){
                        isScrolledRequired = false;
                    }else{
                       isScrolledRequired = true;
                    }
                }
            });
        }
    }

    private void handleIntent(){
        Bundle bundle = getIntent().getExtras();
        status = bundle.getString("status");
        memberCount = bundle.getInt("member_count");
        avatar = bundle.getString("avatar");
        id = bundle.getString("id");
        type = bundle.getString("type");
        Log.e(TAG, "handleIntent: "+ type+"  "+bundle.getString("type"));
        name = bundle.getString("name");
        selfuid = bundle.getString("selfuid");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.one_on_one_chat_menu,menu);
        if(type.equals(CometChatConstants.CONVERSATION_TYPE_USER)){
            menu.getItem(2).setVisible(false);
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.call){
            cometChatCall(CometChatConstants.CALL_TYPE_AUDIO);
        }else if(item.getItemId() == R.id.video_call){
            cometChatCall(CometChatConstants .CALL_TYPE_VIDEO);
        }else if(item.getItemId() == R.id.leave){
            leaveGroup();
        }
        return true;
    }

    private void leaveGroup(){
        CometChat.leaveGroup(id, new CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                finish();
            }
            @Override
            public void onError(CometChatException e) {
                Log.e(TAG, "onError: unable to leave group "+e.getMessage());
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void cometChatCall(String callType) {
        String receiverType;
        if(type.equals(CometChatConstants.CONVERSATION_TYPE_USER)){
            receiverType = CometChatConstants.RECEIVER_TYPE_USER;
        }else {
            receiverType = CometChatConstants.RECEIVER_TYPE_GROUP;
        }
        if(id != null){
            if(!Helper.permissions(this,permission.RECORD_AUDIO) && !Helper.permissions(this,permission.CAMERA)){
                String[] permissions = {permission.RECORD_AUDIO,permission.CAMERA};
                requestPermissions(permissions,102);
            }
            CallUtils.initiateCall(this,id,receiverType,callType);
        }
    }

    private void fetchMessages(){
        viewModel.fetchMessages(id,type,30);
    }
    private void sendMessages(String message){
        TextMessage textMessage = new TextMessage(id,message,type);
        viewModel.sendMessage(textMessage);
    }
    private void setTypeIndicator(TypingIndicator typeIndicator, Boolean show){
        if(typeIndicator.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER)){
            if(id != null && id.equalsIgnoreCase(typeIndicator.getSender().getUid())){
               if(show){
                   binding.toolbarStatus.setText("Typing...");
               }else {
                   binding.toolbarStatus.setText(status);
               }
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(adapter.getItemCount() == 0){
            fetchMessages();
        }
        getCometChatListeners();
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeCometChatListeners();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.send_btn){
            String message = subinding.edtMsg.getText().toString();
            subinding.edtMsg.setText(null);
            sendMessages(message);
        }else if(id == R.id.button_attach){
            if(!Helper.permissions(this,permission.READ_EXTERNAL_STORAGE) && !Helper.permissions(this,permission.WRITE_EXTERNAL_STORAGE)){
                String[] permissions = {permission.WRITE_EXTERNAL_STORAGE,permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permissions,100);
            }else{
                attachmentSheet.show(getSupportFragmentManager(),TAG);
            }

        }else if(id == R.id.new_message){
            linearLayoutManager.smoothScrollToPosition(binding.chatscreenRecycler,null,adapter.getItemCount());
            binding.newMessage.setVisibility(View.GONE);
            isIncoming=false;
        }
    }
    private void getCometChatListeners(){
        CometChat.addUserListener(TAG, new CometChat.UserListener() {
            @Override
            public void onUserOnline(User user) {
                binding.toolbarStatus.setText(user.getStatus());
            }
            @Override
            public void onUserOffline(User user) {
                binding.toolbarStatus.setText(user.getStatus());
            }
        });
        CometChat.addMessageListener(TAG,new CometChat.MessageListener(){
            @Override
            public void onTextMessageReceived(TextMessage textMessage) {
                isIncoming = true;
                viewModel.addMessageToList(textMessage);
            }

            @Override
            public void onMediaMessageReceived(MediaMessage mediaMessage) {
                isIncoming = true;
                viewModel.addMessageToList(mediaMessage);
            }

            @Override
            public void onCustomMessageReceived(CustomMessage customMessage) {
                isIncoming = true;
                viewModel.addMessageToList(customMessage);
            }

            @Override
            public void onTypingStarted(TypingIndicator typingIndicator) {
                setTypeIndicator(typingIndicator,true);
            }

            @Override
            public void onTypingEnded(TypingIndicator typingIndicator) {
                setTypeIndicator(typingIndicator,false);
            }

            @Override
            public void onMessagesDelivered(MessageReceipt messageReceipt) {
                Log.e(TAG, "onMessagesDelivered: "+messageReceipt);
                viewModel.setMessageDelivery(messageReceipt);
            }

            @Override
            public void onMessagesRead(MessageReceipt messageReceipt) {
                viewModel.setReadReceipts(messageReceipt);
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
        CometChat.addGroupListener(TAG,new CometChat.GroupListener(){
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
    private void removeCometChatListeners(){
        CometChat.removeUserListener(TAG);
        CometChat.removeGroupListener(TAG);
        CometChat.removeMessageListener(TAG);
    }

    @Override
    public void onSheetItemClicked(int id) {
        if(id == R.id.send_image){
            createMediaMessageIntent("image/*",101);
        }
    }

    private void createMediaMessageIntent(String type,int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(type);
        startActivityForResult(intent,requestCode);
    }

    private void creteMediaMessage(File file){
        MediaMessage mediaMessage = new MediaMessage(id,file,CometChatConstants.MESSAGE_TYPE_IMAGE,type);
        viewModel.sendMessage(mediaMessage);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if(resultCode == RESULT_OK && data != null){
                Uri selectedImage = data.getData();
                Cursor cursor = getContentResolver().query(selectedImage,null,null,null);
                cursor.moveToFirst();
                int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                String file_path = cursor.getString(index);
                Log.e(TAG, "onActivityResult: "+ file_path);
                creteMediaMessage(new File(file_path));
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if(!TextUtils.isEmpty(charSequence)){
            sendTyping(true);
        }else {
            sendTyping(false);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendTyping(false);
            }
        },3000);
    }

    private void sendTyping(boolean typing){
        if(typing){
            if(type.equals(CometChatConstants.CONVERSATION_TYPE_USER)){
                CometChat.startTyping(new TypingIndicator(id,CometChatConstants.RECEIVER_TYPE_USER));
            }
        }else {
            if(type.equals(CometChatConstants.CONVERSATION_TYPE_USER)){
                CometChat.endTyping(new TypingIndicator(id,CometChatConstants.RECEIVER_TYPE_USER));
            }
        }
    }

    @Override
    public void onRefresh() {
        isRefreshed = true;
        int size = adapter.getCurrentList().size() + 30;
        viewModel.fetchMessages(id,type,size);
        binding.swipeRefresh.setRefreshing(false);
    }

}