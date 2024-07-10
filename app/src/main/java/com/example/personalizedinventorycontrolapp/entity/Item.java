package com.example.personalizedinventorycontrolapp.entity;

import java.math.BigDecimal;

public class Item {
    private int  iid;
    private int  users_id;
    private int  products_id;
    private String itemName;
    private String itemManufacture;
    private float itemPrice;
    private String itemImage_path;
    private String itemBarcode;
    private int quantity;
    private int usage;
    private String usageType;
    private BigDecimal dailyusage;
    private int usage_daily;
    private int usage_weekly;
    private int usage_biweekly;
    private int usage_monthly;
    private boolean Stockcondition;
    private String stock_time;
    private int remainingStock;

    public Item() {

    }

    public Item(int iid, int users_id, int products_id, String itemName, String itemManufacture, float itemPrice,
                String itemImage_path, String itemBarcode, int quantity, int usage, String usageType, BigDecimal dailyusage,
                int usage_daily, int usage_weekly, int usage_biweekly, int usage_monthly, boolean Stockcondition, String stock_time, int remainingStock){
        this.iid = iid;
        this.users_id = users_id;
        this.products_id = products_id;
        this.itemName = itemName;
        this.itemManufacture = itemManufacture;
        this.itemPrice = itemPrice;
        this.itemImage_path = itemImage_path;
        this.itemBarcode = itemBarcode;
        this.quantity = quantity;
        this.usage = usage;
        this.usageType = usageType;
        this.dailyusage = dailyusage;
        this.usage_daily = usage_daily;
        this.usage_weekly = usage_weekly;
        this.usage_biweekly = usage_biweekly;
        this.usage_monthly = usage_monthly;
        this.Stockcondition = Stockcondition;
        this.stock_time = stock_time;
        this.remainingStock = remainingStock;
    }
    public Item(int iid, int users_id, int products_id, int quantity, BigDecimal dailyusage, int usage_daily, int usage_weekly, int usage_biweekly, int usage_monthly, String usageType, int usage, boolean Stockcondition, String stock_time, int remainingStock) {
        this.iid = iid;
        this.users_id = users_id;
        this.products_id = products_id;
        this.quantity = quantity;
        this.dailyusage = dailyusage;
        this.usage_daily = usage_daily;
        this.usage_weekly = usage_weekly;
        this.usage_biweekly = usage_biweekly;
        this.usage_monthly = usage_monthly;
        this.usageType = usageType;
        this.usage = usage;
        this.Stockcondition = Stockcondition;
        this.stock_time = stock_time;
        this.remainingStock = remainingStock;
    }

    //iid
    public int getIid() {
        return iid;
    }
    public void setIid(int iid) {
        this.iid = iid;
    }

    //users_id (foreign key)
    public int getUsers_id() {
        return users_id;
    }
    public void setUsers_id(int users_id) {
        this.users_id = users_id;
    }

    //products_id (foreign key)
    public int getProducts_id() {
        return products_id;
    }
    public void setProducts_id(int products_id) {
        this.products_id = products_id;
    }

    public String getItemName() {
        return itemName;
    }
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemManufacture(){ return itemManufacture; }
    public void setItemManufacture(String itemManufacture) {
        this.itemManufacture = itemManufacture;
    }

    public float getItemPrice() {
        return itemPrice;
    }
    public void setItemPrice(float itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemImage_path(){ return itemImage_path; }
    public void setItemImage_path(String itemImage_path) {
        this.itemImage_path = itemImage_path;
    }

    public String getItemBarcode() {
        return itemBarcode;
    }
    public void setItemBarcode(String itemBarcode) {
        this.itemBarcode = itemBarcode;
    }

    //quantity
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    //Usage

    public int getUsage() {
        return usage;
    }

    public void setUsage(int usage) {
        this.usage = usage;
    }

    public String getUsageType() {
        return usageType;
    }

    public void setUsageType(String usageType) {
        this.usageType = usageType;
    }

    public BigDecimal getDailyusage() {
        return dailyusage;
    }

    public void setDailyusage(BigDecimal dailyusage) {
        this.dailyusage = dailyusage;
    }

    public int getUsage_daily() {
        return usage_daily;
    }

    public void setUsage_daily(int usage_daily) {
        this.usage_daily = usage_daily;
    }

    public int getUsage_weekly() { return usage_weekly; }
    public void setUsage_weekly(int usage_weekly) {
        this.usage_weekly = usage_weekly;
    }

    public int getUsage_biweekly() { return usage_biweekly; }
    public void setUsage_biweekly(int usage_biweekly) {
        this.usage_biweekly = usage_biweekly;
    }

    public int getUsage_monthly() { return usage_monthly; }
    public void setUsage_monthly(int usage_monthly) {
        this.usage_monthly = usage_monthly;
    }

    public boolean isStockCondition(){
        return Stockcondition;
    }
    public void setStockCondition(boolean stockcodition){
        this.Stockcondition = stockcodition;
    }

    public String getStock_time(){
        return stock_time;
    }

    public void setStock_time(String stock_time) {
        this.stock_time = stock_time;
    }

    public int getRemainingStock() {
        return remainingStock;
    }

    public void setRemainingStock(int remainingStock) {
        this.remainingStock = remainingStock;
    }
}