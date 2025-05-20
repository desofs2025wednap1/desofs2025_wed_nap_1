package com.gmail.merikbest2015.ecommerce.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@ToString
@Entity
@Table(name = "perfume")
public class Perfume {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "perfume_id_seq")
    @SequenceGenerator(name = "perfume_id_seq", sequenceName = "perfume_id_seq", initialValue = 109, allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "perfume_title")
    private String perfumeTitle;

    @Column(name = "perfumer")
    private String perfumer;

    @Column(name = "year")
    private Integer year;
    
    @Column(name = "country")
    private String country;

    @Column(name = "perfume_gender")
    private String perfumeGender;

    @Column(name = "fragrance_top_notes")
    private String fragranceTopNotes;

    @Column(name = "fragrance_middle_notes")
    private String fragranceMiddleNotes;
    
    @Column(name = "fragrance_base_notes")
    private String fragranceBaseNotes;

    @Column(name = "description")
    private String description;
    
    @Column(name = "filename")
    private String filename;
    
    @Column(name = "price")
    private Integer price;
    
    @Column(name = "volume")
    private String volume;
    
    @Column(name = "type")
    private String type;
    
    @Column(name = "perfume_rating")
    private Double perfumeRating;

    @OneToMany
    @ToString.Exclude
    private List<Review> reviews;

    public Perfume(String perfumeTitle, String perfumer, Integer year, String country,
                   String perfumeGender, String fragranceTopNotes, String fragranceMiddleNotes,
                   String fragranceBaseNotes, String description, String filename, Integer price,
                   String volume, String type, Double perfumeRating) {

        validatePerfumeTitle(perfumeTitle);
        validateYear(year);
        validateCountry(country);
        validateRating(perfumeRating);

        this.perfumeTitle = perfumeTitle;
        this.perfumer = perfumer;
        this.year = year;
        this.country = country;
        this.perfumeGender = perfumeGender;
        this.fragranceTopNotes = fragranceTopNotes;
        this.fragranceMiddleNotes = fragranceMiddleNotes;
        this.fragranceBaseNotes = fragranceBaseNotes;
        this.description = description;
        this.filename = filename;
        this.price = price;
        this.volume = volume;
        this.type = type;
        this.perfumeRating = perfumeRating;
    }

    public Perfume() {

    }


    private void validatePerfumeTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Perfume title must not be blank.");
        }
    }

    private void validateYear(Integer year) {
        if (year == null || year < 1800 || year > 2100) {
            throw new IllegalArgumentException("Year must be between 1800 and 2100.");
        }
    }

    private void validateCountry(String country) {
        if (country == null || country.trim().isEmpty()) {
            throw new IllegalArgumentException("Country must not be blank.");
        }

        boolean isValid = Arrays.asList(Locale.getISOCountries()).contains(country.toUpperCase());
        if (!isValid) {
            throw new IllegalArgumentException("Country must be a valid ISO country code (e.g., 'US', 'FR').");
        }
    }

    private void validateRating(Double rating) {
        if (rating != null && (rating < 0.0 || rating > 5.0)) {
            throw new IllegalArgumentException("Rating must be between 0 and 5.");
        }
    }


    public boolean reviewExists(Long reviewId) {
        return reviews.stream().anyMatch(review -> review.getId().equals(reviewId));
    }

    public void addReview(Review review) {
        if (reviews == null) {
            reviews = new ArrayList<>();
        }
        reviews.add(review);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Perfume perfume = (Perfume) o;
        return Objects.equals(id, perfume.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
