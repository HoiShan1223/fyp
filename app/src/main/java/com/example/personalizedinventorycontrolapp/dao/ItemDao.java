package com.example.personalizedinventorycontrolapp.dao;

import android.net.Uri;
import android.util.Log;

import com.example.personalizedinventorycontrolapp.entity.Item;
import com.example.personalizedinventorycontrolapp.entity.User;
import com.example.personalizedinventorycontrolapp.utils.JDBCUtils;
import com.example.personalizedinventorycontrolapp.entity.Product;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ItemDao {

    public List<Item> list(int users_id) {

        String sql = "SELECT pname, manufacture, price, image_path, barcode, quantity, dailyusage" +
                ", usage_daily, usage_weekly, usage_biweekly, usage_monthly, pid, stock_condition, stock_time, remaining_stock, iid, users_id" +
                " FROM users u JOIN items i ON u.uid = i.users_id JOIN products p ON p.pid = i.products_id" +
                " WHERE users_id = ?";

        List<Item> list = new ArrayList<>();
        Connection con = JDBCUtils.getConn();

        try {
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, users_id);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Item item = new Item();
                item.setItemName(rs.getString(1));
                item.setItemManufacture(rs.getString(2));
                item.setItemPrice(rs.getFloat(3));
                item.setItemImage_path(rs.getString(4));
                item.setItemBarcode(rs.getString(5));
                item.setQuantity(rs.getInt(6));
                item.setDailyusage(rs.getBigDecimal(7));
                item.setUsage_daily(rs.getInt(8));
                item.setUsage_weekly(rs.getInt(9));
                item.setUsage_biweekly(rs.getInt(10));
                item.setUsage_monthly(rs.getInt(11));
                if (rs.getInt(8) != 0) {
                    item.setUsage(rs.getInt(8));
                    item.setUsageType("Daily");
                } else if (rs.getInt(9) != 0) {
                    item.setUsage(rs.getInt(9));
                    item.setUsageType("Weekly");
                } else if (rs.getInt(10) != 0) {
                    item.setUsage(rs.getInt(10));
                    item.setUsageType("Biweekly");
                } else if (rs.getInt(11) != 0) {
                    item.setUsage(rs.getInt(11));
                    item.setUsageType("Monthly");
                }
                item.setProducts_id(rs.getInt(12));
                item.setStockCondition(rs.getBoolean(13));
                item.setStock_time(rs.getString(14));
                item.setRemainingStock(rs.getInt(15));
                item.setIid(rs.getInt(16));
                item.setUsers_id(rs.getInt(17));
                list.add(item);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            JDBCUtils.close(con);
        }

        JDBCUtils.close(con);
        return list;
    }

    public boolean addItem(int user_id, int product_id, int quantity, BigDecimal dailyusage, int usage_daily, int usage_weekly, int usage_biweekly, int usage_monthly, String stock_time, int remainingStock) {

        String sql = "INSERT INTO items (users_id, products_id, quantity, dailyusage, " +
                "usage_daily, usage_weekly, usage_biweekly, usage_monthly, stock_time, remaining_stock) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection con = JDBCUtils.getConn();

        try {
            PreparedStatement pst = con.prepareStatement(sql);

            pst.setInt(1, user_id);
            pst.setInt(2, product_id);
            pst.setInt(3, quantity);
            pst.setBigDecimal(4, dailyusage);
            pst.setInt(5, usage_daily);
            pst.setInt(6, usage_weekly);
            pst.setInt(7, usage_biweekly);
            pst.setInt(8, usage_monthly);
            pst.setString(9, stock_time);
            pst.setInt(10, remainingStock);

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

    //find item function
    public Item findItem(int user_id, int product_id) {
        String sql = "select * from items where users_id = ? and products_id = ?";

        Connection con = JDBCUtils.getConn();
        Item item = null;
        try {
            PreparedStatement pst = con.prepareStatement(sql);

            pst.setInt(1, user_id);
            pst.setInt(2, product_id);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                int iid = rs.getInt(1);
                int uiddb = rs.getInt(2);
                int piddb = rs.getInt(3);
                int quantitydb = rs.getInt(4);
                BigDecimal dailyusagedb = rs.getBigDecimal(5);
                int usage_dailydb = rs.getInt(6);
                int usage_weeklydb = rs.getInt(7);
                int usage_biweeklydb = rs.getInt(8);
                int usage_monthlydb = rs.getInt(9);
                String usageType = null;
                int usage = 0;
                if (rs.getInt(6) != 0) {
                    usageType = "Daily";
                    usage = usage_dailydb;
                } else if (rs.getInt(7) != 0) {
                    usageType = "Weekly";
                    usage = usage_weeklydb;
                } else if (rs.getInt(8) != 0) {
                    usageType = "Biweekly";
                    usage = usage_biweeklydb;
                } else if (rs.getInt(9) != 0) {
                    usageType = "Monthly";
                    usage = usage_monthlydb;
                }
                boolean stockCondition = rs.getBoolean(10);
                String stock_time = rs.getString(11);
                int remainingStock = rs.getInt(12);
                item = new Item(iid, uiddb, piddb, quantitydb, dailyusagedb, usage_dailydb, usage_weeklydb, usage_biweeklydb, usage_monthlydb, usageType, usage, stockCondition, stock_time, remainingStock);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            JDBCUtils.close(con);
        }

        return item;
    }

    public boolean updateItem(int user_id, int product_id, int quantity, BigDecimal dailyusage, int usage_daily, int usage_weekly, int usage_biweekly, int usage_monthly, String stock_time, int remainingStock) {

        String sql = "UPDATE items SET quantity = ?, dailyusage = ?, usage_daily = ?, usage_weekly = ?, usage_biweekly = ?, usage_monthly = ?, stock_time = ?, remaining_stock = ?" +
                " WHERE users_id = ? and products_id = ?";

        Connection con = JDBCUtils.getConn();

        try {
            PreparedStatement pst = con.prepareStatement(sql);

            pst.setInt(1, quantity);
            pst.setBigDecimal(2, dailyusage);
            pst.setInt(3, usage_daily);
            pst.setInt(4, usage_weekly);
            pst.setInt(5, usage_biweekly);
            pst.setInt(6, usage_monthly);
            pst.setString(7, stock_time);
            pst.setInt(8, remainingStock);
            pst.setInt(9, user_id);
            pst.setInt(10, product_id);

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



    public boolean updateStockCondition(int user_id, int product_id, boolean stockcondition) {

        String sql = "Update items set stock_condition = ? where users_id = ? and products_id = ?";

        int stock_condition;
        if (stockcondition) {
            stock_condition = 1;
        } else {
            stock_condition = 0;
        }

        Connection con = JDBCUtils.getConn();

        try {
            PreparedStatement pst = con.prepareStatement(sql);

            pst.setInt(1, stock_condition);
            pst.setInt(2, user_id);
            pst.setInt(3, product_id);

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



    public boolean updateRemainingStockInItem(int user_id, int product_id, int remainingStock) {

        String sql = "Update items set remaining_stock = ? where users_id = ? and products_id = ?";


        Connection con = JDBCUtils.getConn();

        try {
            PreparedStatement pst = con.prepareStatement(sql);

            pst.setInt(1, remainingStock);
            pst.setInt(2, user_id);
            pst.setInt(3, product_id);

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

    public boolean updateItem(int user_id, int product_id, int quantity, int remainingStock, String stockTime, Boolean stockCondition) {

        String sql = "Update items set quantity = ?, remaining_stock = ?, stock_time = ?, stock_condition = ? where users_id = ? and products_id = ?";

        int stock_condition;
        if (stockCondition) {
            stock_condition = 1;
        } else {
            stock_condition = 0;
        }

        Connection con = JDBCUtils.getConn();

        try {
            PreparedStatement pst = con.prepareStatement(sql);

            pst.setInt(1, quantity);
            pst.setInt(2, remainingStock);
            pst.setString(3, stockTime);
            pst.setInt(4, stock_condition);
            pst.setInt(5, user_id);
            pst.setInt(6, product_id);

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

    public boolean deleteItem(int user_id, int product_id) {

        String sql = "Delete from items where products_id = ? and users_id = ?";

        Connection con = JDBCUtils.getConn();

        try {
            PreparedStatement pst = con.prepareStatement(sql);

            pst.setInt(2, user_id);
            pst.setInt(1, product_id);

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
