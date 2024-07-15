package com.github.gluhov.fakepaymentprovider.model;

import com.fasterxml.jackson.databind.ser.Serializers;
import lombok.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("card_data")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class CardData extends Serializers.Base {
    @Column("card_number")
    private String cardNumber;
    @Column("exp_date")
    private LocalDateTime expDate;
    @Column("cvv")
    private int cvv;

    @ToString.Include(name = "cardNumber")
    private String maskCardNumber() {
        return cardNumber.substring(0,4) + "***" + cardNumber.substring(-4);
    }
}
