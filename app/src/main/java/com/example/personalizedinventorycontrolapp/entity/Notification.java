package com.example.personalizedinventorycontrolapp.entity;

public class Notification {

    private int nid;
    private int  users_id;
    private String notification_day;
    private String notification_time;
    private boolean notification_condition;


    public Notification(){

    }

    public Notification(int nid, int users_id, String notification_day, String notification_time, boolean notification_condition ){
        this.nid = nid;
        this.users_id = users_id;
        this.notification_day = notification_day;
        this.notification_time = notification_time;
        this.notification_condition = notification_condition;
    }

    public int getNid() {
        return nid;
    }

    public void setNid(int nid) {
        this.nid = nid;
    }

    public int getUsers_id() {
        return users_id;
    }
    public void setUsers_id(int users_id) {
        this.users_id = users_id;
    }

    public String getNotification_day(){
        return notification_day;
    }
    public void setNotification_day(String notification_day){
        this.notification_day = notification_day;
    }

    public String getNotification_time(){
        return notification_time;
    }
    public void setNotification_time(String notification_time) {
        this.notification_time = notification_time;
    }

    public boolean isNotification_condition() {
        return notification_condition;
    }

    public void setNotification_condition(boolean notification_condition) {
        this.notification_condition = notification_condition;
    }
}
