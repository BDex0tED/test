package com.manas.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferRequest(
        @NotNull(message = "Sent amount shouldn't be null")
        @Positive(message = "Sent amount should be greater that zero")
        BigDecimal amount,
        @NotNull(message = "Source balanceId shouldn't be null")
        Long fromBalanceId,
        @NotNull(message = "Destination balanceId shouldn't be null")
        Long toBalanceId
) {}
