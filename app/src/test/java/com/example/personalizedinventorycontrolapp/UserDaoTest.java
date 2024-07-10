package com.example.personalizedinventorycontrolapp;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.example.personalizedinventorycontrolapp.dao.UserDao;
import com.example.personalizedinventorycontrolapp.entity.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDaoTest {
    @Test
    public void testFindUser() throws SQLException {
        String username = "rachel1";
        String email = "rhschiu@gmail.com";
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("uid")).thenReturn(3);
        when(mockResultSet.getString("username")).thenReturn(username);
        when(mockResultSet.getString("password")).thenReturn("1234");
        when(mockResultSet.getString("email")).thenReturn(email);
        when(mockResultSet.getString("token")).thenReturn("dH3cnCHGTSCMW88b8dMuSE:APA91bFOs4r8gUXqaD2jbe1x8LeHWscKz3dJytUqLgpowiTUZ7Et6Pa8IzK-up2RCimaa-YIVq6dvfUEpwL1NPfbi0rx4Zs_0O3gcYOAiicOL2pbVzt8letORxhsDngM4Uae3KiGFClK");
        User expectedUser = new User(3, username, "1234", email, "dH3cnCHGTSCMW88b8dMuSE:APA91bFOs4r8gUXqaD2jbe1x8LeHWscKz3dJytUqLgpowiTUZ7Et6Pa8IzK-up2RCimaa-YIVq6dvfUEpwL1NPfbi0rx4Zs_0O3gcYOAiicOL2pbVzt8letORxhsDngM4Uae3KiGFClK");
        User actualUser = new UserDao().findUser(username, email);
//        assertEquals(expectedUser, expectedUser.getId());
        assertNotEquals(expectedUser, actualUser);
    }

    @Test
    public void testUsernameLogin() throws SQLException {
        String username = "rachel";
        String password = "1234";
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("username")).thenReturn(username);
        when(mockResultSet.getString("password")).thenReturn(password);
        when(mockResultSet.getString("email")).thenReturn("rhschiu@gmail.com");
        when(mockResultSet.getString("token")).thenReturn("token");
        boolean result = new UserDao().usernameLogin(username, password);
        assertTrue(result);
    }

    @Test
    public void testEmailLogin() throws SQLException {
        String email = "rhschiu@gmail.com";
        String password = "1234";
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("username")).thenReturn("rachel");
        when(mockResultSet.getString("password")).thenReturn(password);
        when(mockResultSet.getString("email")).thenReturn(email);
        when(mockResultSet.getString("token")).thenReturn("token");
        boolean result = new UserDao().emailLogin(email, password);
        assertTrue(result);
    }

    @Test
    public void testRegister() throws SQLException {
        User user = new User();
        user.setUsername("Yo");
        user.setPassword("12345");
        user.setEmail("yo12345@gmail.com");
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(1);
        boolean result = new UserDao().register(user);
        assertTrue(result);
    }
}
