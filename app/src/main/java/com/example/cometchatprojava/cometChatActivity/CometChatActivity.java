package com.example.cometchatprojava.cometChatActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import android.os.Bundle;
import android.util.Log;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.models.CustomMessage;
import com.cometchat.pro.models.MediaMessage;
import com.cometchat.pro.models.TextMessage;
import com.example.cometchatprojava.R;
import com.example.cometchatprojava.cometChatActivity.fragments.ConversationFragment;
import com.example.cometchatprojava.databinding.ActivityCometChatBinding;
import com.google.android.material.badge.BadgeDrawable;

public class CometChatActivity extends AppCompatActivity {
    private ActivityCometChatBinding binding;
    private static final String TAG = "CometChatActivity";
    private static int allCount = 0;
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
    public void getAllUnreadCount(int count){
        allCount = count;
        BadgeDrawable badgeDrawable = binding.bottomNavBar.getOrCreateBadge(R.id.conversation_fragment);
        badgeDrawable.setVisible(true);
        badgeDrawable.setNumber(count);
    }

    public void removeBadge(){
            allCount = 0;
            binding.bottomNavBar.removeBadge(R.id.conversation_fragment);
    }
    private void cometChatListener(){
        CometChat.addMessageListener(TAG, new CometChat.MessageListener() {
            @Override
            public void onTextMessageReceived(TextMessage textMessage) {
                if(allCount > 0){
                    getAllUnreadCount(allCount + 1);
                }else {
                    Log.e(TAG, "onTextMessageReceived: "+allCount);
                    getAllUnreadCount(1);
                }
            }

            @Override
            public void onMediaMessageReceived(MediaMessage mediaMessage) {
                getAllUnreadCount(1);
            }

            @Override
            public void onCustomMessageReceived(CustomMessage customMessage) {
                getAllUnreadCount(1);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cometChatListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        CometChat.removeMessageListener(TAG);
    }
}