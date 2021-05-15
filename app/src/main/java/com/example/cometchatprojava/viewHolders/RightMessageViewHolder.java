package com.example.cometchatprojava.viewHolders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.TextMessage;
import com.example.cometchatprojava.R;
import com.example.cometchatprojava.databinding.RightTextMessageLayoutBinding;
import com.example.cometchatprojava.utils.Helper;

public class RightMessageViewHolder extends RecyclerView.ViewHolder {
    private RightTextMessageLayoutBinding binding;
    public RightMessageViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = RightTextMessageLayoutBinding.bind(itemView);
    }
    public static RightMessageViewHolder create(ViewGroup parent){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.right_text_message_layout,parent,false);
        return  new RightMessageViewHolder(view);
    }

    public void bind(BaseMessage baseMessage){
        TextMessage textMessage = (TextMessage) baseMessage;
        binding.timestamp.setText(Helper.convertTimeStamp(textMessage.getSentAt()));
        binding.rightMessage.setText(textMessage.getText());
        setReadReciepts(textMessage);
    }

    private void setReadReciepts(BaseMessage baseMessage) {
        if(baseMessage.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER)){
            if(baseMessage.getReadAt() != 0L)
            {
                binding.timestamp.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.message_seen_blue_tick,0,0,0);
                binding.timestamp.setCompoundDrawablePadding(10);
            }else if(baseMessage.getDeliveredAt()!=0L){
                binding.timestamp.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.message_delivered_double_tick,0,0,0);
                binding.timestamp.setCompoundDrawablePadding(10);
            }else {
                binding.timestamp.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.message_sent_single_tick,0,0,0);
                binding.timestamp.setCompoundDrawablePadding(10);
            }
        }
    }
}
