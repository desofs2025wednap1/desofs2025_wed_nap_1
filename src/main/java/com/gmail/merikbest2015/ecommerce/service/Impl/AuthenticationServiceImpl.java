package com.gmail.merikbest2015.ecommerce.service.Impl;

import com.gmail.merikbest2015.ecommerce.enums.AuthProvider;
import com.gmail.merikbest2015.ecommerce.enums.Role;
import com.gmail.merikbest2015.ecommerce.domain.User;
import com.gmail.merikbest2015.ecommerce.dto.CaptchaResponse;
import com.gmail.merikbest2015.ecommerce.exception.ApiRequestException;
import com.gmail.merikbest2015.ecommerce.exception.EmailException;
import com.gmail.merikbest2015.ecommerce.exception.PasswordConfirmationException;
import com.gmail.merikbest2015.ecommerce.exception.PasswordException;
import com.gmail.merikbest2015.ecommerce.repository.UserRepository;
import com.gmail.merikbest2015.ecommerce.security.JwtProvider;
import com.gmail.merikbest2015.ecommerce.security.oauth2.OAuth2UserInfo;
import com.gmail.merikbest2015.ecommerce.service.AuthenticationService;
import com.gmail.merikbest2015.ecommerce.service.email.MailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.gmail.merikbest2015.ecommerce.constants.ErrorMessage.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    private final AuthenticationManager authenticationManager;
    private final RestTemplate restTemplate;
    private final JwtProvider jwtProvider;
    private final MailSender mailSender;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Value("${hostname}")
    private String hostname;

    @Value("${recaptcha.secret}")
    private String secret;

    @Value("${recaptcha.url}")
    private String captchaUrl;

    @Override
    public Map<String, Object> login(String email, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        logger.warn("Login failed: email '{}' not found", email);
                        return new ApiRequestException(EMAIL_NOT_FOUND, HttpStatus.NOT_FOUND);
                    });
            String userRole = user.getRoles().iterator().next().name();
            String token = jwtProvider.createToken(email, userRole);
            logger.info("User '{}' logged in successfully", email);

            Map<String, Object> response = new HashMap<>();
            response.put("user", user);
            response.put("token", token);
            return response;
        } catch (AuthenticationException e) {
            logger.warn("Login failed: incorrect password for '{}'", email);
            throw new ApiRequestException(INCORRECT_PASSWORD, HttpStatus.FORBIDDEN);
        }
    }

    @Override
    @Transactional
    public String registerUser(User user, String captcha, String password2) {
        logger.info("Attempting to register user with email '{}'", user.getEmail());
        String url = String.format(captchaUrl, secret, captcha);
        restTemplate.postForObject(url, Collections.emptyList(), CaptchaResponse.class);

        if (user.getPassword() != null && !user.getPassword().equals(password2)) {
            logger.warn("Passwords do not match for registration attempt: '{}'", user.getEmail());
            throw new PasswordException(PASSWORDS_DO_NOT_MATCH);
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            logger.warn("Registration failed: email '{}' already in use", user.getEmail());
            throw new EmailException(EMAIL_IN_USE);
        }

        user.setActive(false);
        user.setRoles(Collections.singleton(Role.USER));
        user.setProvider(AuthProvider.LOCAL);
        user.setActivationCode(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        sendEmail(user, "Activation code", "registration-template", "registrationUrl", "/activate/" + user.getActivationCode());
        logger.info("User '{}' registered successfully", user.getEmail());
        return "User successfully registered.";
    }

    @Override
    @Transactional
    public User registerOauth2User(String provider, OAuth2UserInfo oAuth2UserInfo) {
        logger.info("Registering OAuth2 user from provider '{}'", provider);
        User user = new User();
        user.setEmail(oAuth2UserInfo.getEmail());
        user.setFirstName(oAuth2UserInfo.getFirstName());
        user.setLastName(oAuth2UserInfo.getLastName());
        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        user.setProvider(AuthProvider.valueOf(provider.toUpperCase()));
        return userRepository.save(user);
    }


    @Override
    @Transactional
    public User updateOauth2User(User user, String provider, OAuth2UserInfo oAuth2UserInfo) {
        logger.info("Updating OAuth2 user '{}' from provider '{}'", user.getEmail(), provider);
        user.setFirstName(oAuth2UserInfo.getFirstName());
        user.setLastName(oAuth2UserInfo.getLastName());
        user.setProvider(AuthProvider.valueOf(provider.toUpperCase()));
        return userRepository.save(user);
    }

    @Override
    public String getEmailByPasswordResetCode(String code) {
        logger.info("Retrieving email by password reset code '{}'", code);
        return userRepository.getEmailByPasswordResetCode(code)
                .orElseThrow(() -> {
                    logger.warn("Invalid password reset code '{}'", code);
                    return new ApiRequestException(INVALID_PASSWORD_CODE, HttpStatus.BAD_REQUEST);
                });
    }

    @Override
    @Transactional
    public String sendPasswordResetCode(String email) {
        logger.info("Sending password reset code to '{}'", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Password reset failed: email '{}' not found", email);
                    return new ApiRequestException(EMAIL_NOT_FOUND, HttpStatus.NOT_FOUND);
                });
        user.setPasswordResetCode(UUID.randomUUID().toString());
        userRepository.save(user);

        sendEmail(user, "Password reset", "password-reset-template", "resetUrl", "/reset/" + user.getPasswordResetCode());
        logger.info("Password reset code sent to '{}'", email);
        return "Reset password code is send to your E-mail";
    }

    @Override
    @Transactional
    public String passwordReset(String email, String password, String password2) {
        if (StringUtils.isEmpty(password2)) {
            logger.warn("Password confirmation is empty for '{}'", email);
            throw new PasswordConfirmationException(EMPTY_PASSWORD_CONFIRMATION);
        }
        if (password != null && !password.equals(password2)) {
            logger.warn("Password mismatch during reset for '{}'", email);
            throw new PasswordException(PASSWORDS_DO_NOT_MATCH);
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Password reset failed: email '{}' not found", email);
                    return new ApiRequestException(EMAIL_NOT_FOUND, HttpStatus.NOT_FOUND);
                });
        user.setPassword(passwordEncoder.encode(password));
        user.setPasswordResetCode(null);
        userRepository.save(user);
        logger.info("Password successfully reset for '{}'", email);
        return "Password successfully changed!";
    }

    @Override
    @Transactional
    public String activateUser(String code) {
        logger.info("Activating user with code '{}'", code);
        User user = userRepository.findByActivationCode(code)
                .orElseThrow(() -> {
                    logger.warn("Activation failed: invalid code '{}'", code);
                    return new ApiRequestException(ACTIVATION_CODE_NOT_FOUND, HttpStatus.NOT_FOUND);
                });
        user.setActivationCode(null);
        user.setActive(true);
        userRepository.save(user);
        logger.info("User '{}' activated successfully", user.getEmail());
        return "User successfully activated.";
    }

    private void sendEmail(User user, String subject, String template, String urlAttribute, String urlPath) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("firstName", user.getFirstName());
        attributes.put(urlAttribute, "http://" + hostname + urlPath);
        mailSender.sendMessageHtml(user.getEmail(), subject, template, attributes);
        logger.info("Email sent to '{}' with subject '{}'", user.getEmail(), subject);
    }
}
