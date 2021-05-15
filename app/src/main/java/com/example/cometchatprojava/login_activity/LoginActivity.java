package com.example.cometchatprojava.login_activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.example.cometchatprojava.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        NavController navController = Navigation.findNavController(this,R.id.login_fragment_host);
        NavigationUI.setupActionBarWithNavController(this,navController);
    }
}