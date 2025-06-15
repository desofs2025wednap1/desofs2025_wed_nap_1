package com.gmail.merikbest2015.ecommerce.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@Table(name = "order_item")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_item_seq")
    @SequenceGenerator(name = "order_item_seq", sequenceName = "order_item_seq", initialValue = 12, allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "amount")
    private Long amount;

    @Column(name = "quantity")
    private Long quantity;

    @OneToOne
    private Perfume perfume;


    public OrderItem() {

    }

    public OrderItem(Long amount, Long quantity, Perfume perfume) {
        validateAmount(amount);
        validateQuantity(quantity);
        validatePerfume(perfume);

        this.amount = amount;
        this.quantity = quantity;
        this.perfume = perfume;
    }



    // Getters and setters if needed...

    private void validateAmount(Long amount) {
        if (amount == null || amount < 0) {
            throw new IllegalArgumentException("Amount must be non-null and non-negative.");
        }
    }

    private void validateQuantity(Long quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be non-null and positive.");
        }
    }

    private void validatePerfume(Perfume perfume) {
        if (perfume == null) {
            throw new IllegalArgumentException("Perfume must not be null.");
        }
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(id, orderItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
