package com.example.cometchatprojava.viewHolders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.Call;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.models.Action;
import com.cometchat.pro.models.BaseMessage;
import com.example.cometchatprojava.R;
import com.example.cometchatprojava.databinding.ActionItemRowBinding;

public class ActionCallMessageViewHolder extends RecyclerView.ViewHolder {
    private ActionItemRowBinding binding;
    public ActionCallMessageViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = ActionItemRowBinding.bind(itemView);
    }
    public static ActionCallMessageViewHolder create(ViewGroup viewGroup){
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.action_item_row,viewGroup,false);
        return new ActionCallMessageViewHolder(view);
    }
    public void bind(BaseMessage baseMessage){
        if (baseMessage.getCategory().equals(CometChatConstants.CATEGORY_ACTION)){
            Action action = (Action) baseMessage;
            binding.actionMesssage.setText(action.getMessage());
        }else {
            Call call = (Call) baseMessage;
            String message;
            String status = call.getCallStatus();
            String callType = call.getType();
            String sender;
            if(call.getSender().getUid().equals(CometChat.getLoggedInUser().getUid())){
                sender = "You";
            }else{
                sender = call.getSender().getName();
            }
            if(status.equals(CometChatConstants.CALL_STATUS_INITIATED) || status.equals(CometChatConstants.CALL_STATUS_CANCELLED)
            || status.equals(CometChatConstants.CALL_STATUS_REJECTED)){
                message = sender +" "+ status +" "+callType+" call";
            }else if(status.equals(CometChatConstants.CALL_STATUS_BUSY)){
                message = sender +" is busy";
            }else if(status.equals(CometChatConstants.CALL_STATUS_ONGOING) || status.equals(CometChatConstants.CALL_STATUS_ENDED)){
                message = status + " "+ callType+" call";
            }
            else{
                message = status;
            }

            binding.actionMesssage.setText(message);
        }
    }
}
