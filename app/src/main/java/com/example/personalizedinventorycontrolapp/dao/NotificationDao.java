package com.example.personalizedinventorycontrolapp.dao;

import com.example.personalizedinventorycontrolapp.entity.Notification;
import com.example.personalizedinventorycontrolapp.utils.JDBCUtils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NotificationDao {
    public Notification findNotification(int user_id){

        String sql = "SELECT nid, users_id, notification_day, notification_time, notification_condition FROM users u JOIN notifications n ON u.uid=n.users_id where users_id = ?";

        Connection  con = JDBCUtils.getConn();
        Notification notification = null;
        try {
            PreparedStatement pst=con.prepareStatement(sql);

            pst.setInt(1,user_id);

            ResultSet rs = pst.executeQuery();

            while (rs.next()){

                int nid = rs.getInt(1);
                int uid = rs.getInt(2);
                String notification_day = rs.getString(3);
                String notification_time  = rs.getString(4);
                boolean notification_condition = rs.getBoolean(5);
                notification = new Notification(nid,uid,notification_day,notification_time, notification_condition );
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            JDBCUtils.close(con);
        }

        return notification;
    }

    public boolean addNotification(int uid, String notification_day,String notification_time, boolean notification_condition ){

        String sql = "INSERT INTO notifications (users_id, notification_day, notification_time, notification_condition) VALUES ( ?, ? ,? ,?);";

        Connection con = JDBCUtils.getConn();
        int notificationCondition;
        if(notification_condition){
            notificationCondition = 1;
        }else{
            notificationCondition = 0;
        }

        try {
            PreparedStatement pst=con.prepareStatement(sql);

            pst.setInt(1,uid);
            pst.setString(2,notification_day);
            pst.setString(3,notification_time);
            pst.setInt(4, notificationCondition);

            int value = pst.executeUpdate();

            if(value>0){
                return true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            JDBCUtils.close(con);
        }
        return false;
    }

    public boolean updateNotification(int uid, String notification_day,String notification_time){

        String sql = "UPDATE notifications SET notification_day = ?, notification_time = ? WHERE users_id = ?";

        Connection  con = JDBCUtils.getConn();

        try {
            PreparedStatement pst=con.prepareStatement(sql);

            pst.setString(1, notification_day);
            pst.setString(2, notification_time);
            pst.setInt(3, uid);

            int value = pst.executeUpdate();

            if(value>0){
                return true;
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            JDBCUtils.close(con);
        }
        return false;
    }

    public boolean updateNotification(int users_id, boolean notification_condition){
        String sql = "UPDATE notifications SET notification_condition = ? WHERE users_id = ?";

        Connection  con = JDBCUtils.getConn();
        int notificationCondition;
        if(notification_condition){
            notificationCondition = 1;
        }else{
            notificationCondition = 0;
        }

        try {
            PreparedStatement pst=con.prepareStatement(sql);

            pst.setInt(1, notificationCondition);
            pst.setInt(2, users_id);

            int value = pst.executeUpdate();

            if(value>0){
                return true;
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            JDBCUtils.close(con);
        }
        return false;
    }
}
