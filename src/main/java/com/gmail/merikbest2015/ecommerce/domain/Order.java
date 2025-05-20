package com.gmail.merikbest2015.ecommerce.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Getter
@Setter
@ToString
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orders_seq")
    @SequenceGenerator(name = "orders_seq", sequenceName = "orders_seq", initialValue = 6, allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "total_price")
    private Double totalPrice;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "city")
    private String city;

    @Column(name = "address")
    private String address;

    @Column(name = "email")
    private String email;
    
    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "post_index")
    private Integer postIndex;

    @OneToMany(fetch = FetchType.EAGER)
    private List<OrderItem> orderItems;

    public Order() {
        this.date = LocalDate.now();
        this.orderItems = new ArrayList<>();
    }

    public Order(Double totalPrice, LocalDate date, String firstName, String lastName,
                 String city, String address, String email, String phoneNumber, Integer postIndex) {

        validateTotalPrice(totalPrice);
        validateDate(date);
        validateNames(firstName, lastName);
        validateAddress(city, address);
        validateEmail(email);
        validatePhoneNumber(phoneNumber);
        validatePostIndex(postIndex);

        this.totalPrice = totalPrice;
        this.date = date;
        this.firstName = firstName;
        this.lastName = lastName;
        this.city = city;
        this.address = address;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.postIndex = postIndex;
        this.orderItems = new ArrayList<>();
    }



    private void validateTotalPrice(Double price) {
        if (price == null || price < 0) {
            throw new IllegalArgumentException("Total price must be non-null and non-negative.");
        }
    }

    private void validateDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date must not be null.");
        }
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date cannot be in the future.");
        }
    }

    private void validateNames(String firstName, String lastName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name must not be blank.");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name must not be blank.");
        }
    }

    private void validateAddress(String city, String address) {
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City must not be blank.");
        }
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address must not be blank.");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email must not be blank.");
        }
        String emailRegex = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        if (!Pattern.matches(emailRegex, email)) {
            throw new IllegalArgumentException("Email format is invalid.");
        }
    }

    private void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number must not be blank.");
        }
        String phoneRegex = "^[+]?\\d{7,15}$";
        if (!Pattern.matches(phoneRegex, phoneNumber)) {
            throw new IllegalArgumentException("Phone number format is invalid.");
        }
    }

    private void validatePostIndex(Integer postIndex) {
        if (postIndex == null || postIndex <= 0) {
            throw new IllegalArgumentException("Post index must be positive.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }



}
