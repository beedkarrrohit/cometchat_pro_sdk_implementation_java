package com.example.cometchatprojava.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.Call;
import com.cometchat.pro.models.Action;
import com.cometchat.pro.models.Conversation;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.models.User;
import com.example.cometchatprojava.R;
import com.example.cometchatprojava.databinding.RecyclerItemRowBinding;
import com.example.cometchatprojava.utils.Helper;

public class ConversationListAdapter extends ListAdapter<Conversation, ConversationListAdapter.ConversationViewHolder> {
    private static final String TAG = "ConversationListAdapter";
    private final ItemClickListener itemClickListener;
    public ConversationListAdapter(@NonNull DiffUtil.ItemCallback<Conversation> diffCallback, ItemClickListener itemClickListener) {
        super(diffCallback);
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_row,parent,false);
        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        Conversation conversation = getItem(position);
        Log.e(TAG, "onBindViewHolder: "+ conversation.getUnreadMessageCount());
        holder.setBinding(conversation,itemClickListener);
    }

    public static class ConversationViewHolder extends RecyclerView.ViewHolder{
        private final RecyclerItemRowBinding binding;
        private static final String TAG = "ConversationViewHolder";
        public ConversationViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RecyclerItemRowBinding.bind(itemView);
        }
        void setBinding(Conversation conversation,ItemClickListener itemClickListener){
            Log.e(TAG, "setBinding: "+ conversation);
            if(conversation.getConversationType().equals(CometChatConstants.CONVERSATION_TYPE_USER)){
                User user =(User) conversation.getConversationWith();
                binding.usersName.setText(user.getName());
                Glide.with(binding.getRoot()).load(user.getAvatar()).placeholder(R.drawable.user).into(binding.avatar);
                if(conversation.getLastMessage() != null){
                    switch(conversation.getLastMessage().getCategory()){
                        case CometChatConstants.CATEGORY_MESSAGE:
                            switch (conversation.getLastMessage().getType()){
                                case CometChatConstants.MESSAGE_TYPE_TEXT:
                                    TextMessage textMessage = (TextMessage) conversation.getLastMessage();
                                    binding.status.setText(textMessage.getText());
                                    break;
                                case CometChatConstants.MESSAGE_TYPE_IMAGE:
                                    binding.status.setText("Image");
                                    binding.status.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_image_24,0,0,0);
                                    binding.status.setCompoundDrawablePadding(10);
                                    break;
                            }
                            break;
                        case CometChatConstants.CATEGORY_CALL:
                            Call call = (Call) conversation.getLastMessage();
                            binding.status.setText(call.getCallStatus());
                            break;
                    }
                }else{
                    binding.status.setText("Tap to start Conversation");
                }
            }else{
                Group group = (Group) conversation.getConversationWith();
                binding.usersName.setText(group.getName());
                Glide.with(binding.getRoot()).load(group.getIcon()).placeholder(R.drawable.user).into(binding.avatar);
                if(conversation.getLastMessage() != null){
                    switch (conversation.getLastMessage().getCategory()){
                        case CometChatConstants.CATEGORY_MESSAGE:
                            switch(conversation.getLastMessage().getType()){
                                case CometChatConstants.MESSAGE_TYPE_TEXT :
                                    TextMessage textMessage = (TextMessage) conversation.getLastMessage();
                                    binding.status.setText(textMessage.getText());
                                    break;
                                case CometChatConstants.MESSAGE_TYPE_IMAGE:
                                    binding.status.setText("Image");
                                    binding.status.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_image_24,0,0,0);
                                    binding.status.setCompoundDrawablePadding(10);
                            }
                            break;
                        case  CometChatConstants.CATEGORY_CALL:
                            Call call = (Call) conversation.getLastMessage();
                            binding.status.setText(call.getCallStatus());
                            break;
                        case CometChatConstants.CATEGORY_ACTION:
                            Action action = (Action) conversation.getLastMessage();
                            binding.status.setText(action.getMessage());
                    }
                }else{
                    binding.status.setText("Tap to start conversation");
                }
            }
            binding.messageTime.setVisibility(View.VISIBLE);
            binding.messageTime.setText(Helper.getTimeDate(conversation.getLastMessage().getSentAt()));
            if(conversation.getUnreadMessageCount() > 0 ){
                Log.e(TAG, "setBinding: "+conversation.getUnreadMessageCount());
                binding.messageCount.setVisibility(View.VISIBLE);
                String count = String.valueOf(conversation.getUnreadMessageCount());
                binding.messageCount.setText(count);
            }else{
                binding.messageCount.setVisibility(View.GONE);
            }
            binding.itemRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickListener.onItemClick(getAdapterPosition());
                }
            });
        }

    }

}
