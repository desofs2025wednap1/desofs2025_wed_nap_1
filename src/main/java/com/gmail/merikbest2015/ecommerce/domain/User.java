package com.gmail.merikbest2015.ecommerce.domain;

import com.gmail.merikbest2015.ecommerce.enums.AuthProvider;
import com.gmail.merikbest2015.ecommerce.enums.Role;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

@Getter
@Setter
@ToString
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_seq")
    @SequenceGenerator(name = "users_id_seq", sequenceName = "users_id_seq", initialValue = 4, allocationSize = 1)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "password")
    private String password;
    
    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "city")
    private String city;

    @Column(name = "address")
    private String address;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "post_index")
    private String postIndex;

    @Column(name = "activation_code")
    private String activationCode;

    @Column(name = "password_reset_code")
    private String passwordResetCode;

    @Column(name = "active")
    private boolean active;

    @Column(name = "provider")
    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;


    public User() {
        // default constructor for JPA
    }

    public User(String email, String password, String firstName, String lastName,
                String city, String address, String phoneNumber, String postIndex,
                String activationCode, String passwordResetCode, boolean active,
                AuthProvider provider, Set<Role> roles) {

        validateEmail(email);
        validatePhoneNumber(phoneNumber);
        validatePostIndex(postIndex);

        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.city = city;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.postIndex = postIndex;
        this.activationCode = activationCode;
        this.passwordResetCode = passwordResetCode;
        this.active = active;
        this.provider = provider;
        this.roles = roles;
    }

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email must not be blank.");
        }
        String emailRegex = "^[\\w-.]+@[\\w-]+\\.[a-z]{2,}$";
        if (!Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE).matcher(email).matches()) {
            throw new IllegalArgumentException("Email format is invalid.");
        }
    }

    private void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            // Simple validation: digits, +, -, spaces allowed
            String phoneRegex = "^[+\\d\\s-]{7,15}$";
            if (!Pattern.compile(phoneRegex).matcher(phoneNumber).matches()) {
                throw new IllegalArgumentException("Phone number format is invalid.");
            }
        }
    }

    private void validatePostIndex(String postIndex) {
        if (postIndex != null && !postIndex.trim().isEmpty()) {
            // Simple postal code validation - customize as needed
            if (postIndex.length() > 10) {
                throw new IllegalArgumentException("Post index is too long.");
            }
        }
    }




    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
