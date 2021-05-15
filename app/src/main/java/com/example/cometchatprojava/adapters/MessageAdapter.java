package com.example.cometchatprojava.adapters;

import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.models.BaseMessage;
import com.example.cometchatprojava.databinding.RightTextMessageLayoutBinding;
import com.example.cometchatprojava.viewHolders.ActionCallMessageViewHolder;
import com.example.cometchatprojava.viewHolders.LeftImageViewHolder;
import com.example.cometchatprojava.viewHolders.LeftMessageViewHolder;
import com.example.cometchatprojava.viewHolders.RightImageViewHolder;
import com.example.cometchatprojava.viewHolders.RightMessageViewHolder;

public class MessageAdapter extends ListAdapter<BaseMessage, RecyclerView.ViewHolder> {

    private static final int RIGHT_TEXT_MESSAGE = 1;
    private static final int LEFT_TEXT_MESSAGE = 2;
    private static final int RIGHT_IMAGE_MESSAGE = 3;
    private static final int LEFT_IMAGE_MESSAGE = 4;
    private static final int ACTION_MESSAGE = 5;
    private static final int CALL_MESSAGE = 6;
    private static final String TAG = "MessageAdapter";
    public MessageAdapter(@NonNull DiffUtil.ItemCallback<BaseMessage> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder messageViewHolder = null;
        switch (viewType){
            case RIGHT_TEXT_MESSAGE:
                messageViewHolder= RightMessageViewHolder.create(parent);
               break;
            case LEFT_TEXT_MESSAGE:
                messageViewHolder= LeftMessageViewHolder.create(parent);
                break;
            case ACTION_MESSAGE:
            case CALL_MESSAGE:
                messageViewHolder= ActionCallMessageViewHolder.create(parent);
                break;
            case RIGHT_IMAGE_MESSAGE:
                messageViewHolder= RightImageViewHolder.create(parent);
                break;
            case LEFT_IMAGE_MESSAGE:
                messageViewHolder= LeftImageViewHolder.create(parent);
            default:
                break;
        }
        return messageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BaseMessage baseMessage = getItem(position);
        Log.e(TAG, "onBindViewHolder: "+baseMessage);
        switch (holder.getItemViewType()){
            case RIGHT_TEXT_MESSAGE:
                RightMessageViewHolder rightMessageViewHolder = (RightMessageViewHolder) holder;
                rightMessageViewHolder.bind(baseMessage);
                break;
            case LEFT_TEXT_MESSAGE:
                LeftMessageViewHolder leftMessageViewHolder = (LeftMessageViewHolder) holder;
                leftMessageViewHolder.bind(baseMessage);
                break;
            case RIGHT_IMAGE_MESSAGE:
                RightImageViewHolder rightImageViewHolder = (RightImageViewHolder) holder;
                rightImageViewHolder.bind(baseMessage);
                break;
            case LEFT_IMAGE_MESSAGE:
                LeftImageViewHolder leftImageViewHolder = (LeftImageViewHolder) holder;
                leftImageViewHolder.bind(baseMessage);
                break;
            case ACTION_MESSAGE:
            case CALL_MESSAGE:
                ActionCallMessageViewHolder actionCallMessageViewHolder = (ActionCallMessageViewHolder) holder;
                actionCallMessageViewHolder.bind(baseMessage);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return getItemTypes(position);
    }

    private int getItemTypes(int position){
        BaseMessage baseMessage = getItem(position);
        switch (baseMessage.getCategory()){
            case CometChatConstants.CATEGORY_MESSAGE:
                switch (baseMessage.getType()){
                    case  CometChatConstants.MESSAGE_TYPE_TEXT:
                        if(baseMessage.getSender().getUid().equals(CometChat.getLoggedInUser().getUid())){
                            return RIGHT_TEXT_MESSAGE;
                        }else{
                            return LEFT_TEXT_MESSAGE;
                        }
                    case CometChatConstants.MESSAGE_TYPE_IMAGE:
                        if(baseMessage.getSender().getUid().equals(CometChat.getLoggedInUser().getUid())){
                            return RIGHT_IMAGE_MESSAGE;
                        }else{
                            return LEFT_IMAGE_MESSAGE;
                        }
                    default:
                        return -1;
                }
            case CometChatConstants.CATEGORY_CALL:
                return CALL_MESSAGE;
            case CometChatConstants.CATEGORY_ACTION:
                return ACTION_MESSAGE;
            default: return -1;
        }
    }
}
