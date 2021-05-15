package com.example.cometchatprojava.login_activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.example.cometchatprojava.R;
import com.example.cometchatprojava.databinding.FragmentCreateUserBinding;
import com.example.cometchatprojava.utils.Constants;

public class CreateUserFragment extends Fragment implements View.OnClickListener {
    private FragmentCreateUserBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_user, container, false);
        binding = FragmentCreateUserBinding.bind(view);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.createNewUser.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.create_new_user){
            String userid = binding.edtUid.getText().toString();
            String username= binding.edtName.getText().toString();
            createUser(userid,username);
        }
    }

    private void createUser(String userid,String username) {
        User user = new User();
        user.setUid(userid);
        user.setName(username);
        CometChat.createUser(user, Constants.auth_key, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                if(user != null){
                    Login.login(user.getUid(),getActivity());
                }
            }

            @Override
            public void onError(CometChatException e) {

            }
        });
    }
}