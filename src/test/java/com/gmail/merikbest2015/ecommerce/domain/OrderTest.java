package com.gmail.merikbest2015.ecommerce.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    @Test
    public void testValidOrderCreation() {
        LocalDate today = LocalDate.now();
        Order order = new Order(
                100.0,
                today,
                "John",
                "Doe",
                "New York",
                "123 Main St",
                "john.doe@example.com",
                "+1234567890",
                12345
        );

        assertEquals(100.0, order.getTotalPrice());
        assertEquals(today, order.getDate());
        assertEquals("John", order.getFirstName());
        assertEquals("Doe", order.getLastName());
        assertEquals("New York", order.getCity());
        assertEquals("123 Main St", order.getAddress());
        assertEquals("john.doe@example.com", order.getEmail());
        assertEquals("+1234567890", order.getPhoneNumber());
        assertEquals(12345, order.getPostIndex());
        assertNotNull(order.getOrderItems());
        assertTrue(order.getOrderItems().isEmpty());
    }

    @Test
    public void testNullTotalPriceThrows() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new Order(null, LocalDate.now(), "John", "Doe", "City", "Address", "email@example.com", "+1234567890", 123)
        );
        assertEquals("Total price must be non-null and non-negative.", ex.getMessage());
    }

    @Test
    public void testNegativeTotalPriceThrows() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new Order(-10.0, LocalDate.now(), "John", "Doe", "City", "Address", "email@example.com", "+1234567890", 123)
        );
        assertEquals("Total price must be non-null and non-negative.", ex.getMessage());
    }

    @Test
    public void testNullDateThrows() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new Order(10.0, null, "John", "Doe", "City", "Address", "email@example.com", "+1234567890", 123)
        );
        assertEquals("Date must not be null.", ex.getMessage());
    }

    @Test
    public void testFutureDateThrows() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new Order(10.0, LocalDate.now().plusDays(1), "John", "Doe", "City", "Address", "email@example.com", "+1234567890", 123)
        );
        assertEquals("Date cannot be in the future.", ex.getMessage());
    }

    @Test
    public void testBlankFirstNameThrows() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new Order(10.0, LocalDate.now(), " ", "Doe", "City", "Address", "email@example.com", "+1234567890", 123)
        );
        assertEquals("First name must not be blank.", ex.getMessage());
    }

    @Test
    public void testBlankLastNameThrows() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new Order(10.0, LocalDate.now(), "John", " ", "City", "Address", "email@example.com", "+1234567890", 123)
        );
        assertEquals("Last name must not be blank.", ex.getMessage());
    }

    @Test
    public void testBlankCityThrows() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new Order(10.0, LocalDate.now(), "John", "Doe", "", "Address", "email@example.com", "+1234567890", 123)
        );
        assertEquals("City must not be blank.", ex.getMessage());
    }

    @Test
    public void testBlankAddressThrows() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new Order(10.0, LocalDate.now(), "John", "Doe", "City", " ", "email@example.com", "+1234567890", 123)
        );
        assertEquals("Address must not be blank.", ex.getMessage());
    }

    @Test
    public void testInvalidEmailThrows() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new Order(10.0, LocalDate.now(), "John", "Doe", "City", "Address", "invalid-email", "+1234567890", 123)
        );
        assertEquals("Email format is invalid.", ex.getMessage());
    }

    @Test
    public void testBlankEmailThrows() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new Order(10.0, LocalDate.now(), "John", "Doe", "City", "Address", " ", "+1234567890", 123)
        );
        assertEquals("Email must not be blank.", ex.getMessage());
    }

    @Test
    public void testInvalidPhoneNumberThrows() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new Order(10.0, LocalDate.now(), "John", "Doe", "City", "Address", "email@example.com", "abc123", 123)
        );
        assertEquals("Phone number format is invalid.", ex.getMessage());
    }

    @Test
    public void testBlankPhoneNumberThrows() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new Order(10.0, LocalDate.now(), "John", "Doe", "City", "Address", "email@example.com", " ", 123)
        );
        assertEquals("Phone number must not be blank.", ex.getMessage());
    }

    @Test
    public void testNullOrNegativePostIndexThrows() {
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () ->
                new Order(10.0, LocalDate.now(), "John", "Doe", "City", "Address", "email@example.com", "+1234567890", null)
        );
        assertEquals("Post index must be positive.", ex1.getMessage());

        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () ->
                new Order(10.0, LocalDate.now(), "John", "Doe", "City", "Address", "email@example.com", "+1234567890", -1)
        );
        assertEquals("Post index must be positive.", ex2.getMessage());
    }
}