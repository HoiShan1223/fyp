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
import com.example.personalizedinventorycontrolapp.entity.Item;

import java.util.List;

public class gridAdapter extends RecyclerView.Adapter<gridAdapter.ImageViewHolder> {

    private final Context context;
    private final List<Item> itemList;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public interface OnItemClickListener{
        void onItemClick(View view,int position);
    }
    public interface OnItemLongClickListener{
        void onItemLongClick(View view,int position);
    }
    public gridAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }
    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_layout, parent, false);

        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Glide.with(context).load(itemList.get(position).getItemImage_path()).into(holder.imageView);
        holder.viewitemname.setText(itemList.get(position).getItemName());
        holder.viewitemquantity.setText(String.valueOf(itemList.get(position).getQuantity()));
        holder.viewitemusage.setText(String.valueOf(itemList.get(position).getUsage()));
        holder.viewitemusagetype.setText(itemList.get(position).getUsageType());
        if(mOnItemClickListener != null){
            holder.itemView.setOnClickListener(v -> {
                int position12 = holder.getLayoutPosition();
                mOnItemClickListener.onItemClick(holder.itemView, position12);
            });
        }
        if(mOnItemLongClickListener != null){
            holder.itemView.setOnLongClickListener(v -> {
                int position1 = holder.getLayoutPosition();
                mOnItemLongClickListener.onItemLongClick(holder.itemView, position1);
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder{
        TextView viewitemname, viewitemquantity, viewitemusage, viewitemusagetype;
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.itemImage);
            viewitemname = itemView.findViewById(R.id.productNameList);
            viewitemquantity = itemView.findViewById(R.id.productQuantityList);
            viewitemusage = itemView.findViewById(R.id.productUsageList);
            viewitemusagetype = itemView.findViewById(R.id.productUsageTypeList);
        }
    }
}