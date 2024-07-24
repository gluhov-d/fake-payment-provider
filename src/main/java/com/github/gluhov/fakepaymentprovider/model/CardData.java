package com.github.gluhov.fakepaymentprovider.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Table("card_data")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CardData extends BaseEntity {
    @Column("card_number")
    @NotNull
    private String cardNumber;
    @Column("exp_date")
    private LocalDate expDate;
    @Column("cvv")
    private int cvv;

    @Builder
    public CardData(UUID id, Status status, LocalDateTime createdAt, LocalDateTime updatedAt, String createdBy, String modifiedBy, String cardNumber, LocalDate expDate, int cvv) {
        super(id,  status, createdAt, updatedAt, createdBy, modifiedBy);
        this.cardNumber = cardNumber;
        this.expDate = expDate;
        this.cvv = cvv;
    }

    @ToString.Include(name = "cardNumber")
    private String maskCardNumber() {
        return cardNumber.substring(0,4) + "***" + cardNumber.substring(cardNumber.length() - 4);
    }
}
