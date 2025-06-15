package com.gmail.merikbest2015.ecommerce.domain;

import com.gmail.merikbest2015.ecommerce.enums.AuthProvider;
import com.gmail.merikbest2015.ecommerce.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    Set<Role> roles = new HashSet<>();

    @BeforeEach
    public void setUp() {
        roles.add(Role.USER);
        roles.add(Role.ADMIN);
    }


    @Test
    public void testValidUserCreation() {
        User user = new User(
                "test@example.com",
                "password123",
                "John",
                "Doe",
                "New York",
                "123 Main St",
                "+1 555-123-4567",
                "12345",
                "activation-code",
                "reset-code",
                true,
                AuthProvider.LOCAL,
                roles
        );

        assertEquals("test@example.com", user.getEmail());
        assertEquals("+1 555-123-4567", user.getPhoneNumber());
        assertEquals("12345", user.getPostIndex());
        assertTrue(user.isActive());
        assertNotNull(user.getRoles());
    }

    @Test
    public void testInvalidEmailNull_ThrowsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new User(
                null, "password", "John", "Doe", "City", "Address",
                "+15551234567", "12345", "activation", "reset", true,
                AuthProvider.LOCAL, roles
        ));
        assertEquals("Email must not be blank.", ex.getMessage());
    }

    @Test
    public void testInvalidEmailFormat_ThrowsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new User(
                "bademail.com", "password", "John", "Doe", "City", "Address",
                "+15551234567", "12345", "activation", "reset", true,
                AuthProvider.LOCAL, roles
        ));
        assertEquals("Email format is invalid.", ex.getMessage());
    }

    @Test
    public void testValidPhoneNumberNull_Allows() {
        User user = new User(
                "test@example.com", "password", "John", "Doe", "City", "Address",
                null, "12345", "activation", "reset", true,
                AuthProvider.LOCAL,roles
        );
        assertNull(user.getPhoneNumber());
    }

    @Test
    public void testInvalidPhoneNumber_ThrowsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new User(
                "test@example.com", "password", "John", "Doe", "City", "Address",
                "invalid-phone!", "12345", "activation", "reset", true,
                AuthProvider.LOCAL, roles
        ));
        assertEquals("Phone number format is invalid.", ex.getMessage());
    }

    @Test
    public void testValidPostIndexNull_Allows() {
        User user = new User(
                "test@example.com", "password", "John", "Doe", "City", "Address",
                "+15551234567", null, "activation", "reset", true,
                AuthProvider.LOCAL, roles
        );
        assertNull(user.getPostIndex());
    }

    @Test
    public void testInvalidPostIndexTooLong_ThrowsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new User(
                "test@example.com", "password", "John", "Doe", "City", "Address",
                "+15551234567", "12345678901", "activation", "reset", true,
                AuthProvider.LOCAL, roles
        ));
        assertEquals("Post index is too long.", ex.getMessage());
    }
}