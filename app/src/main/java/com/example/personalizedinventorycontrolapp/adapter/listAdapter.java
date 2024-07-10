package com.example.personalizedinventorycontrolapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.personalizedinventorycontrolapp.R;
import com.example.personalizedinventorycontrolapp.entity.Group;

import java.util.List;

public class listAdapter extends RecyclerView.Adapter<listAdapter.ImageViewHolder>{
    private Context context;
    private final List<Group> familyAccountList;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public interface OnItemClickListener{
        void onItemClick(View view,int position);
    }
    public interface OnItemLongClickListener{
        void onItemLongClick(View view,int position);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

    public listAdapter(Context context, List<Group> familyAccountList) {
        this.context = context;
        this.familyAccountList = familyAccountList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_family_account_layout, parent, false);

        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.viewLinkedAccountEmail.setText(familyAccountList.get(position).getgEmail());
        holder.viewFamilyAccountUsername.setText(familyAccountList.get(position).getUsername());
        Glide.with(context).load(familyAccountList.get(position).getUserIcon()).into(holder.imageView);
        if(mOnItemClickListener != null){
            holder.itemView.setOnClickListener(v -> {
                int position1 = holder.getLayoutPosition();
                mOnItemClickListener.onItemClick(holder.itemView, position1);
            });
        }
        if(mOnItemLongClickListener != null){
            holder.itemView.setOnLongClickListener(v -> {
                int position12 = holder.getLayoutPosition();
                mOnItemLongClickListener.onItemLongClick(holder.itemView, position12);
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return familyAccountList.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        TextView viewLinkedAccountEmail, viewFamilyAccountUsername;
        ImageView imageView;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.UserIcon);
            viewLinkedAccountEmail = itemView.findViewById(R.id.LinkedAccountEmail);
            viewFamilyAccountUsername = itemView.findViewById(R.id.familyAccountUsername);
        }

    }


}
