package com.example.cometchatprojava.login_activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.example.cometchatprojava.R;
import com.example.cometchatprojava.cometChatActivity.CometChatActivity;
import com.example.cometchatprojava.databinding.FragmentLoginBinding;
import com.example.cometchatprojava.utils.Constants;

public class LoginFragment extends Fragment implements View.OnClickListener {
    private FragmentLoginBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View  view = inflater.inflate(R.layout.fragment_login, container, false);
        binding =  FragmentLoginBinding.bind(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.login.setOnClickListener(this);
        binding.createuser.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.login :
                String uid = binding.uid.getText().toString();
                login(uid);
                break;
            case R.id.createuser :
                NavController navController  = Navigation.findNavController(view);
                navController.navigate(R.id.action_loginFragment_to_createUserFragment);
                break;
        }
    }

    private void login(String uid) {
        Login.login(uid,getActivity());
    }
}