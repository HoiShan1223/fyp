package com.example.personalizedinventorycontrolapp;

import com.example.personalizedinventorycontrolapp.dao.ShoppingListDao;
import com.example.personalizedinventorycontrolapp.entity.ShoppingList;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.anyString;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ShoppingListDaoTest {
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
    public void testGetShoppingList() throws SQLException {
        // Arrange
        int users_id = 11;
        ShoppingListDao shoppingListDao = new ShoppingListDao();

        // Set up mocked PreparedStatement and ResultSet
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        // Set up expected SQL statement and result set
        String expectedSql = "SELECT sid, s.users_id, s.products_id, s.items_id, p.pname, p.manufacture, item_purchase_price, item_purchase_quantity, " +
                "p.image_path, i.remaining_stock, restock FROM users u JOIN shoppingLists s ON u.uid = s.users_id JOIN products p " +
                "ON p.pid = s.products_id JOIN items i ON i.iid = s.items_id WHERE s.users_id = ?";
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt(1)).thenReturn(79);
        when(mockResultSet.getInt(2)).thenReturn(11);
        when(mockResultSet.getInt(3)).thenReturn(1);
        when(mockResultSet.getInt(4)).thenReturn(39);
        when(mockResultSet.getString(5)).thenReturn("Vitasoy Soyabean Milk 250ml");
        when(mockResultSet.getString(6)).thenReturn("Vitasoy");
        when(mockResultSet.getFloat(7)).thenReturn(20f);
        when(mockResultSet.getInt(8)).thenReturn(4);
        when(mockResultSet.getString(9)).thenReturn("https://www.barcodeplus.com.hk/eid/resource/libx/dfile/gtin:84/4899950000019/56/4891028164395-1.jpg");
        when(mockResultSet.getInt(10)).thenReturn(-2);
        when(mockResultSet.getBoolean(11)).thenReturn(false);

        // Act
        mockPreparedStatement.setInt(1, users_id);
        ResultSet actualResultSet = mockPreparedStatement.executeQuery();
        List<ShoppingList> actualList = shoppingListDao.getShoppingList(users_id);

        // Assert
        System.out.println("Actual SQL statement: " + mockConnection.prepareStatement(expectedSql));
        verify(mockConnection).prepareStatement(expectedSql);
        verify(mockPreparedStatement).setInt(1, users_id);
        verify(mockPreparedStatement).executeQuery();
        assertEquals(2, actualList.size());
        assertEquals(79, actualList.get(0).getShoppinglistId());
        assertEquals(11, actualList.get(0).getUsers_id());
        assertEquals(1, actualList.get(0).getProducts_id());
        assertEquals(39, actualList.get(0).getItems_id());
        assertEquals("Vitasoy Soyabean Milk 250ml", actualList.get(0).getItemName());
        assertEquals("Vitasoy", actualList.get(0).getItemManufacture());
        assertEquals(20f, actualList.get(0).getItemPurchasePrice(), 0.01);
        assertEquals(4, actualList.get(0).getPurchaseQuantity());
        assertEquals("https://www.barcodeplus.com.hk/eid/resource/libx/dfile/gtin:84/4899950000019/56/4891028164395-1.jpg", actualList.get(0).getItemImage_path());
        assertEquals(-2, actualList.get(0).getRemainingStock());
        assertFalse(actualList.get(0).isRestock());
    }

    @Test
    public void testAddItemToShoppingListDB() throws SQLException {
        // Arrange
        int userid = 4;
        int pid = 2;
        int iid = 9;
        float item_purchase_price = 20f;
        int item_purchase_quantity = 1;
        boolean restock = false;
        ShoppingListDao shoppingListDao = new ShoppingListDao();

        // Set up mocked PreparedStatement
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // Set up expected SQL statement and update count
        String expectedSql = "INSERT INTO shoppingLists (users_id, products_id, items_id, item_purchase_price, item_purchase_quantity, restock) VALUES (?, ?, ?, ?, ?, ?)";
        int expectedUpdateCount = 1;
        when(mockPreparedStatement.executeUpdate()).thenReturn(expectedUpdateCount);

        // Act
        (mockPreparedStatement).setInt(1, userid);
        (mockPreparedStatement).setInt(2, pid);
        (mockPreparedStatement).setInt(3, iid);
        (mockPreparedStatement).setFloat(4, item_purchase_price);
        (mockPreparedStatement).setInt(5, item_purchase_quantity);
        (mockPreparedStatement).setInt(6, 0);
        int value = mockPreparedStatement.executeUpdate();
        boolean actualResult = shoppingListDao.addItemToShoppingListDB(userid, pid, iid, item_purchase_price, item_purchase_quantity, false);

        // Assert
        System.out.println("Actual SQL statement: " + mockConnection.prepareStatement(expectedSql));
        System.out.println("Actual executeUpdate Value: " + value);
        verify(mockConnection).prepareStatement(expectedSql);
        verify(mockPreparedStatement).setInt(1, userid);
        verify(mockPreparedStatement).setInt(2, pid);
        verify(mockPreparedStatement).setInt(3, iid);
        verify(mockPreparedStatement).setFloat(4, item_purchase_price);
        verify(mockPreparedStatement).setInt(5, item_purchase_quantity);
        verify(mockPreparedStatement).setInt(6, 0);
        verify(mockPreparedStatement).executeUpdate();
        assertTrue(actualResult);
    }

    @Test
    public void testDeleteItemFromShoppingList() throws SQLException {
        // Arrange
        int user_id = 4;
        int products_id = 2;
        int items_id = 9;
        ShoppingListDao shoppingListDao = new ShoppingListDao();

        // Set up mocked PreparedStatement
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // Set up expected SQL statement and update count
        String expectedSql = "Delete from shoppingLists where users_id = ? and products_id = ? and items_id = ?";
        int expectedUpdateCount = 1;
        when(mockPreparedStatement.executeUpdate()).thenReturn(expectedUpdateCount);

        // Act
        (mockPreparedStatement).setInt(1, user_id);
        (mockPreparedStatement).setInt(2, products_id);
        (mockPreparedStatement).setInt(3, items_id);
        int value = mockPreparedStatement.executeUpdate();
        boolean actualResult = shoppingListDao.deleteItemFromShoppingList(user_id, products_id, items_id);

        // Assert
        System.out.println("Actual SQL statement: " + mockConnection.prepareStatement(expectedSql));
        System.out.println("Actual executeUpdate Value: " + value);
        verify(mockConnection).prepareStatement(expectedSql);
        verify(mockPreparedStatement).setInt(1, user_id);
        verify(mockPreparedStatement).setInt(2, products_id);
        verify(mockPreparedStatement).setInt(3, items_id);
        verify(mockPreparedStatement).executeUpdate();
        assertTrue(actualResult);
    }

}
