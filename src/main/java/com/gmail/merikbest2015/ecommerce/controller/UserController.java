package com.gmail.merikbest2015.ecommerce.controller;

import com.gmail.merikbest2015.ecommerce.dto.GraphQLRequest;
import com.gmail.merikbest2015.ecommerce.dto.perfume.PerfumeResponse;
import com.gmail.merikbest2015.ecommerce.dto.user.UpdateUserRequest;
import com.gmail.merikbest2015.ecommerce.dto.user.UserResponse;
import com.gmail.merikbest2015.ecommerce.mapper.UserMapper;
import com.gmail.merikbest2015.ecommerce.security.UserPrincipal;
import com.gmail.merikbest2015.ecommerce.service.graphql.GraphQLProvider;
import graphql.ExecutionResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.gmail.merikbest2015.ecommerce.constants.PathConstants.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_V1_USERS)
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserMapper userMapper;
    private final GraphQLProvider graphQLProvider;

    @GetMapping
    public ResponseEntity<UserResponse> getUserInfo(@AuthenticationPrincipal UserPrincipal user) {
        logger.info("Fetching user info for email={}", user.getEmail());
        UserResponse response = userMapper.getUserInfo(user.getEmail());
        logger.info("User info fetched for email={}", user.getEmail());
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<UserResponse> updateUserInfo(@AuthenticationPrincipal UserPrincipal user,
                                                       @Valid @RequestBody UpdateUserRequest request,
                                                       BindingResult bindingResult) {
        logger.info("Updating user info for email={}", user.getEmail());
        UserResponse response = userMapper.updateUserInfo(user.getEmail(), request, bindingResult);
        logger.info("User info updated for email={}", user.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping(CART)
    public ResponseEntity<List<PerfumeResponse>> getCart(@RequestBody List<Long> perfumesIds) {
        logger.info("Fetching cart for perfumesIds={}", perfumesIds);
        List<PerfumeResponse> cart = userMapper.getCart(perfumesIds);
        logger.info("Cart fetched with {} items", cart.size());
        return ResponseEntity.ok(cart);
    }

    @PostMapping(GRAPHQL)
    public ResponseEntity<ExecutionResult> getUserInfoByQuery(@RequestBody GraphQLRequest request) {
        logger.info("Executing GraphQL user info query");
        ExecutionResult result = graphQLProvider.getGraphQL().execute(request.getQuery());
        logger.info("GraphQL user info query executed");
        return ResponseEntity.ok(result);
    }
}