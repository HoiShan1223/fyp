package com.example.personalizedinventorycontrolapp.entity;

public class ShoppingList {
    private int  shoppinglistId;
    private int  users_id;
    private int products_id;
    private int items_id;
    private String itemName;
    private String itemManufacture;
    private float itemPurchasePrice;
    private int purchaseQuantity;
    private String itemImage_path;
    private int remainingStock;
    private boolean restock;

    public ShoppingList() {
    }

    public ShoppingList(int shoppinglistId, int users_id, int products_id, int items_id,  String itemName, String itemManufacture,
                        float itemPurchasePrice, int purchaseQuantity, String itemImage_path, int remainingStock, boolean restock) {
        this.shoppinglistId = shoppinglistId;
        this.users_id = users_id;
        this.products_id = products_id;
        this.items_id = items_id;
        this.itemName = itemName;
        this.itemManufacture = itemManufacture;
        this.itemPurchasePrice = itemPurchasePrice;
        this.purchaseQuantity = purchaseQuantity;
        this.itemImage_path = itemImage_path;
        this.remainingStock = remainingStock;
        this.restock = restock;
    }

    //shoppingListID
    public int getShoppinglistId() {
        return shoppinglistId;
    }

    public void setShoppinglistId(int shoppinglistId) {
        this.shoppinglistId = shoppinglistId;
    }

    //users_id (foreign key)
    public int getUsers_id() {
        return users_id;
    }
    public void setUsers_id(int users_id) {
        this.users_id = users_id;
    }

    //products_id (foreign key) -- to get the product info
    public int getProducts_id() {
        return products_id;
    }
    public void setProducts_id(int products_id) {
        this.products_id = products_id;
    }

    //items_id(foreign key) -- to get the remaining stock value
    public int getItems_id() {
        return items_id;
    }

    public void setItems_id(int items_id) {
        this.items_id = items_id;
    }

    //item Name
    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    //item Manufacture
    public String getItemManufacture() {
        return itemManufacture;
    }

    public void setItemManufacture(String itemManufacture) {
        this.itemManufacture = itemManufacture;
    }

    //Purchase price
    public float getItemPurchasePrice() {
        return itemPurchasePrice;
    }

    public void setItemPurchasePrice(float itemPurchasePrice) {
        this.itemPurchasePrice = itemPurchasePrice;
    }

    //Purchase quantity
    public int getPurchaseQuantity() {
        return purchaseQuantity;
    }

    public void setPurchaseQuantity(int purchaseQuantity) {
        this.purchaseQuantity = purchaseQuantity;
    }

    //item image path
    public String getItemImage_path() {
        return itemImage_path;
    }

    public void setItemImage_path(String itemImage_path) {
        this.itemImage_path = itemImage_path;
    }

    //remaining stock
    public int getRemainingStock() {
        return remainingStock;
    }

    public void setRemainingStock(int remainingStock) {
        this.remainingStock = remainingStock;
    }

    //restock
    public boolean isRestock() {
        return restock;
    }

    public void setRestock(boolean restock) {
        this.restock = restock;
    }
}
