package com.example.cometchatprojava.cometChatActivity.fragments;

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
import com.example.cometchatprojava.databinding.FragmentProfileBinding;
import com.example.cometchatprojava.login_activity.LoginActivity;


public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        binding = FragmentProfileBinding.bind(view);
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
        binding.layout.username.setText(user.getName());
        binding.layout.status.setText(user.getStatus());
        Glide.with(this).load(user.getAvatar()).placeholder(R.drawable.user).into(binding.layout.avatar);
    }
}