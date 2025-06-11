package com.gmail.merikbest2015.ecommerce.controller;

import com.gmail.merikbest2015.ecommerce.dto.RegistrationRequest;
import com.gmail.merikbest2015.ecommerce.mapper.AuthenticationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.gmail.merikbest2015.ecommerce.constants.PathConstants.ACTIVATE_CODE;
import static com.gmail.merikbest2015.ecommerce.constants.PathConstants.API_V1_REGISTRATION;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_V1_REGISTRATION)
public class RegistrationController {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    private final AuthenticationMapper authenticationMapper;

    @PostMapping
    public ResponseEntity<String> registration(@Valid @RequestBody RegistrationRequest user, BindingResult bindingResult) {
        logger.info("Registering user with email: {}", user.getEmail());
        String response = authenticationMapper.registerUser(user.getCaptcha(), user, bindingResult);
        logger.info("Registration response: {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping(ACTIVATE_CODE)
    public ResponseEntity<String> activateEmailCode(@PathVariable String code) {
        logger.info("Activating user with code: {}", code);
        String response = authenticationMapper.activateUser(code);
        logger.info("Activation response: {}", response);
        return ResponseEntity.ok(response);
    }
}
