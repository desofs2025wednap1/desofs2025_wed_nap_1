package com.gmail.merikbest2015.ecommerce.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@ToString
@Entity
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "author")
    private String author;

    @Column(name = "message")
    private String message;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "date")
    private LocalDate date;

    public Review() {
        this.date = LocalDate.now();
    }

    public Review(String author, String message, Integer rating, LocalDate date) {
        validateAuthor(author);
        validateMessage(message);
        validateRating(rating);
        validateDate(date);

        this.author = author;
        this.message = message;
        this.rating = rating;
        this.date = date != null ? date : LocalDate.now();
    }

    // Example validation methods

    private void validateAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Author must not be blank.");
        }
    }

    private void validateMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Message must not be blank.");
        }
    }

    private void validateRating(Integer rating) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }
    }

    private void validateDate(LocalDate date) {
        if (date != null && date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date cannot be in the future.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return Objects.equals(id, review.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
