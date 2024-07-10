package com.example.personalizedinventorycontrolapp.dao;

import com.example.personalizedinventorycontrolapp.entity.Group;
import com.example.personalizedinventorycontrolapp.utils.JDBCUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GroupDao {
    public Group findGroup(String email){
        String sql = "SELECT gid from groupings where email = ?";

        Connection  con = JDBCUtils.getConn();
        Group group = null;
        try {
            PreparedStatement pst=con.prepareStatement(sql);

            pst.setString(1,email);

            ResultSet rs = pst.executeQuery();

            while (rs.next()){

                int gid = rs.getInt(1);

                group = new Group(gid);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            JDBCUtils.close(con);
        }

        return group;
    }

    public List<Group> list(int groupings_id) {

        String sql = "SELECT g.email AS 'Group Name', ug.groupings_id, ug.users_id, u.email, u.username, u.icon " +
                "FROM users u JOIN users_groupings ug ON u.uid=ug.users_id JOIN " +
                "groupings g ON g.gid=ug.groupings_id where groupings_id = ? AND g.email <> u.email";

        List<Group> list = new ArrayList<>();
        Connection con = JDBCUtils.getConn();

        try {
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, groupings_id);

            ResultSet rs = pst.executeQuery();
            while(rs.next()){
                Group group = new Group();
                group.setGroupName(rs.getString(1));
                group.setGid(rs.getInt(2));
                group.setUsers_id(rs.getInt(3));
                group.setgEmail(rs.getString(4));
                group.setUsername(rs.getString(5));
                group.setUserIcon(rs.getString(6));
                list.add(group);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            JDBCUtils.close(con);
        }

        JDBCUtils.close(con);
        return list;
    }

    public boolean createGroup(String email){

        String sql = "INSERT INTO groupings (email) VALUES (?)";

        Connection  con = JDBCUtils.getConn();

        try {
            PreparedStatement pst=con.prepareStatement(sql);

            pst.setString(1, email);

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

    public boolean addUsersToGroup(int groupings_id, int users_id){

        String sql = "INSERT INTO users_groupings (groupings_id, users_id) VALUES (?,?)";

        Connection  con = JDBCUtils.getConn();

        try {
            PreparedStatement pst=con.prepareStatement(sql);

            pst.setInt(1, groupings_id);
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

    public Group findUserInGroup(int groupings_id, int users_id){

        String sql = "SELECT * from users_groupings where users_id = ? and groupings_id = ?;";

        Connection  con = JDBCUtils.getConn();
        Group group = null;
        try {
            PreparedStatement pst=con.prepareStatement(sql);

            pst.setInt(1,users_id);
            pst.setInt(2,groupings_id);

            ResultSet rs = pst.executeQuery();

            while (rs.next()){

                int gid = rs.getInt(1);
                int uid = rs.getInt(2);

                group = new Group(gid, uid);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            JDBCUtils.close(con);
        }

        return group;
    }

    public boolean deleteUserFromGroup(int groupings_id, int users_id){
        String sql = "Delete from users_groupings where users_id = ? and groupings_id = ?";

        Connection  con = JDBCUtils.getConn();

        try {
            PreparedStatement pst=con.prepareStatement(sql);

            pst.setInt(2, groupings_id);
            pst.setInt(1, users_id);

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
