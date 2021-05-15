package com.example.cometchatprojava.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cometchat.pro.models.Group;
import com.example.cometchatprojava.R;
import com.example.cometchatprojava.databinding.RecyclerItemRowBinding;

public class GroupListAdapter extends ListAdapter<Group, GroupListAdapter.GroupListViewHolder> {
    private ItemClickListener itemClickListener;
    public GroupListAdapter(@NonNull DiffUtil.ItemCallback<Group> diffCallback,ItemClickListener itemClickListener) {
        super(diffCallback);
        this.itemClickListener = itemClickListener;
    }
    @NonNull
    @Override
    public GroupListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_row,parent,false);
        return new GroupListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupListViewHolder holder, int position) {
        Group group = getItem(position);
        holder.bind(group,itemClickListener);
    }

    class GroupListViewHolder extends RecyclerView.ViewHolder{
        private RecyclerItemRowBinding binding;
        public GroupListViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RecyclerItemRowBinding.bind(itemView);
        }
        void bind(Group group,ItemClickListener itemClickListener){
            binding.usersName.setText(group.getName());
            binding.status.setText("Members: "+group.getMembersCount());
            Glide.with(binding.getRoot()).load(group.getIcon()).placeholder(R.drawable.user).into(binding.avatar);
            binding.itemRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickListener.onItemClick(getAdapterPosition());
                }
            });
        }
    }
}
