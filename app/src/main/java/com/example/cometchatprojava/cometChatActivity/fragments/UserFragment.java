package com.example.cometchatprojava.cometChatActivity.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.UsersRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.User;
import com.example.cometchatprojava.R;
import com.example.cometchatprojava.adapters.ItemClickListener;
import com.example.cometchatprojava.adapters.UserListAdapter;
import com.example.cometchatprojava.chatActivity.ChatScreenActivity;
import com.example.cometchatprojava.databinding.FragmentUserBinding;
import com.example.cometchatprojava.databinding.RecyclerBinding;
import com.example.cometchatprojava.databinding.RecyclerItemRowBinding;
import com.example.cometchatprojava.databinding.SearchLayoutBinding;
import com.example.cometchatprojava.diffUtil.DiffUtils;

import java.util.ArrayList;
import java.util.List;


public class UserFragment extends Fragment implements ItemClickListener, TextWatcher {
    private FragmentUserBinding binding;
    private RecyclerBinding subbinding;
    private SearchLayoutBinding searchBinding;
    private UserListAdapter adapter;
    private static String uid;
    private static final String TAG = "UserFragment";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserBinding.bind(inflater.inflate(R.layout.fragment_user, container, false));
        subbinding =RecyclerBinding.bind(binding.getRoot());
        searchBinding = SearchLayoutBinding.bind(binding.getRoot());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new UserListAdapter(new DiffUtils<User>(),this);
        subbinding.recycler.setAdapter(adapter);
        uid = CometChat.getLoggedInUser().getUid();
        searchBinding.search.addTextChangedListener(this);

    }

    private void getUsersList() {
        UsersRequest usersRequest = new UsersRequest.UsersRequestBuilder().setLimit(30).build();
        usersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                if(users != null){
                    adapter.submitList(users);
                }
            }
            @Override
            public void onError(CometChatException e) {
                Toast.makeText(getContext(), "Failed to get Users", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        User user = adapter.getCurrentList().get(position);
        Intent intent = new Intent(getActivity(), ChatScreenActivity.class);
        intent.putExtras(createIntentBundle(user));
        startActivity(intent);
    }

    private Bundle createIntentBundle(User user){
        Bundle bundle = new Bundle();
        bundle.putString("name",user.getName());
        bundle.putString("id",user.getUid());
        bundle.putString("avatar",user.getAvatar());
        bundle.putString("status",user.getStatus());
        bundle.putString("selfuid",uid);
        bundle.putString("type", CometChatConstants.CONVERSATION_TYPE_USER);
        return bundle;
    }

    @Override
    public void onResume() {
        super.onResume();
        getUsersList();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if(adapter != null && adapter.getCurrentList().size() >0){
            if(charSequence.length() > 0){
                String name = charSequence.toString();
                List<User> searchList = new ArrayList<>();
                if(charSequence.length() < 3){
                    for(User user : adapter.getCurrentList()){
                        if(user.getName().toLowerCase().contains(name.toLowerCase())){
                            searchList.add(user);
                        }
                    }
                    if(searchList.size()>0){
                        Log.e(TAG, "onTextChanged: no empty called");
                        adapter.submitList(searchList);
                        adapter.notifyDataSetChanged();
                    }

                }else{
                    Log.e(TAG, "onTextChanged: else called");
                    UsersRequest usersRequest = new UsersRequest.UsersRequestBuilder().setSearchKeyword(name).build();
                    usersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
                        @Override
                        public void onSuccess(List<User> list) {
                            Log.e(TAG, "onTextChanged: else called");
                            if(list.size() > 0){
                                adapter.submitList(list);
                            }

                        }

                        @Override
                        public void onError(CometChatException e) {
                            Log.e(TAG, "onError: "+e.getMessage());
                        }
                    });
                }

            }else {
                Log.e(TAG, "onTextChanged: called");
                getUsersList();
            }
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

}