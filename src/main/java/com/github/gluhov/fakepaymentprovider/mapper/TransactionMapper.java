package com.github.gluhov.fakepaymentprovider.mapper;

import com.github.gluhov.fakepaymentprovider.dto.TransactionDto;
import com.github.gluhov.fakepaymentprovider.model.Transaction;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    TransactionDto map(Transaction transaction);

    @InheritInverseConfiguration
    Transaction map(TransactionDto transactionDto);
}