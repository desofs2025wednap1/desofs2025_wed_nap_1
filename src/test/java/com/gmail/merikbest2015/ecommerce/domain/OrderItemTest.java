package com.gmail.merikbest2015.ecommerce.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class OrderItemTest {

    @Test
    void constructor_shouldCreateOrderItem_whenValidInputs() {
        Perfume mockPerfume = mock(Perfume.class);
        Long amount = 100L;
        Long quantity = 2L;

        OrderItem item = new OrderItem(amount, quantity, mockPerfume);

        assertEquals(amount, item.getAmount());
        assertEquals(quantity, item.getQuantity());
        assertEquals(mockPerfume, item.getPerfume());
    }

    @Test
    void constructor_shouldThrowException_whenAmountIsNull() {
        Perfume mockPerfume = mock(Perfume.class);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new OrderItem(null, 1L, mockPerfume));
        assertEquals("Amount must be non-null and non-negative.", exception.getMessage());
    }

    @Test
    void constructor_shouldThrowException_whenAmountIsNegative() {
        Perfume mockPerfume = mock(Perfume.class);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new OrderItem(-1L, 1L, mockPerfume));
        assertEquals("Amount must be non-null and non-negative.", exception.getMessage());
    }

    @Test
    void constructor_shouldThrowException_whenQuantityIsNull() {
        Perfume mockPerfume = mock(Perfume.class);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new OrderItem(10L, null, mockPerfume));
        assertEquals("Quantity must be non-null and positive.", exception.getMessage());
    }

    @Test
    void constructor_shouldThrowException_whenQuantityIsZeroOrNegative() {
        Perfume mockPerfume = mock(Perfume.class);

        IllegalArgumentException exceptionZero = assertThrows(IllegalArgumentException.class, () -> new OrderItem(10L, 0L, mockPerfume));
        assertEquals("Quantity must be non-null and positive.", exceptionZero.getMessage());

        IllegalArgumentException exceptionNegative = assertThrows(IllegalArgumentException.class, () -> new OrderItem(10L, -5L, mockPerfume));
        assertEquals("Quantity must be non-null and positive.", exceptionNegative.getMessage());
    }

    @Test
    void constructor_shouldThrowException_whenPerfumeIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new OrderItem(10L, 1L, null));
        assertEquals("Perfume must not be null.", exception.getMessage());
    }
}