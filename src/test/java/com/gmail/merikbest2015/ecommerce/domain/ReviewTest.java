package com.gmail.merikbest2015.ecommerce.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ReviewTest {


    @Test
    public void testConstructor_validInputs_shouldCreateReview() {
        LocalDate date = LocalDate.of(2023, 5, 1);
        Review review = new Review("Alice", "Great scent", 4, date);

        assertEquals("Alice", review.getAuthor());
        assertEquals("Great scent", review.getMessage());
        assertEquals(4, review.getRating());
        assertEquals(date, review.getDate());
    }

    @Test
    public void testConstructor_nullDate_shouldSetToNow() {
        Review review = new Review("Bob", "Nice!", 3, null);
        assertEquals(LocalDate.now(), review.getDate());
    }

    @Test
    public void testConstructor_blankAuthor_shouldThrow() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new Review(" ", "Good", 3, LocalDate.now()));
        assertEquals("Author must not be blank.", ex.getMessage());
    }

    @Test
    public void testConstructor_blankMessage_shouldThrow() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new Review("Alice", "   ", 3, LocalDate.now()));
        assertEquals("Message must not be blank.", ex.getMessage());
    }

    @Test
    public void testConstructor_invalidRating_shouldThrow() {
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class,
                () -> new Review("Alice", "Good", 0, LocalDate.now()));
        assertEquals("Rating must be between 1 and 5.", ex1.getMessage());

        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class,
                () -> new Review("Alice", "Good", 6, LocalDate.now()));
        assertEquals("Rating must be between 1 and 5.", ex2.getMessage());

        IllegalArgumentException ex3 = assertThrows(IllegalArgumentException.class,
                () -> new Review("Alice", "Good", null, LocalDate.now()));
        assertEquals("Rating must be between 1 and 5.", ex3.getMessage());
    }

    @Test
    public void testConstructor_futureDate_shouldThrow() {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new Review("Alice", "Good", 4, futureDate));
        assertEquals("Date cannot be in the future.", ex.getMessage());
    }

    @Test
    public void testSetters_validValues_shouldSet() {
        Review review = new Review();

        review.setAuthor("John");
        assertEquals("John", review.getAuthor());

        review.setMessage("Excellent");
        assertEquals("Excellent", review.getMessage());

        review.setRating(5);
        assertEquals(5, review.getRating());

        LocalDate date = LocalDate.of(2020, 1, 1);
        review.setDate(date);
        assertEquals(date, review.getDate());
    }

}