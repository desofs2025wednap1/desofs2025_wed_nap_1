package com.gmail.merikbest2015.ecommerce.domain;

import static com.gmail.merikbest2015.ecommerce.util.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.gmail.merikbest2015.ecommerce.dto.GraphQLRequest;
import com.gmail.merikbest2015.ecommerce.dto.perfume.PerfumeRequest;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

public class PerfumeTest {

    private Perfume perfume;

    @BeforeEach
    public void setUp() {
        perfume = new Perfume();
    }

    @Test
    public void testAddReviewAddsToList() {
        Review review = mock(Review.class);

        assertTrue(perfume.getReviews() == null || perfume.getReviews().isEmpty());

        perfume.addReview(review);

        assertNotNull(perfume.getReviews());
        assertEquals(1, perfume.getReviews().size());
        assertTrue(perfume.getReviews().contains(review));
    }

    @Test
    public void testReviewExistsReturnsTrueWhenReviewFound() {
        Review review = mock(Review.class);
        when(review.getId()).thenReturn(1L);

        perfume.addReview(review);

        assertTrue(perfume.reviewExists(1L));
    }

    @Test
    public void testReviewExistsReturnsFalseWhenNotFound() {
        Review review = mock(Review.class);
        when(review.getId()).thenReturn(2L);

        perfume.addReview(review);

        assertFalse(perfume.reviewExists(999L));
    }

    @Test
    public void testReviewExistsOnEmptyListReturnsFalse() {
        perfume.setReviews(new ArrayList<>());
        assertFalse(perfume.reviewExists(1L));
    }

    @Test
    public void testAddReviewInitializesListIfNull() {
        Review review = mock(Review.class);
        perfume.setReviews(null);

        perfume.addReview(review);

        assertNotNull(perfume.getReviews());
        assertEquals(1, perfume.getReviews().size());
    }

    @Test
    public void testValidPerfumeCreation() {
        assertDoesNotThrow(() -> new Perfume(
                "Sauvage", "Dior", 2020, "FR",
                "Male", "Bergamot", "Lavender", "Ambroxan",
                "Fresh and spicy", "sauvage.jpg", 120,
                "100ml", "Eau de Toilette", 4.5
        ));
    }

    @Test
    public void testNullTitleThrowsException() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new Perfume(null, "Dior", 2020, "FR",
                        "Male", "Bergamot", "Lavender", "Ambroxan",
                        "Fresh", "file.jpg", 100, "100ml", "EDT", 4.0)
        );
        assertEquals("Perfume title must not be blank.", ex.getMessage());
    }

    @Test
    public void testBlankTitleThrowsException() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new Perfume("  ", "Dior", 2020, "FR",
                        "Male", "Top", "Mid", "Base",
                        "Desc", "file.jpg", 100, "100ml", "EDT", 4.0)
        );
        assertEquals("Perfume title must not be blank.", ex.getMessage());
    }

    @Test
    public void testInvalidYearThrowsException() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new Perfume("Title", "Dior", 1500, "FR",
                        "Male", "Top", "Mid", "Base",
                        "Desc", "file.jpg", 100, "100ml", "EDT", 4.0)
        );
        assertEquals("Year must be between 1800 and 2100.", ex.getMessage());
    }

    @Test
    public void testInvalidCountryThrowsException() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new Perfume("Title", "Dior", 2020, "INVALID",
                        "Male", "Top", "Mid", "Base",
                        "Desc", "file.jpg", 100, "100ml", "EDT", 4.0)
        );
        assertEquals("Country must be a valid ISO country code (e.g., 'US', 'FR').", ex.getMessage());
    }

    @Test
    public void testInvalidRatingThrowsException() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                new Perfume("Title", "Dior", 2020, "FR",
                        "Male", "Top", "Mid", "Base",
                        "Desc", "file.jpg", 100, "100ml", "EDT", 6.0)
        );
        assertEquals("Rating must be between 0 and 5.", ex.getMessage());
    }

    @Test
    public void testNullRatingIsAllowed() {
        assertDoesNotThrow(() -> new Perfume(
                "Title", "Dior", 2020, "FR",
                "Male", "Top", "Mid", "Base",
                "Desc", "file.jpg", 100, "100ml", "EDT", null
        ));
    }
}