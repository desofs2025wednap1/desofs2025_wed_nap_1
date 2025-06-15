package com.gmail.merikbest2015.ecommerce.controller;

import com.gmail.merikbest2015.ecommerce.dto.review.ReviewRequest;
import com.gmail.merikbest2015.ecommerce.dto.review.ReviewResponse;
import com.gmail.merikbest2015.ecommerce.mapper.ReviewMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.gmail.merikbest2015.ecommerce.constants.PathConstants.API_V1_REVIEW;
import static com.gmail.merikbest2015.ecommerce.constants.PathConstants.PERFUME_ID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@RestController
@RequiredArgsConstructor
@RequestMapping(API_V1_REVIEW)
public class ReviewController {

    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

    private final ReviewMapper reviewMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping(PERFUME_ID)
    public ResponseEntity<List<ReviewResponse>> getReviewsByPerfumeId(@PathVariable Long perfumeId) {
        logger.info("Fetching reviews for perfumeId={}", perfumeId);
        List<ReviewResponse> reviews = reviewMapper.getReviewsByPerfumeId(perfumeId);
        logger.info("Found {} reviews", reviews.size());
        return ResponseEntity.ok(reviews);
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> addReviewToPerfume(@Valid @RequestBody ReviewRequest reviewRequest,
                                                             BindingResult bindingResult) {
        logger.info("Adding review for perfumeId={}, userId={}", reviewRequest.getPerfumeId(), reviewRequest.getPerfumeId());
        ReviewResponse review = reviewMapper.addReviewToPerfume(reviewRequest, reviewRequest.getPerfumeId(), bindingResult);
        messagingTemplate.convertAndSend("/topic/reviews/" + reviewRequest.getPerfumeId(), review);
        logger.info("Review added and sent to websocket topic for perfumeId={}", reviewRequest.getPerfumeId());
        return ResponseEntity.ok(review);
    }
}