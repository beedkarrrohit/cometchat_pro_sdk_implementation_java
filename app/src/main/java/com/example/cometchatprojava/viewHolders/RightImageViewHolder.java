package com.example.cometchatprojava.viewHolders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.models.Attachment;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.MediaMessage;
import com.example.cometchatprojava.R;
import com.example.cometchatprojava.databinding.CcRightImageMessageBinding;
import com.example.cometchatprojava.utils.Helper;

public class RightImageViewHolder extends RecyclerView.ViewHolder {
    private final CcRightImageMessageBinding binding;
    public RightImageViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = CcRightImageMessageBinding.bind(itemView);
    }
    public static  RightImageViewHolder create(ViewGroup parent){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cc_right_image_message,parent,false);
        return new RightImageViewHolder(view);
    }
    public void bind(BaseMessage baseMessage){
        MediaMessage mediaMessage = (MediaMessage) baseMessage;
        Attachment attachment = mediaMessage.getAttachment();
        String file = attachment.getFileUrl();
        Glide.with(binding.getRoot()).load(file).placeholder(R.drawable.ic_baseline_image_24).into(binding.rightImageMessage);
        binding.timestamp.setText(Helper.convertTimeStamp(baseMessage.getSentAt()));
        setReadReceipts(mediaMessage);
    }

    private void setReadReceipts(BaseMessage baseMessage) {
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
