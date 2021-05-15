package com.example.cometchatprojava.viewHolders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.TextMessage;
import com.example.cometchatprojava.R;
import com.example.cometchatprojava.databinding.LeftTextMessageLayoutBinding;
import com.example.cometchatprojava.utils.Helper;

public class LeftMessageViewHolder extends RecyclerView.ViewHolder {
    private LeftTextMessageLayoutBinding binding;
    public LeftMessageViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = LeftTextMessageLayoutBinding.bind(itemView);
    }

    public static LeftMessageViewHolder create(ViewGroup parent){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.left_text_message_layout,parent,false);
        return new LeftMessageViewHolder(v);
    }

    public void bind(BaseMessage baseMessage){
        TextMessage textMessage = (TextMessage) baseMessage;
        binding.leftMessage.setText(textMessage.getText());
        binding.timestamp.setText(Helper.convertTimeStamp(textMessage.getSentAt()));
    }
}
