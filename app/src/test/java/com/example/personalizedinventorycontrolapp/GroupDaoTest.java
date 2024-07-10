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

import com.example.personalizedinventorycontrolapp.dao.GroupDao;
import com.example.personalizedinventorycontrolapp.entity.Group;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GroupDaoTest {
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
    public void testFindGroup() throws SQLException {
        // Arrange
        String email = "rhschiu@gmail.com";
        int expectedGroupid = 1;
        GroupDao groupDao = new GroupDao();

        // Set up mocked PreparedStatement and ResultSet
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        // Set up expected SQL statement and result set
        String expectedSql = "SELECT gid from groupings where email = ?";
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(expectedGroupid);

        // Act
        mockPreparedStatement.setString(1, email);
        ResultSet actualResultSet = mockPreparedStatement.executeQuery();
        int actualResultGroupID = actualResultSet.getInt(1);
        Group actualGroup = groupDao.findGroup(email);

        // Assert
        System.out.println("Actual SQL statement: " + mockConnection.prepareStatement(expectedSql));
        verify(mockConnection).prepareStatement(expectedSql);
        verify(mockPreparedStatement).setString(1, email);
        verify(mockPreparedStatement).executeQuery();
        assertEquals(expectedGroupid, actualGroup.getGid());
        assertEquals(actualResultGroupID, actualGroup.getGid());
    }

    @Test
    public void testList() throws SQLException {
        // Arrange
        int groupings_id = 1;
        GroupDao groupDao = new GroupDao();

        // Set up mocked PreparedStatement and ResultSet
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        // Set up expected SQL statement and result set
        String expectedSql = "SELECT g.email AS 'Group Name', ug.groupings_id, ug.users_id, u.email " +
                "FROM users u JOIN users_groupings ug ON u.uid=ug.users_id JOIN " +
                "groupings g ON g.gid=ug.groupings_id where groupings_id = ? AND g.email <> u.email";
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getString(1)).thenReturn("Test Group");
        when(mockResultSet.getInt(2)).thenReturn(1);
        when(mockResultSet.getInt(3)).thenReturn(2);
        when(mockResultSet.getString(4)).thenReturn("test@example.com");

        // Act
        mockPreparedStatement.setInt(1, groupings_id);
        ResultSet actualResultSet = mockPreparedStatement.executeQuery();
        List<Group> actualResultList = new ArrayList<>();
        while(actualResultSet.next()){
            Group group = new Group();
            group.setGroupName(actualResultSet.getString(1));
            group.setGid(actualResultSet.getInt(2));
            group.setUsers_id(actualResultSet.getInt(3));
            group.setgEmail(actualResultSet.getString(4));
            actualResultList.add(group);
        }
        List<Group> actualList = groupDao.list(groupings_id);

        // Assert
        System.out.println("Actual SQL statement: " + mockConnection.prepareStatement(expectedSql));
        verify(mockConnection).prepareStatement(expectedSql);
        verify(mockPreparedStatement).setInt(1, groupings_id);
        verify(mockPreparedStatement).executeQuery();
        assertEquals(3, actualList.size());
        assertEquals("rhschiu@gmail.com", actualList.get(0).getGroupName());
        assertEquals(1, actualList.get(0).getGid());
        assertEquals(2, actualList.get(0).getUsers_id());
        assertEquals("comp1234@gmail.com", actualList.get(0).getgEmail());
    }

    @Test
    public void testAddUsersToGroup() throws SQLException {
        // Arrange
        int groupings_id = 12;
        int users_id = 1;
        GroupDao groupDao = new GroupDao();

        // Set up mocked PreparedStatement
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // Set up expected SQL statement and update count
        String expectedSql = "INSERT INTO users_groupings (groupings_id, users_id) VALUES (?,?)";
        int expectedUpdateCount = 1;
        when(mockPreparedStatement.executeUpdate()).thenReturn(expectedUpdateCount);

        // Act
        (mockPreparedStatement).setInt(1, groupings_id);
        (mockPreparedStatement).setInt(2, users_id);
        int value = mockPreparedStatement.executeUpdate();
        boolean actualResult = groupDao.addUsersToGroup(groupings_id, users_id);
        System.out.println("Actual SQL statement: " + mockConnection.prepareStatement(expectedSql));
        System.out.println("Actual executeUpdate Value: " + value);
        // Assert
        verify(mockConnection).prepareStatement(expectedSql);
        verify(mockPreparedStatement).setInt(1, groupings_id);
        verify(mockPreparedStatement).setInt(2, users_id);
        verify(mockPreparedStatement).executeUpdate();
        assertFalse(actualResult);
    }



}
