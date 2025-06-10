package com.gmail.merikbest2015.ecommerce.service.Impl;

import com.gmail.merikbest2015.ecommerce.domain.Perfume;
import com.gmail.merikbest2015.ecommerce.domain.User;
import com.gmail.merikbest2015.ecommerce.exception.ApiRequestException;
import com.gmail.merikbest2015.ecommerce.repository.PerfumeRepository;
import com.gmail.merikbest2015.ecommerce.repository.UserRepository;
import com.gmail.merikbest2015.ecommerce.service.UserService;
import graphql.schema.DataFetcher;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.gmail.merikbest2015.ecommerce.constants.ErrorMessage.EMAIL_NOT_FOUND;
import static com.gmail.merikbest2015.ecommerce.constants.ErrorMessage.USER_NOT_FOUND;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PerfumeRepository perfumeRepository;

    @Override
    public User getUserById(Long userId) {
        logger.info("Fetching user by id: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("User not found with id: {}", userId);
                    return new ApiRequestException(USER_NOT_FOUND, HttpStatus.NOT_FOUND);
                });
    }

    @Override
    public User getUserInfo(String email) {
        logger.info("Fetching user info by email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Email not found: {}", email);
                    return new ApiRequestException(EMAIL_NOT_FOUND, HttpStatus.NOT_FOUND);
                });
    }

    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        logger.info("Fetching all users with pagination: {}", pageable);
        return userRepository.findAllByOrderByIdAsc(pageable);
    }

    @Override
    public List<Perfume> getCart(List<Long> perfumeIds) {
        logger.info("Fetching perfumes for cart with ids: {}", perfumeIds);
        return perfumeRepository.findByIdIn(perfumeIds);
    }

    @Override
    @Transactional
    public User updateUserInfo(String email, User user) {
        logger.info("Updating user info for email: {}", email);
        User userFromDb = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Email not found while updating: {}", email);
                    return new ApiRequestException(EMAIL_NOT_FOUND, HttpStatus.NOT_FOUND);
                });
        userFromDb.setFirstName(user.getFirstName());
        userFromDb.setLastName(user.getLastName());
        userFromDb.setCity(user.getCity());
        userFromDb.setAddress(user.getAddress());
        userFromDb.setPhoneNumber(user.getPhoneNumber());
        userFromDb.setPostIndex(user.getPostIndex());
        logger.info("User info updated for email: {}", email);
        return userFromDb;
    }

    @Override
    public DataFetcher<User> getUserByQuery() {
        return dataFetchingEnvironment -> {
            Long userId = Long.parseLong(dataFetchingEnvironment.getArgument("id"));
            logger.info("GraphQL query: getUserById with id {}", userId);
            return userRepository.findById(userId).get();
        };
    }

    @Override
    public DataFetcher<List<User>> getAllUsersByQuery() {
        return dataFetchingEnvironment -> {
            logger.info("GraphQL query: getAllUsers");
            return userRepository.findAllByOrderByIdAsc();
        };
    }
}
