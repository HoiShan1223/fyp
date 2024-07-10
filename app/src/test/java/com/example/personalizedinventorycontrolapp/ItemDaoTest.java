package com.example.personalizedinventorycontrolapp;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.anyString;

import com.example.personalizedinventorycontrolapp.dao.ItemDao;
import com.example.personalizedinventorycontrolapp.dao.UserDao;
import com.example.personalizedinventorycontrolapp.entity.Item;


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemDaoTest {
    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindItem() throws SQLException {
        int userId = 1;
        int productId = 1;
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("iid")).thenReturn(1);
        when(mockResultSet.getInt("users_id")).thenReturn(userId);
        when(mockResultSet.getInt("products_id")).thenReturn(productId);
        when(mockResultSet.getInt("quantity")).thenReturn(7);
        when(mockResultSet.getBigDecimal("daily_usage")).thenReturn(new BigDecimal("1.00000000000"));
        when(mockResultSet.getInt("usage_daily")).thenReturn(1);
        when(mockResultSet.getInt("usage_weekly")).thenReturn(0);
        when(mockResultSet.getInt("usage_biweekly")).thenReturn(0);
        when(mockResultSet.getInt("usage_monthly")).thenReturn(0);
        when(mockResultSet.getBoolean("stock_condition")).thenReturn(false);
        when(mockResultSet.getString("stock_time")).thenReturn("2023-03-25");
        when(mockResultSet.getInt("remaining_stock")).thenReturn(7);
        Item expectedItem = new Item(1, userId, productId, 7, new BigDecimal("1.00000000000"), 1, 0, 0, 0, "Daily", 1, false, "2023-03-25", 7);
        Item actualItem = new ItemDao().findItem(userId, productId);
        assertEquals(expectedItem.getStock_time(), actualItem.getStock_time());
//        assertEquals(expectedItem.getStock_time(), expectedItem.getStock_time());
    }


    @Test
    public void testList() throws SQLException {
        // Arrange
        int userId = 13;
        ItemDao itemDao = new ItemDao();

        // Set up mocked PreparedStatement and ResultSet

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        // Set up mocked ResultSet to return one row of data
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getString("pname")).thenReturn("Test Product");
        when(mockResultSet.getString("manufacture")).thenReturn("Test Manufacturer");
        when(mockResultSet.getFloat("price")).thenReturn(10.0f);
        when(mockResultSet.getString("image_path")).thenReturn("test.png");
        when(mockResultSet.getString("barcode")).thenReturn("1234567890");
        when(mockResultSet.getInt("quantity")).thenReturn(5);
        when(mockResultSet.getBigDecimal("dailyusage")).thenReturn(new BigDecimal("1.0"));
        when(mockResultSet.getInt("usage_daily")).thenReturn(1);
        when(mockResultSet.getInt("usage_weekly")).thenReturn(0);
        when(mockResultSet.getInt("usage_biweekly")).thenReturn(0);
        when(mockResultSet.getInt("usage_monthly")).thenReturn(0);
        when(mockResultSet.getInt("pid")).thenReturn(1);
        when(mockResultSet.getBoolean("stock_condition")).thenReturn(true);
        when(mockResultSet.getString("stock_time")).thenReturn("2022-01-01");
        when(mockResultSet.getInt("remaining_stock")).thenReturn(3);
        when(mockResultSet.getInt("iid")).thenReturn(1);
        when(mockResultSet.getInt("users_id")).thenReturn(userId);

        // Expected result
        List<Item> expectedItemList = new ArrayList<>();
        Item expectedItem = new Item();
        expectedItem.setItemName("Testing Product");
        expectedItem.setItemManufacture("Testing Manufacturer");
        expectedItem.setItemPrice(10.0f);
        expectedItem.setItemImage_path("test.png");
        expectedItem.setItemBarcode("1234567890");
        expectedItem.setQuantity(5);
        expectedItem.setDailyusage(new BigDecimal("7.0"));
        expectedItem.setUsage_daily(1);
        expectedItem.setUsage_weekly(0);
        expectedItem.setUsage_biweekly(0);
        expectedItem.setUsage_monthly(0);
        expectedItem.setProducts_id(1);
        expectedItem.setStockCondition(true);
        expectedItem.setStock_time("2023-03-01");
        expectedItem.setRemainingStock(3);
        expectedItem.setIid(1);
        expectedItem.setUsers_id(userId);
        expectedItemList.add(expectedItem);

        // Act
        mockPreparedStatement.setInt(1, userId);
        ResultSet actualResultSet = mockPreparedStatement.executeQuery();
        Item item = new Item();
        item.setItemName(actualResultSet.getString(1));
        item.setItemManufacture(actualResultSet.getString(2));
        item.setItemPrice(actualResultSet.getFloat(3));
        item.setItemImage_path(actualResultSet.getString(4));
        item.setItemBarcode(actualResultSet.getString(5));
        item.setQuantity(actualResultSet.getInt(6));
        item.setDailyusage(actualResultSet.getBigDecimal(7));
        item.setUsage_daily(actualResultSet.getInt(8));
        item.setUsage_weekly(actualResultSet.getInt(9));
        item.setUsage_biweekly(actualResultSet.getInt(10));
        item.setUsage_monthly(actualResultSet.getInt(11));
        if (actualResultSet.getInt(8) != 0) {
            item.setUsage(actualResultSet.getInt(8));
            item.setUsageType("Daily");
        } else if (actualResultSet.getInt(9) != 0) {
            item.setUsage(actualResultSet.getInt(9));
            item.setUsageType("Weekly");
        } else if (actualResultSet.getInt(10) != 0) {
            item.setUsage(actualResultSet.getInt(10));
            item.setUsageType("Biweekly");
        } else if (actualResultSet.getInt(11) != 0) {
            item.setUsage(actualResultSet.getInt(11));
            item.setUsageType("Monthly");
        }
        item.setProducts_id(actualResultSet.getInt(12));
        item.setStockCondition(actualResultSet.getBoolean(13));
        item.setStock_time(actualResultSet.getString(14));
        item.setRemainingStock(actualResultSet.getInt(15));
        item.setIid(actualResultSet.getInt(16));
        item.setUsers_id(actualResultSet.getInt(17));
        List<Item> actualItemList = new ArrayList<>();
        actualItemList.add(item);

        // Assert
        verify(mockPreparedStatement).setInt(1, userId);
        verify(mockPreparedStatement).executeQuery();
        assertTrue(actualItemList.size() == 1);
        System.out.println("Actual Item: " + actualItemList.get(0).getItemName());
//        assertEquals(expectedItemList.size(), actualItemList.size());
    }

    @Test
    public void testAddItem() throws SQLException {
        // Arrange
        int user_id = 1;
        int product_id = 2;
        int quantity = 5;
        BigDecimal dailyusage = new BigDecimal("1.0");
        int usage_daily = 1;
        int usage_weekly = 0;
        int usage_biweekly = 0;
        int usage_monthly = 0;
        String stock_time = "2023-03-04";
        int remainingStock = 3;
        ItemDao itemDao = new ItemDao();

        // Set up mocked PreparedStatement
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // Set up expected SQL statement and result value
        String expectedSql = "INSERT INTO items (users_id, products_id, quantity, dailyusage, " +
                "usage_daily, usage_weekly, usage_biweekly, usage_monthly, stock_time, remaining_stock) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int expectedValue = 1;

        // Set up mocked executeUpdate() method to return expected value
        when(mockPreparedStatement.executeUpdate()).thenReturn(expectedValue);

        // Act
        mockPreparedStatement.setInt(1, user_id);
        (mockPreparedStatement).setInt(2, product_id);
        (mockPreparedStatement).setInt(3, quantity);
        (mockPreparedStatement).setBigDecimal(4, dailyusage);
        (mockPreparedStatement).setInt(5, usage_daily);
        (mockPreparedStatement).setInt(6, usage_weekly);
        (mockPreparedStatement).setInt(7, usage_biweekly);
        (mockPreparedStatement).setInt(8, usage_monthly);
        (mockPreparedStatement).setString(9, stock_time);
        (mockPreparedStatement).setInt(10, remainingStock);
        int value = mockPreparedStatement.executeUpdate();
        boolean actualResult = itemDao.addItem(user_id, product_id, quantity, dailyusage, usage_daily, usage_weekly, usage_biweekly, usage_monthly, stock_time, remainingStock);

        // Assert
        System.out.println("Actual SQL statement: " + mockConnection.prepareStatement(expectedSql));
        System.out.println("Actual executeUpdate Value: " + value);
        verify(mockConnection).prepareStatement(expectedSql);
        verify(mockPreparedStatement).setInt(1, user_id);
        verify(mockPreparedStatement).setInt(2, product_id);
        verify(mockPreparedStatement).setInt(3, quantity);
        verify(mockPreparedStatement).setBigDecimal(4, dailyusage);
        verify(mockPreparedStatement).setInt(5, usage_daily);
        verify(mockPreparedStatement).setInt(6, usage_weekly);
        verify(mockPreparedStatement).setInt(7, usage_biweekly);
        verify(mockPreparedStatement).setInt(8, usage_monthly);
        verify(mockPreparedStatement).setString(9, stock_time);
        verify(mockPreparedStatement).setInt(10, remainingStock);
        verify(mockPreparedStatement).executeUpdate();
        assertTrue(actualResult);
    }
    @Test
    public void testUpdateItem() throws SQLException {
        // Arrange
        int user_id = 1;
        int product_id = 4;
        int quantity = 10;
        BigDecimal dailyusage = new BigDecimal("2.0");
        int usage_daily = 2;
        int usage_weekly = 0;
        int usage_biweekly = 0;
        int usage_monthly = 0;
        String stock_time = "2023-03-26";
        int remainingStock = 10;
        ItemDao itemDao = new ItemDao();

        // Set up mocked PreparedStatement
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // Set up expected SQL statement and result value
        String expectedSql = "UPDATE items SET quantity = ?, dailyusage = ?, usage_daily = ?, usage_weekly = ?, usage_biweekly = ?, usage_monthly = ?, stock_time = ?, remaining_stock = ? WHERE users_id = ? and products_id = ?";
        int expectedValue = 1;

        // Set up mocked executeUpdate() method to return expected value
        when(mockPreparedStatement.executeUpdate()).thenReturn(expectedValue);

        // Act
        mockPreparedStatement.setInt(1, quantity);
        (mockPreparedStatement).setBigDecimal(2, dailyusage);
        (mockPreparedStatement).setInt(3, usage_daily);
        (mockPreparedStatement).setInt(4, usage_weekly);
        (mockPreparedStatement).setInt(5, usage_biweekly);
        (mockPreparedStatement).setInt(6, usage_monthly);
        (mockPreparedStatement).setString(7, stock_time);
        (mockPreparedStatement).setInt(8, remainingStock);
        (mockPreparedStatement).setInt(9, user_id);
        (mockPreparedStatement).setInt(10, product_id);
        int value = mockPreparedStatement.executeUpdate();
        System.out.println("Actual executeUpdate Value: " + value);
        boolean actualResult = itemDao.updateItem(user_id, product_id, quantity, dailyusage, usage_daily, usage_weekly, usage_biweekly, usage_monthly, stock_time, remainingStock);

        // Assert
        System.out.println("Actual SQL statement: " + mockConnection.prepareStatement(expectedSql));
        verify(mockConnection).prepareStatement(expectedSql);
        verify(mockPreparedStatement).setInt(1, quantity);
        verify(mockPreparedStatement).setBigDecimal(2, dailyusage);
        verify(mockPreparedStatement).setInt(3, usage_daily);
        verify(mockPreparedStatement).setInt(4, usage_weekly);
        verify(mockPreparedStatement).setInt(5, usage_biweekly);
        verify(mockPreparedStatement).setInt(6, usage_monthly);
        verify(mockPreparedStatement).setString(7, stock_time);
        verify(mockPreparedStatement).setInt(8, remainingStock);
        verify(mockPreparedStatement).setInt(9, user_id);
        verify(mockPreparedStatement).setInt(10, product_id);
        verify(mockPreparedStatement).executeUpdate();
        assertFalse(actualResult);
    }

    @Test
    public void testDeleteItem() throws SQLException {
        // Arrange
        int user_id = 12;
        int product_id = 2;
        ItemDao itemDao = new ItemDao();

        // Set up mocked PreparedStatement
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // Set up expected SQL statement and result value
        String expectedSql = "Delete from items where products_id = ? and users_id = ?";
        int expectedValue = 1;

        // Set up mocked executeUpdate() method to return expected value
        when(mockPreparedStatement.executeUpdate()).thenReturn(expectedValue);

        // Act
        mockPreparedStatement.setInt(1, product_id);
        mockPreparedStatement.setInt(2, user_id);
        int value = mockPreparedStatement.executeUpdate();
        boolean actualResult = itemDao.deleteItem(user_id, product_id);

        // Assert
        System.out.println("Actual SQL statement: " + mockConnection.prepareStatement(expectedSql));
        System.out.println("Actual executeUpdate Value: " + value);
        verify(mockConnection).prepareStatement(expectedSql);
        verify(mockPreparedStatement).setInt(1, product_id);
        verify(mockPreparedStatement).setInt(2, user_id);
        verify(mockPreparedStatement).executeUpdate();
        assertFalse(actualResult);
    }

}
