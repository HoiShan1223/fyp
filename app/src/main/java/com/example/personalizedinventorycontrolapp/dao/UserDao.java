package com.example.personalizedinventorycontrolapp.dao;


import com.example.personalizedinventorycontrolapp.entity.User;
import com.example.personalizedinventorycontrolapp.utils.JDBCUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    public boolean usernameLogin(String username,String password){

        String sql = "select * from users where username = ? and password = ?";

        Connection  con = JDBCUtils.getConn();

        try {
            PreparedStatement pst=con.prepareStatement(sql);

            pst.setString(1,username);
            pst.setString(2,password);

            if(pst.executeQuery().next()){

                return true;

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
       }finally {
            JDBCUtils.close(con);
        }

        return false;
    }

    public boolean emailLogin(String email,String password){

        String sql = "select * from users where email = ? and password = ?";

        Connection  con = JDBCUtils.getConn();

        try {
            PreparedStatement pst=con.prepareStatement(sql);

            pst.setString(1,email);
            pst.setString(2,password);

            if(pst.executeQuery().next()){

                return true;

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            JDBCUtils.close(con);
        }

        return false;
    }

    public boolean register(User user){

        String sql = "insert into users (username,password,email) values (?,?,?)";

        Connection  con = JDBCUtils.getConn();

        try {
            PreparedStatement pst=con.prepareStatement(sql);

            pst.setString(1,user.getUsername());
            pst.setString(2,user.getPassword());
            pst.setString(3,user.getEmail());

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

    public User findUser(String username, String email){

        String sql = "select * from users where username = ? and email = ?";

        Connection  con = JDBCUtils.getConn();
        User user = null;
        try {
            PreparedStatement pst=con.prepareStatement(sql);

            pst.setString(1,username);
            pst.setString(2,email);

            ResultSet rs = pst.executeQuery();

            while (rs.next()){

                int id = rs.getInt(1);
                String usernamedb = rs.getString(2);
                String passworddb  = rs.getString(3);
                String emaildb = rs.getString(4);
                String tokendb = rs.getString(5);
                String usericon = rs.getString(6);
                user = new User(id,usernamedb,passworddb,emaildb,tokendb, usericon);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            JDBCUtils.close(con);
        }

        return user;
    }

    public User findUserWithUsername(String username){

        String sql = "select * from users where username = ?";

        Connection  con = JDBCUtils.getConn();
        User user = null;
        try {
            PreparedStatement pst=con.prepareStatement(sql);

            pst.setString(1,username);

            ResultSet rs = pst.executeQuery();

            while (rs.next()){

                int id = rs.getInt(1);
                String usernamedb = rs.getString(2);
                String passworddb  = rs.getString(3);
                String emaildb = rs.getString(4);
                String tokendb = rs.getString(5);
                String usericon = rs.getString(6);
                user = new User(id,usernamedb,passworddb,emaildb,tokendb, usericon);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            JDBCUtils.close(con);
        }

        return user;
    }

    public User findUserWithEmail(String email){

        String sql = "select * from users where email = ?";

        Connection  con = JDBCUtils.getConn();
        User user = null;
        try {
            PreparedStatement pst=con.prepareStatement(sql);

            pst.setString(1,email);

            ResultSet rs = pst.executeQuery();

            while (rs.next()){

                int id = rs.getInt(1);
                String usernamedb = rs.getString(2);
                String passworddb  = rs.getString(3);
                String emaildb = rs.getString(4);
                String tokendb = rs.getString(5);
                String usericon = rs.getString(6);
                user = new User(id,usernamedb,passworddb,emaildb,tokendb, usericon);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            JDBCUtils.close(con);
        }

        return user;
    }

    public User findUserWithID(int userid){

        String sql = "select * from users where uid = ?";

        Connection  con = JDBCUtils.getConn();
        User user = null;
        try {
            PreparedStatement pst=con.prepareStatement(sql);

            pst.setInt(1,userid);

            ResultSet rs = pst.executeQuery();

            while (rs.next()){

                int id = rs.getInt(1);
                String usernamedb = rs.getString(2);
                String passworddb  = rs.getString(3);
                String emaildb = rs.getString(4);
                String tokendb = rs.getString(5);
                String usericon = rs.getString(6);
                user = new User(id,usernamedb,passworddb,emaildb,tokendb, usericon);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            JDBCUtils.close(con);
        }

        return user;
    }

    public User findUserWithToken(String regToken, int userid){

        String sql = "select * from users u where token = ? AND u.uid != ?";

        Connection  con = JDBCUtils.getConn();
        User user = null;
        try {
            PreparedStatement pst=con.prepareStatement(sql);

            pst.setString(1,regToken);
            pst.setInt(2,userid);
            ResultSet rs = pst.executeQuery();

            while (rs.next()){

                int id = rs.getInt(1);
                String usernamedb = rs.getString(2);
                String passworddb  = rs.getString(3);
                String emaildb = rs.getString(4);
                String tokendb = rs.getString(5);
                String usericon = rs.getString(6);
                user = new User(id,usernamedb,passworddb,emaildb,tokendb, usericon);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            JDBCUtils.close(con);
        }

        return user;
    }

    public List<User> list() {

        String sql = "SELECT * FROM users";

        List<User> list = new ArrayList<>();
        Connection con = JDBCUtils.getConn();

        try {
            PreparedStatement pst = con.prepareStatement(sql);

            ResultSet rs = pst.executeQuery();
            while(rs.next()){
                User user = new User();
                user.setId(rs.getInt(1));
                user.setUsername(rs.getString(2));
                user.setEmail(rs.getString(4));
                user.setToken(rs.getString(5));
                user.setIconImage(rs.getString(6));
                list.add(user);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            JDBCUtils.close(con);
        }

        JDBCUtils.close(con);
        return list;
    }

    public boolean updateToken(int userid, String token){

        String sql = "UPDATE users SET token = ? WHERE uid = ?";

        Connection  con = JDBCUtils.getConn();

        try {
            PreparedStatement pst=con.prepareStatement(sql);

            pst.setString(1,token);
            pst.setInt(2,userid);

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

    public boolean updatePassword(int userid, String password){

        String sql = "UPDATE users SET password = ? WHERE uid = ?";

        Connection  con = JDBCUtils.getConn();

        try {
            PreparedStatement pst=con.prepareStatement(sql);

            pst.setString(1,password);
            pst.setInt(2,userid);

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
