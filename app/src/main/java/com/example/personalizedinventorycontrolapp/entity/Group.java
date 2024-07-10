package com.example.personalizedinventorycontrolapp.entity;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private int  gid;
    private String groupName;
    private int  users_id;
    private String gEmail;
    private String username;
    private String userIcon;
    private List<ShoppingList> ShoppingList;

    public Group(){

    }

    public Group(int gid, int users_id){
        this.gid = gid;
        this.users_id = users_id;
    }

    public Group(int gid) {
        this.gid = gid;
    }

    public Group(int gid,String groupName, int users_id, String gEmail, String username, String userIcon) {
        this.gid = gid;
        this.groupName = groupName;
        this.users_id = users_id;
        this.gEmail = gEmail;
        this.username = username;
        this.userIcon = userIcon;
    }

    public Group(int gid,String groupName, int users_id, String gEmail, String username, String userIcon, List<ShoppingList> ShoppingList) {
        this.gid = gid;
        this.groupName = groupName;
        this.users_id = users_id;
        this.gEmail = gEmail;
        this.username = username;
        this.userIcon = userIcon;
        this.ShoppingList = ShoppingList;
    }

    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }

    public String getGroupName() {
        return groupName;
    }
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getUsers_id() {
        return users_id;
    }

    public void setUsers_id(int users_id) {
        this.users_id = users_id;
    }

    public String getgEmail() {
        return gEmail;
    }

    public void setgEmail(String gEmail) {
        this.gEmail = gEmail;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public List<ShoppingList> getShoppingList() {
        return ShoppingList;
    }

    public void setShoppingList(List<ShoppingList> shoppingList) {
        ShoppingList = shoppingList;
    }
}
