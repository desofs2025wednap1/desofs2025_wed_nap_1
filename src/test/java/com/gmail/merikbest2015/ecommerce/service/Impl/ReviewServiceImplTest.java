package com.gmail.merikbest2015.ecommerce.service.Impl;

import com.gmail.merikbest2015.ecommerce.domain.Perfume;
import com.gmail.merikbest2015.ecommerce.domain.Review;
import com.gmail.merikbest2015.ecommerce.repository.PerfumeRepository;
import com.gmail.merikbest2015.ecommerce.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class ReviewServiceImplTest {

    @Autowired
    private ReviewServiceImpl reviewService;

    @MockitoBean
    private PerfumeRepository perfumeRepository;

    @MockitoBean
    private ReviewRepository reviewRepository;

    @Test
    public void addReviewToPerfume() {
        List<Review> reviewList = new ArrayList<>();
        Review review = new Review();
        review.setRating(5);
        reviewList.add(review);
        Perfume perfume = new Perfume();
        perfume.setId(123L);
        perfume.setReviews(reviewList);

        when(perfumeRepository.findById(123L)).thenReturn(Optional.of(perfume));
        when(reviewRepository.save(review)).thenReturn(review);
        reviewService.addReviewToPerfume(review, 123L);
        assertEquals(123L, perfume.getId());
        assertNotNull(perfume.getReviews());
        verify(perfumeRepository, times(1)).findById(123L);
        verify(reviewRepository, times(1)).save(review);
    }
}
