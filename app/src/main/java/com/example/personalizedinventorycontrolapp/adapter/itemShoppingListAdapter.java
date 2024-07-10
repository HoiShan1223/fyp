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
import com.example.personalizedinventorycontrolapp.entity.ShoppingList;

import java.util.List;

public class itemShoppingListAdapter extends RecyclerView.Adapter<itemShoppingListAdapter.ImageViewHolder>{
    private final List<ShoppingList> familyAccountShoppingList;
    private final Context context;

    public itemShoppingListAdapter(Context context, List<ShoppingList> familyAccountShoppingList) {
        this.context = context;
        this.familyAccountShoppingList = familyAccountShoppingList;
    }
    @NonNull
    @Override
    public itemShoppingListAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.family_account_shoppinglist_layout, parent, false);

        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull itemShoppingListAdapter.ImageViewHolder holder, int position) {
        Glide.with(context).load(familyAccountShoppingList.get(position).getItemImage_path()).into(holder.imageView);
        holder.viewitemname.setText(familyAccountShoppingList.get(position).getItemName());
        holder.viewitemmanufacture.setText(familyAccountShoppingList.get(position).getItemManufacture());
        holder.viewitemquantity.setText(String.valueOf(familyAccountShoppingList.get(position).getPurchaseQuantity()));
        holder.viewitemprice.setText(String.valueOf(familyAccountShoppingList.get(position).getItemPurchasePrice()));
    }

    @Override
    public int getItemCount() {
        return familyAccountShoppingList.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder{
        TextView viewitemname,viewitemmanufacture, viewitemquantity, viewitemprice;
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.restockFamilyItemImage);
            viewitemname = itemView.findViewById(R.id.restockFamilyItemName);
            viewitemmanufacture = itemView.findViewById(R.id.restockFamilyItemManufacture);
            viewitemquantity = itemView.findViewById(R.id.restockFamilyItemQuantity);
            viewitemprice = itemView.findViewById(R.id.restockFamilyItemPrice);

        }
    }
}
