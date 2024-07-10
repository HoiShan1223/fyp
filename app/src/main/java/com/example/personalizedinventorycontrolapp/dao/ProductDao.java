package com.example.personalizedinventorycontrolapp.dao;


import com.example.personalizedinventorycontrolapp.utils.JDBCUtils;
import com.example.personalizedinventorycontrolapp.entity.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class ProductDao {
    public Product findProduct(String barcode){

        String sql = "select * from products where barcode = ?";

        Connection  con = JDBCUtils.getConn();
        Product product = null;
        try {
            PreparedStatement pst=con.prepareStatement(sql);

            pst.setString(1,barcode);

            ResultSet rs = pst.executeQuery();

            while (rs.next()){

                int pid = rs.getInt(1);
                String pnamedb = rs.getString(2);
                String manufacturedb  = rs.getString(3);
                float price = rs.getFloat(4);
                String imagepathdb  = rs.getString(5);
                String barcodedb  = rs.getString(6);
                product = new Product(pid,pnamedb,manufacturedb,price,imagepathdb, barcodedb);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            JDBCUtils.close(con);
        }

        return product;
    }

    public Product findProduct(int product_id){

        String sql = "select * from products where pid = ?";

        Connection  con = JDBCUtils.getConn();
        Product product = null;
        try {
            PreparedStatement pst=con.prepareStatement(sql);

            pst.setInt(1,product_id);

            ResultSet rs = pst.executeQuery();

            while (rs.next()){

                int pid = rs.getInt(1);
                String pnamedb = rs.getString(2);
                String manufacturedb  = rs.getString(3);
                float price = rs.getFloat(4);
                String imagepathdb  = rs.getString(5);
                String barcodedb  = rs.getString(6);
                product = new Product(pid,pnamedb,manufacturedb,price,imagepathdb, barcodedb);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            JDBCUtils.close(con);
        }

        return product;
    }

    public boolean addProduct(Product product){

        String sql = "insert into products (pname, manufacture, price, image_path, barcode) values (?, ?, ?, ?, ?)";

        Connection  con = JDBCUtils.getConn();

        try {
            PreparedStatement pst=con.prepareStatement(sql);

            pst.setString(1,product.getPname());
            pst.setString(2, product.getManufacture());
            pst.setFloat(3,product.getPrice());
            pst.setString(4, product.getImage_path());
            pst.setString(5, product.getBarcode());

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
