package com.gmail.merikbest2015.ecommerce.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gmail.merikbest2015.ecommerce.dto.GraphQLRequest;
import com.gmail.merikbest2015.ecommerce.dto.HeaderResponse;
import com.gmail.merikbest2015.ecommerce.dto.perfume.*;
import com.gmail.merikbest2015.ecommerce.mapper.PerfumeMapper;
import com.gmail.merikbest2015.ecommerce.service.graphql.GraphQLProvider;

import graphql.ExecutionResult;
import lombok.RequiredArgsConstructor;

import static com.gmail.merikbest2015.ecommerce.constants.PathConstants.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_V1_PERFUMES)
public class PerfumeController {

    private static final Logger logger = LoggerFactory.getLogger(PerfumeController.class);

    private final PerfumeMapper perfumeMapper;
    private final GraphQLProvider graphQLProvider;

    @GetMapping
    public ResponseEntity<List<PerfumeResponse>> getAllPerfumes(@PageableDefault(size = 15) Pageable pageable) {
        logger.info("Fetching all perfumes with pageable: {}", pageable);
        HeaderResponse<PerfumeResponse> response = perfumeMapper.getAllPerfumes(pageable);
        return ResponseEntity.ok().headers(response.getHeaders()).body(response.getItems());
    }

    @GetMapping(PERFUME_ID)
    public ResponseEntity<FullPerfumeResponse> getPerfumeById(@PathVariable Long perfumeId) {
        logger.info("Fetching perfume by ID: {}", perfumeId);
        return ResponseEntity.ok(perfumeMapper.getPerfumeById(perfumeId));
    }

    @PostMapping(IDS)
    public ResponseEntity<List<PerfumeResponse>> getPerfumesByIds(@RequestBody List<Long> perfumesIds) {
        logger.info("Fetching perfumes by IDs: {}", perfumesIds);
        return ResponseEntity.ok(perfumeMapper.getPerfumesByIds(perfumesIds));
    }

    @PostMapping(SEARCH)
    public ResponseEntity<List<PerfumeResponse>> findPerfumesByFilterParams(@RequestBody PerfumeSearchRequest filter,
                                                                            @PageableDefault(size = 15) Pageable pageable) {
        logger.info("Searching perfumes by filter params: {} with pageable: {}", filter, pageable);
        HeaderResponse<PerfumeResponse> response = perfumeMapper.findPerfumesByFilterParams(filter, pageable);
        return ResponseEntity.ok().headers(response.getHeaders()).body(response.getItems());
    }

    @PostMapping(SEARCH_GENDER)
    public ResponseEntity<List<PerfumeResponse>> findByPerfumeGender(@RequestBody PerfumeSearchRequest filter) {
        logger.info("Searching perfumes by gender: {}", filter.getPerfumeGender());
        return ResponseEntity.ok(perfumeMapper.findByPerfumeGender(filter.getPerfumeGender()));
    }

    @PostMapping(SEARCH_PERFUMER)
    public ResponseEntity<List<PerfumeResponse>> findByPerfumer(@RequestBody PerfumeSearchRequest filter) {
        logger.info("Searching perfumes by perfumer: {}", filter.getPerfumer());
        return ResponseEntity.ok(perfumeMapper.findByPerfumer(filter.getPerfumer()));
    }

    @PostMapping(SEARCH_TEXT)
    public ResponseEntity<List<PerfumeResponse>> findByInputText(@RequestBody SearchTypeRequest searchType,
                                                                 @PageableDefault(size = 15) Pageable pageable) {
        logger.info("Searching perfumes by input text. SearchType: {}, Text: {}, Pageable: {}",
                searchType.getSearchType(), searchType.getText(), pageable);
        HeaderResponse<PerfumeResponse> response = perfumeMapper.findByInputText(searchType.getSearchType(), searchType.getText(), pageable);
        return ResponseEntity.ok().headers(response.getHeaders()).body(response.getItems());
    }

    @PostMapping(GRAPHQL_IDS)
    public ResponseEntity<ExecutionResult> getPerfumesByIdsQuery(@RequestBody GraphQLRequest request) {
        logger.info("GraphQL query for perfumes by IDs received");
        return ResponseEntity.ok(graphQLProvider.getGraphQL().execute(request.getQuery()));
    }

    @PostMapping(GRAPHQL_PERFUMES)
    public ResponseEntity<ExecutionResult> getAllPerfumesByQuery(@RequestBody GraphQLRequest request) {
        logger.info("GraphQL query for all perfumes received");
        return ResponseEntity.ok(graphQLProvider.getGraphQL().execute(request.getQuery()));
    }

    @PostMapping(GRAPHQL_PERFUME)
    public ResponseEntity<ExecutionResult> getPerfumeByQuery(@RequestBody GraphQLRequest request) {
        logger.info("GraphQL query for perfume received");
        return ResponseEntity.ok(graphQLProvider.getGraphQL().execute(request.getQuery()));
    }
}