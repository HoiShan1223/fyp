package com.example.personalizedinventorycontrolapp.dao;

import android.util.Log;

import com.example.personalizedinventorycontrolapp.entity.Item;
import com.example.personalizedinventorycontrolapp.entity.ShoppingList;
import com.example.personalizedinventorycontrolapp.utils.JDBCUtils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ShoppingListDao {
    public List<ShoppingList> getShoppingList(int users_id) {

        String sql = "SELECT sid, s.users_id, s.products_id, s.items_id, p.pname, p.manufacture, item_purchase_price, item_purchase_quantity, " +
                "p.image_path, i.remaining_stock, restock FROM users u JOIN shoppingLists s ON u.uid = s.users_id JOIN products p " +
                "ON p.pid = s.products_id JOIN items i ON i.iid = s.items_id WHERE s.users_id = ?";

        List<ShoppingList> list = new ArrayList<>();
        Connection con = JDBCUtils.getConn();

        try {
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, users_id);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                ShoppingList shoppingList = new ShoppingList();
                shoppingList.setShoppinglistId(rs.getInt(1));
                shoppingList.setUsers_id(rs.getInt(2));
                shoppingList.setProducts_id(rs.getInt(3));
                shoppingList.setItems_id(rs.getInt(4));
                shoppingList.setItemName(rs.getString(5));
                shoppingList.setItemManufacture(rs.getString(6));
                shoppingList.setItemPurchasePrice(rs.getFloat(7));
                shoppingList.setPurchaseQuantity(rs.getInt(8));
                shoppingList.setItemImage_path(rs.getString(9));
                shoppingList.setRemainingStock(rs.getInt(10));
                shoppingList.setRestock(rs.getBoolean(11));
                list.add(shoppingList);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            JDBCUtils.close(con);
        }

        JDBCUtils.close(con);
        return list;
    }

    public ShoppingList findItemInShoppingListDB(int users_id, int products_id, int items_id){
        String sql = "select sid, s.users_id, s.products_id, s.items_id, p.pname, p.manufacture, s.item_purchase_price, s.item_purchase_quantity, p.image_path, i.remaining_stock, s.restock FROM users u JOIN shoppingLists s " +
                "ON u.uid = s.users_id JOIN products p ON p.pid = s.products_id JOIN items i ON i.iid = s.items_id where s.users_id = ? and s.products_id = ? and s.items_id = ?";

        Connection con = JDBCUtils.getConn();
        ShoppingList shoppingListItem = null;

        try {
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, users_id);
            pst.setInt(2, products_id);
            pst.setInt(3, items_id);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                int sid = rs.getInt(1);
                int nid = rs.getInt(2);
                int pid = rs.getInt(3);
                int iid = rs.getInt(4);
                String itemName = rs.getString(5);
                String itemManufacture = rs.getString(6);
                float itemPurchasePrice = rs.getFloat(7);
                int itemPurchaseQuantity = rs.getInt(8);
                String itemImagePath = rs.getString(9);
                int remainingStock = rs.getInt(10);
                boolean restock = rs.getBoolean(11);
                shoppingListItem = new ShoppingList(sid, nid, pid, iid, itemName, itemManufacture, itemPurchasePrice, itemPurchaseQuantity, itemImagePath, remainingStock, restock);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            JDBCUtils.close(con);
        }

        return shoppingListItem;
    }
    public boolean addItemToShoppingListDB(int userid, int pid, int iid, float item_purchase_price, int item_purchase_quantity, boolean restock) {

        String sql = "INSERT INTO shoppingLists (users_id, products_id, items_id, item_purchase_price, item_purchase_quantity, restock) VALUES (?, ?, ?, ?, ?, ?)";

        int Restock;
        if (restock) {
            Restock = 1;
        } else {
            Restock = 0;
        }
        Connection con = JDBCUtils.getConn();

        try {
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, userid);
            pst.setInt(2, pid);
            pst.setInt(3, iid);
            pst.setFloat(4, item_purchase_price);
            pst.setInt(5, item_purchase_quantity);
            pst.setInt(6, Restock);

            int value = pst.executeUpdate();

            if (value > 0) {
                return true;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            JDBCUtils.close(con);
        }
        return false;
    }

    public boolean updateItemOnShoppingList(int user_id, int products_id, int items_id, float item_purchase_price, int item_purchase_quantity, boolean restock) {

        String sql = "Update shoppingLists set restock = ?, item_purchase_price = ?, item_purchase_quantity = ? where users_id = ? and products_id = ? and items_id = ?";

        int Restock;
        if (restock) {
            Restock = 1;
        } else {
            Restock = 0;
        }

        Connection con = JDBCUtils.getConn();

        try {
            PreparedStatement pst = con.prepareStatement(sql);

            pst.setInt(1, Restock);
            pst.setFloat(2, item_purchase_price);
            pst.setInt(3, item_purchase_quantity);
            pst.setInt(4, user_id);
            pst.setInt(5, products_id);
            pst.setInt(6, items_id);

            int value = pst.executeUpdate();

            if (value > 0) {
                return true;
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            JDBCUtils.close(con);
        }
        return false;
    }

    public boolean deleteItemFromShoppingList(int user_id, int products_id, int items_id) {

        String sql = "Delete from shoppingLists where users_id = ? and products_id = ? and items_id = ?";

        Connection con = JDBCUtils.getConn();

        try {
            PreparedStatement pst = con.prepareStatement(sql);

            pst.setInt(1, user_id);
            pst.setInt(2, products_id);
            pst.setInt(3, items_id);

            int value = pst.executeUpdate();

            if (value > 0) {
                return true;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            JDBCUtils.close(con);
        }
        return false;
    }
}
