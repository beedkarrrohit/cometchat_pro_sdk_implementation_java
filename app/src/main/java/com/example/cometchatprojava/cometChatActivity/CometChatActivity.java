package com.example.cometchatprojava.cometChatActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.example.cometchatprojava.R;
import com.example.cometchatprojava.databinding.ActivityCometChatBinding;

public class CometChatActivity extends AppCompatActivity {
    private ActivityCometChatBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCometChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.conversation_fragment,
                R.id.user_fragment,
                R.id.call_fragment,
                R.id.group_fragment,
                R.id.profile_fragment
        ).build();

        NavController navController = Navigation.findNavController(this,R.id.fragment_bottom_nav);
        NavigationUI.setupActionBarWithNavController(this,navController,appBarConfiguration);
        NavigationUI.setupWithNavController(binding.bottomNavBar,navController);
    }
}