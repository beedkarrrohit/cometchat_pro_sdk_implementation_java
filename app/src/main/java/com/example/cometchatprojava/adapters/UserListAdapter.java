package com.example.cometchatprojava.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.User;
import com.example.cometchatprojava.R;
import com.example.cometchatprojava.databinding.RecyclerItemRowBinding;

public class UserListAdapter extends ListAdapter<User, UserListAdapter.UserListViewHolder> {
    private ItemClickListener itemClickListener;
    public UserListAdapter(@NonNull DiffUtil.ItemCallback<User> diffCallback, ItemClickListener itemClickListener) {
        super(diffCallback);
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_row,parent,false);
        return new UserListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserListViewHolder holder, int position) {
        User user = getItem(position);
        holder.setBinding(user,itemClickListener);
    }

     class UserListViewHolder extends RecyclerView.ViewHolder{
        private RecyclerItemRowBinding binding;
        public UserListViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RecyclerItemRowBinding.bind(itemView);
        }
        void setBinding(User user,ItemClickListener itemClickListener){
            binding.usersName.setText(user.getName());
            binding.status.setText(user.getStatus());
            Glide.with(binding.getRoot()).load(user.getAvatar()).placeholder(R.drawable.user).into(binding.avatar);
            binding.itemRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickListener.onItemClick(getAdapterPosition());
                }
            });
        }
    }
}
