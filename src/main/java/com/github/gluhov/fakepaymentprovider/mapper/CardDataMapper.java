package com.github.gluhov.fakepaymentprovider.mapper;

import com.github.gluhov.fakepaymentprovider.dto.CardDataDto;
import com.github.gluhov.fakepaymentprovider.model.CardData;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CardDataMapper {
    CardDataDto map(CardData cardData);

    @InheritInverseConfiguration
    CardData map(CardDataDto cardDataDto);
}