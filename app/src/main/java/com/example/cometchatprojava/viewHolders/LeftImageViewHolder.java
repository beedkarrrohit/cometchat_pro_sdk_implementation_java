package com.example.cometchatprojava.viewHolders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cometchat.pro.models.Attachment;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.MediaMessage;
import com.example.cometchatprojava.R;
import com.example.cometchatprojava.databinding.CcLeftImageMessageBinding;
import com.example.cometchatprojava.utils.Helper;

public class LeftImageViewHolder extends RecyclerView.ViewHolder {
    private CcLeftImageMessageBinding binding;
    public LeftImageViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = CcLeftImageMessageBinding.bind(itemView);
    }
    public static LeftImageViewHolder create(ViewGroup parent){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cc_left_image_message,parent,false);
        return new LeftImageViewHolder(view);
    }
    public void bind(BaseMessage baseMessage){
        MediaMessage mediaMessage = (MediaMessage) baseMessage;
        Attachment attachment = mediaMessage.getAttachment();
        String file = attachment.getFileUrl();
        Glide.with(binding.getRoot()).load(file).placeholder(R.drawable.ic_baseline_image_24).into(binding.leftImageMessage);
        binding.timestamp.setText(Helper.convertTimeStamp(mediaMessage.getSentAt()));
    }

}
