package com.example.personalizedinventorycontrolapp;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.personalizedinventorycontrolapp.entity.User;

public class UserTest {
    @Test
    public void testGetUsername() {
        User user = new User(1, "johndoe", "password", "johndoe@example.com", "token");
        assertEquals("johndoe", user.getUsername());
    }
}
