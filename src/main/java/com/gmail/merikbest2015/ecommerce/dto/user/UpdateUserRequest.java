package com.gmail.merikbest2015.ecommerce.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


import static com.gmail.merikbest2015.ecommerce.constants.ErrorMessage.EMPTY_FIRST_NAME;
import static com.gmail.merikbest2015.ecommerce.constants.ErrorMessage.EMPTY_LAST_NAME;

@Data
public class UpdateUserRequest {
    private Long id;

    @NotBlank(message = EMPTY_FIRST_NAME)
    private String firstName;

    @NotBlank(message = EMPTY_LAST_NAME)
    private String lastName;

    private String city;
    private String address;
    private String phoneNumber;
    private String postIndex;
}
