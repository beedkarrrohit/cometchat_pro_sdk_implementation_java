package com.example.cometchatprojava.cometChatActivity.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.GroupsRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Group;
import com.example.cometchatprojava.R;
import com.example.cometchatprojava.adapters.GroupListAdapter;
import com.example.cometchatprojava.adapters.ItemClickListener;
import com.example.cometchatprojava.chatActivity.ChatScreenActivity;
import com.example.cometchatprojava.databinding.FragmentGroupBinding;
import com.example.cometchatprojava.databinding.RecyclerBinding;
import com.example.cometchatprojava.databinding.RecyclerItemRowBinding;
import com.example.cometchatprojava.diffUtil.DiffUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;


public class GroupFragment extends Fragment implements ItemClickListener {
    private FragmentGroupBinding binding;
    private RecyclerBinding subbinding;
    private GroupListAdapter adapter;
    private static String uid;
    private static final String TAG = "GroupFragment";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGroupBinding.bind(inflater.inflate(R.layout.fragment_group, container, false));
        subbinding = RecyclerBinding.bind(binding.getRoot());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new GroupListAdapter(new DiffUtils<Group>(),this);
        subbinding.recycler.setAdapter(adapter);
        uid = CometChat.getLoggedInUser().getUid();
    }

    private void getAllGroups() {
        GroupsRequest groupsRequest = new GroupsRequest.GroupsRequestBuilder().setLimit(30).build();
        groupsRequest.fetchNext(new CometChat.CallbackListener<List<Group>>() {
            @Override
            public void onSuccess(List<Group> groups) {
                if(groups != null){
                    Log.e(TAG, "onSuccess: "+groups);
                    adapter.submitList(groups);
                }
            }

            @Override
            public void onError(CometChatException e) {
                Toast.makeText(getContext(), "Error While fetching Groups", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Group group = adapter.getCurrentList().get(position);
        if(group.isJoined()){
            Intent intent = new Intent(getActivity(), ChatScreenActivity.class);
            intent.putExtras(createIntentBundle(group));
            startActivity(intent);
        }else{
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity())
                    .setMessage("Do you Want to Join this Group")
                    .setCancelable(false)
                    .setPositiveButton("Join", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            joinGroup(group);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.setTitle("Join Group");
            alertDialog.show();
        }

    }

    private Bundle createIntentBundle(Group group){
        Bundle bundle = new Bundle();
        bundle.putString("name",group.getName());
        bundle.putString("id",group.getGuid());
        bundle.putString("avatar",group.getIcon());
        bundle.putInt("member_counts",group.getMembersCount());
        bundle.putString("selfuid",uid);
        bundle.putString("type", CometChatConstants.CONVERSATION_TYPE_GROUP);
        return bundle;
    }

    private void joinGroup(Group group){
        binding.groupProgress.setVisibility(View.VISIBLE);
        String guid = group.getGuid();
        String groupType = group.getGroupType();
        String password="";
        if(group.getPassword() != null){
            password = password.concat(group.getPassword());
        }
        CometChat.joinGroup(guid, groupType, password, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group group) {
                binding.groupProgress.setVisibility(View.GONE);
                Intent intent = new Intent(getActivity(),ChatScreenActivity.class);
                intent.putExtras(createIntentBundle(group));
                startActivity(intent);
            }
            @Override
            public void onError(CometChatException e) {
                Toast.makeText(getContext(), "Cannot Join the Group", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getAllGroups();
    }
}