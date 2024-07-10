package com.example.personalizedinventorycontrolapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.personalizedinventorycontrolapp.R;
import com.example.personalizedinventorycontrolapp.entity.Group;

import java.util.List;

public class familyShoppingListAdapter extends RecyclerView.Adapter<familyShoppingListAdapter.ImageViewHolder>{
    private final Context context;
    private final List<Group> groupList;

    public familyShoppingListAdapter(Context context, List<Group> groupList){
        this.context = context;
        this.groupList = groupList;
    }
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shoppinglist_account_layout, parent, false);

        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String title = groupList.get(position).getgEmail() + "'s ShoppingList";
        holder.viewAccountName.setText(title);
        Glide.with(context).load(groupList.get(position).getUserIcon()).into(holder.imageView);
        if(groupList.get(position).getShoppingList().size() == 0){
            holder.viewAccountName.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.GONE);
            holder.viewUserNameLabel.setVisibility(View.GONE);
        }else{
            holder.viewAccountName.setVisibility(View.VISIBLE);
            holder.imageView.setVisibility(View.VISIBLE);
            holder.viewUserNameLabel.setVisibility(View.VISIBLE);
        }
        holder.itemShoppingListAdapter = new itemShoppingListAdapter(context.getApplicationContext(), groupList.get(position).getShoppingList());
        holder.itemShoppingListRecyclerView.setAdapter(holder.itemShoppingListAdapter);
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder{
        TextView viewAccountName, viewUserNameLabel;
        RecyclerView itemShoppingListRecyclerView;
        ImageView imageView;
        itemShoppingListAdapter itemShoppingListAdapter;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.UserIconForShoppingList);
            viewAccountName = itemView.findViewById(R.id.shoppingListUsername);
            viewUserNameLabel = itemView.findViewById(R.id.shoppingListUsernameLabel);
            itemShoppingListRecyclerView = itemView.findViewById(R.id.recyclerviewshoppinglist3);
            itemShoppingListRecyclerView.setLayoutManager(new LinearLayoutManager((itemView.getContext())));
        }
    }
}
