package com.example.personalizedinventorycontrolapp.entity;

public class Product {
    private int  pid;
    private String pname;
    private String manufacture;
    private float price;
    private String image_path; // need to change cause set it as LONGBLOB in mysql
    private String barcode;

    public Product(){

    }

    public Product(int pid, String pname, String manufacture, float price, String image_path, String barcode){
        this.pid = pid;
        this.pname = pname;
        this.manufacture = manufacture;
        this.price = price;
        this.image_path = image_path;
        this.barcode = barcode;
    }

    public int getPid(){
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getPname() {
        return pname;
    }
    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getManufacture(){ return manufacture; }
    public void setManufacture(String manufacture) {
        this.manufacture = manufacture;
    }

    public float getPrice() {
        return price;
    }
    public void setPrice(float price) {
        this.price = price;
    }

    public String getImage_path(){ return image_path; }
    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getBarcode() {
        return barcode;
    }
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
}
