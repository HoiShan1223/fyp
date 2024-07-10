package com.example.personalizedinventorycontrolapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.personalizedinventorycontrolapp.R;
import com.example.personalizedinventorycontrolapp.entity.ShoppingList;

import java.util.List;

public class myAdapter extends RecyclerView.Adapter<myAdapter.ImageViewHolder>{
    private final Context context;
    private final List<ShoppingList> ShoppingList;
    int currentQuantity = 0;
    int clickID = 0;

    public myAdapter(Context context, List<ShoppingList> ShoppingList) {
        this.context = context;
        this.ShoppingList = ShoppingList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shoppinglist_layout, parent, false);

        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        int itemPosition = holder.getAdapterPosition();
        Glide.with(context).load(ShoppingList.get(position).getItemImage_path()).into(holder.imageView);
        holder.viewitemname.setText(ShoppingList.get(position).getItemName());
        holder.viewitemmanufacture.setText(ShoppingList.get(position).getItemManufacture());
        float priceForOne = ShoppingList.get(itemPosition).getItemPurchasePrice()/ShoppingList.get(itemPosition).getPurchaseQuantity();
        holder.viewitemquantity.setText(String.valueOf(ShoppingList.get(position).getPurchaseQuantity()));
        holder.viewitemprice.setText(String.valueOf(ShoppingList.get(position).getItemPurchasePrice()));
        holder.viewimageButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.viewimageButtonPlusItem.setVisibility(View.VISIBLE);
                holder.viewimageButtonPlusItem.setClickable(true);
                holder.viewimageButtonMinusItem.setVisibility(View.VISIBLE);
                holder.viewimageButtonMinusItem.setClickable(true);
                holder.viewimageButtonEdit.setVisibility(View.INVISIBLE);
                holder.viewimageButtonEdit.setClickable(false);
                holder.viewimageButtonCheck.setVisibility(View.VISIBLE);
                holder.viewimageButtonCheck.setClickable(true);
            }
        });
        holder.viewimageButtonPlusItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickID = 1;
                calculateQuantityAndPrice(holder, itemPosition, priceForOne, clickID);
            }
        });

        holder.viewimageButtonMinusItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickID = 2;
                calculateQuantityAndPrice(holder, itemPosition, priceForOne, clickID);
            }
        });

        holder.viewimageButtonCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.viewimageButtonPlusItem.setVisibility(View.INVISIBLE);
                holder.viewimageButtonPlusItem.setClickable(false);
                holder.viewimageButtonMinusItem.setVisibility(View.INVISIBLE);
                holder.viewimageButtonMinusItem.setClickable(false);
                holder.viewimageButtonEdit.setVisibility(View.VISIBLE);
                holder.viewimageButtonEdit.setClickable(true);
                holder.viewimageButtonCheck.setVisibility(View.INVISIBLE);
                holder.viewimageButtonCheck.setClickable(false);
            }
        });

    }

    private void calculateQuantityAndPrice(@NonNull ImageViewHolder holder, int itemPosition, float priceForOne, int clickID){
        if(clickID == 1){
            currentQuantity = ShoppingList.get(itemPosition).getPurchaseQuantity();
            int newCurrentQuantity = currentQuantity + 1;
            ShoppingList.get(itemPosition).setPurchaseQuantity(newCurrentQuantity);
            holder.viewitemquantity.setText(String.valueOf(ShoppingList.get(itemPosition).getPurchaseQuantity()));
            Float price = ShoppingList.get(itemPosition).getItemPurchasePrice() + priceForOne;
            ShoppingList.get(itemPosition).setItemPurchasePrice(price);
            Log.d("myAdapter", "New Quantity and Price: " + newCurrentQuantity + ", " + price);
            holder.viewitemprice.setText(String.valueOf(price));
        }else if(clickID == 2){
            currentQuantity = ShoppingList.get(itemPosition).getPurchaseQuantity();
            int newCurrentQuantity = currentQuantity - 1;
            ShoppingList.get(itemPosition).setPurchaseQuantity(newCurrentQuantity);
            holder.viewitemquantity.setText(String.valueOf(ShoppingList.get(itemPosition).getPurchaseQuantity()));
            Float price = ShoppingList.get(itemPosition).getItemPurchasePrice() - priceForOne;
            ShoppingList.get(itemPosition).setItemPurchasePrice(price);
            Log.d("myAdapter", "New Quantity and Price: " + newCurrentQuantity + ", " + price);
            holder.viewitemprice.setText(String.valueOf(price));
        }
    }


    @Override
    public int getItemCount() {
        return ShoppingList.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder{
        TextView viewitemname,viewitemmanufacture, viewitemquantity, viewitemprice;
        ImageView imageView;
        ImageButton viewimageButtonPlusItem, viewimageButtonMinusItem, viewimageButtonEdit, viewimageButtonCheck;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.restockItemImage);
            viewitemname = itemView.findViewById(R.id.restockItemName);
            viewitemmanufacture = itemView.findViewById(R.id.restockItemManufacture);
            viewitemquantity = itemView.findViewById(R.id.restockItemQuantity);
            viewitemprice = itemView.findViewById(R.id.restockItemPrice);
            viewimageButtonPlusItem = itemView.findViewById(R.id.imageButtonPlusItem);
            viewimageButtonMinusItem = itemView.findViewById(R.id.imageButtonMinusItem);
            viewimageButtonEdit = itemView.findViewById(R.id.imageButtonEdit);
            viewimageButtonCheck = itemView.findViewById(R.id.imageButtonCheck);
            viewimageButtonPlusItem.setClickable(false);
            viewimageButtonMinusItem.setClickable(false);
            viewimageButtonEdit.setClickable(true);
            viewimageButtonCheck.setClickable(false);
        }
    }

}
