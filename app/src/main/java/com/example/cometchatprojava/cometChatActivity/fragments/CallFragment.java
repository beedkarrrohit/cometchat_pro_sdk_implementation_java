package com.example.cometchatprojava.cometChatActivity.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.Call;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.MessagesRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.User;
import com.example.cometchatprojava.R;
import com.example.cometchatprojava.adapters.CallLogsAdapter;
import com.example.cometchatprojava.adapters.ItemClickListener;
import com.example.cometchatprojava.databinding.CallAlertDialogLayoutBinding;
import com.example.cometchatprojava.databinding.FragmentCallBinding;
import com.example.cometchatprojava.diffUtil.DiffUtils;
import com.example.cometchatprojava.utils.CallUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CallFragment extends Fragment implements ItemClickListener {
    private FragmentCallBinding binding;
    private CallLogsAdapter adapter;
    private static final String TAG = "CallFragment";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_call, container, false);
        binding =FragmentCallBinding.bind(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new CallLogsAdapter(new DiffUtils<>(),this);
        binding.callRecycler.setAdapter(adapter);
        fetchCallLogs();
    }

    private void fetchCallLogs() {
        List<String> list = new ArrayList<>();
        list.add(CometChatConstants.CATEGORY_CALL);
        MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder().setCategories(list).setLimit(30).build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> baseMessages) {
                List<BaseMessage> callList = new ArrayList<>();
                for(BaseMessage baseMessage : baseMessages){
                    if (baseMessage instanceof Call){
                        callList.add(baseMessage);
                    }
                }
                Collections.reverse(baseMessages);
                adapter.submitList(sortList(baseMessages));
                //adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(CometChatException e) {

            }
        });
    }

    private List<BaseMessage> sortList(List<BaseMessage> list){
        List<BaseMessage> sortedList = new ArrayList<>();
        for(BaseMessage baseMessage : list){
            Call call = (Call) baseMessage;
            String status = call.getCallStatus();
            if(status.equals(CometChatConstants.CALL_STATUS_REJECTED)
                    ||status.equals(CometChatConstants.CALL_STATUS_ENDED)
                    ||status.equals(CometChatConstants.CALL_STATUS_UNANSWERED)
                    ||status.equals(CometChatConstants.CALL_STATUS_CANCELLED)){
                sortedList.add(baseMessage);
            }
        }
        return sortedList;
    }

    @Override
    public void onItemClick(int position) {
        BaseMessage baseMessage = adapter.getCurrentList().get(position);
        Call call = (Call) baseMessage;
        String avatar=null;
        if(call.getCallReceiver() instanceof User){
            avatar = ((User) call.getCallReceiver()).getAvatar();
        }else{
            avatar = ((Group)call.getCallReceiver()).getIcon();
        }
        /*Log.e(TAG, "onItemClick: "+baseMessage);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setMessage("Choose Action")
                .setCancelable(true)
                .setPositiveButton("Audio Call", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        cometChatCall(call,CometChatConstants.CALL_TYPE_AUDIO);
                    }
                }).setNegativeButton("Video Call", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        cometChatCall(call,CometChatConstants.CALL_TYPE_VIDEO);
                    }
                });*/
        Log.e(TAG, "onItemClick: "+avatar);
        LayoutInflater inflater =LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.call_alert_dialog_layout,null);
        CallAlertDialogLayoutBinding binding1 = CallAlertDialogLayoutBinding.bind(view);
        Glide.with(binding1.getRoot()).load(avatar).placeholder(R.drawable.user).into(binding1.avatar);
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).setView(view).create();
        alertDialog.show();
        binding1.audioCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cometChatCall(call,CometChatConstants.CALL_TYPE_AUDIO);
                alertDialog.dismiss();
            }
        });
        binding1.videoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cometChatCall(call,CometChatConstants.CALL_TYPE_VIDEO);
                alertDialog.dismiss();
            }
        });
    }

    private void cometChatCall(Call call,String type){
        Log.e(TAG, "cometChatCall: "+call);
        if(call.getCallReceiver() instanceof User){
            User user = (User) call.getCallReceiver();
            Log.e(TAG, "cometChatCall: "+user);
            String id = user.getUid();
            CallUtils.initiateCall(getActivity(),id,CometChatConstants.RECEIVER_TYPE_USER,type);
        }else {
            Group group = (Group) call.getCallReceiver();
            String id = group.getGuid();
            CallUtils.initiateCall(getActivity(),id,CometChatConstants.RECEIVER_TYPE_GROUP,type);
        }
    }
}