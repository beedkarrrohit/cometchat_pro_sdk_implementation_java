package com.example.cometchatprojava.cometChatActivity.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.example.cometchatprojava.R;
import com.example.cometchatprojava.databinding.CustomAlertDialogUserUpdateBinding;
import com.example.cometchatprojava.databinding.FragmentProfileBinding;
import com.example.cometchatprojava.databinding.ProfileInfoBinding;
import com.example.cometchatprojava.login_activity.LoginActivity;


public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private ProfileInfoBinding subinding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        binding = FragmentProfileBinding.bind(view);
        subinding = ProfileInfoBinding.bind(binding.getRoot());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        User user =  CometChat.getLoggedInUser();
        super.onViewCreated(view, savedInstanceState);
        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CometChat.logout(new CometChat.CallbackListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Toast.makeText(getContext(), "Logged out Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        getActivity().finish();
                    }

                    @Override
                    public void onError(CometChatException e) {

                    }
                });
            }
        });
        subinding.username.setText(user.getName());
        subinding.status.setText(user.getStatus());
        subinding.profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile(user.getAvatar());
            }
        });
        Glide.with(this).load(user.getAvatar()).placeholder(R.drawable.user).into(subinding.avatar);
    }

    private void updateProfile(String avatar) {
        CustomAlertDialogUserUpdateBinding dialogBinding =
        CustomAlertDialogUserUpdateBinding.bind(LayoutInflater.from(getActivity()).inflate(R.layout.custom_alert_dialog_user_update,null));
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity(),R.style.CustomAlertDialog).setView(dialogBinding.getRoot()).create();
        alertDialog.show();
        Glide.with(dialogBinding.getRoot()).load(avatar).placeholder(R.drawable.user).into(dialogBinding.userAvatar);
        dialogBinding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        dialogBinding.setName.setOnClickListener(view -> {
            String name = dialogBinding.setNameEdt.getText().toString();
            setName(name);
            alertDialog.dismiss();
        });
    }
    private void setName(String name){
        User user = CometChat.getLoggedInUser();
        user.setName(name);
        CometChat.updateCurrentUserDetails(user, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                subinding.username.setText(CometChat.getLoggedInUser().getName());
            }
            @Override
            public void onError(CometChatException e) {

            }
        });
    }
}