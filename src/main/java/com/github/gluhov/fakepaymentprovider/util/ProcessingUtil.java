package com.github.gluhov.fakepaymentprovider.util;

import com.github.gluhov.fakepaymentprovider.model.TransactionStatus;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ProcessingUtil {
    private static final TransactionStatus[] STATUSES = {TransactionStatus.FAILED, TransactionStatus.SUCCESS};

    public static TransactionStatus getRandomStatus() {
        int randomIndex = (int) (Math.random() * STATUSES.length);
        return STATUSES[randomIndex];
    }
}