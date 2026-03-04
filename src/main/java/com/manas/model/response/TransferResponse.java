package com.manas.model.response;

import com.manas.model.entity.TransactionStatus;

import java.time.LocalDateTime;

public record TransferResponse(
        TransactionStatus status,
        LocalDateTime transferredTime
) {}
