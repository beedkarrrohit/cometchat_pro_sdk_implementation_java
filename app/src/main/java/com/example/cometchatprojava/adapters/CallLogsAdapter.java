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
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.User;
import com.example.cometchatprojava.R;
import com.example.cometchatprojava.databinding.RecyclerItemRowBinding;
import com.example.cometchatprojava.utils.Helper;

public class CallLogsAdapter extends ListAdapter<BaseMessage, CallLogsAdapter.CallLogViewHolder> {
    private static final String TAG = "CallLogsAdapter";
    ItemClickListener itemClickListener;
    public CallLogsAdapter(@NonNull DiffUtil.ItemCallback<BaseMessage> diffCallback,ItemClickListener itemClickListener) {
        super(diffCallback);
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public CallLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CallLogViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CallLogViewHolder holder, int position) {
        BaseMessage baseMessage = getItem(position);
        holder.bind(baseMessage,itemClickListener);
    }

     class CallLogViewHolder extends RecyclerView.ViewHolder{
        private final RecyclerItemRowBinding binding;
        public CallLogViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RecyclerItemRowBinding.bind(itemView);
        }
        public void bind(BaseMessage baseMessage,ItemClickListener itemClickListener){
            Call call =(Call) baseMessage;
            Log.e(TAG, "bind: "+call);
            String name = null;
            String avatar=null;
            String message =null;
            boolean incoming;
            boolean missed = false;
            boolean videoCall;
            if(call.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER)){
                if(((User) call.getCallInitiator()).getUid().equals(CometChat.getLoggedInUser().getUid())){
                    //Log.e(TAG, "bindIn: "+call.getSessionId()+" "+((User) call.getReceiver()).getAvatar());
                    name = ((User) call.getCallReceiver()).getName();
                    avatar = ((User) call.getCallReceiver()).getAvatar();
                    if(call.getCallStatus().equals(CometChatConstants.CALL_STATUS_UNANSWERED)||call.getCallStatus().equals(CometChatConstants.CALL_STATUS_CANCELLED)){
                        message = "missed";
                        missed = true;
                    }else if (call.getCallStatus().equals(CometChatConstants.CALL_STATUS_REJECTED)){
                        message = "Rejected";
                        missed = true;
                    }else{
                        message = "outgoing";
                    }
                    incoming = false;
                }else{
                    name =((User) call.getCallInitiator()).getName();
                    avatar = ((User) call.getCallInitiator()).getAvatar();
                    if(call.getCallStatus().equals(CometChatConstants.CALL_STATUS_UNANSWERED)||call.getCallStatus().equals(CometChatConstants.CALL_STATUS_CANCELLED)){
                        message = "missed";
                        missed = true;
                    }else if (call.getCallStatus().equals(CometChatConstants.CALL_STATUS_REJECTED)){
                        message = "Rejected";
                        missed = true;
                    }else{
                        message = "incoming";
                    }
                    incoming = true;
                }
            }else{
                name = ((Group) call.getReceiver()).getName();
                avatar=((Group) call.getReceiver()).getIcon();

                if(((User) call.getCallInitiator()).getUid().equals(CometChat.getLoggedInUser().getUid())){
                    if(call.getCallStatus().equals(CometChatConstants.CALL_STATUS_UNANSWERED)||call.getCallStatus().equals(CometChatConstants.CALL_STATUS_CANCELLED)){
                        message = "missed";
                        missed = true;
                    }else if (call.getCallStatus().equals(CometChatConstants.CALL_STATUS_REJECTED)){
                        message = "Rejected";
                        missed = true;
                    }else{
                        message = "outgoing";
                    }
                    incoming = false;
                }else {
                    if(call.getCallStatus().equals(CometChatConstants.CALL_STATUS_UNANSWERED)||call.getCallStatus().equals(CometChatConstants.CALL_STATUS_CANCELLED)){
                        message = "missed";
                        missed = true;
                    }else if (call.getCallStatus().equals(CometChatConstants.CALL_STATUS_REJECTED)){
                        message = "Rejected";
                        missed = true;
                    }else{
                        message = "incoming";
                    }
                    incoming = true;
                }
            }
            if(call.getType().equals(CometChatConstants.CALL_TYPE_VIDEO)){
                message = message + " video call";
                videoCall = true;
            }else{
                message = message + " audio call";
                videoCall = false;
            }
            binding.usersName.setText(name);
            binding.status.setText(message);
            binding.callTime.setVisibility(View.VISIBLE);
            binding.callTime.setText(Helper.getTimeDate(call.getInitiatedAt()));
            //Log.e(TAG, "bind: "+call.getSessionId()+" "+avatar);
            Glide.with(binding.getRoot()).load(avatar).placeholder(R.drawable.user).into(binding.avatar);
            if(videoCall){
                binding.status.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.vidcam_drawable,0,0,0);
            }else{
                if(incoming && missed){
                    binding.status.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_call_missed_white_24dp,0,0,0);
                }else if(incoming && !missed){
                    binding.status.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_call_received_white_24dp,0,0,0);
                }else if(!incoming && !missed){
                    binding.status.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_call_made_white_24dp,0,0,0);
                }else if(!incoming && missed){
                    binding.status.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_call_missed_outgoing_white_24dp,0,0,0);
                }
            }
            binding.status.setCompoundDrawablePadding(10);
            binding.callButton.setVisibility(View.VISIBLE);
            binding.callButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickListener.onItemClick(getAdapterPosition());
                }
            });
        }
    }
}
