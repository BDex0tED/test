package com.manas.service;

import com.manas.model.request.TransferRequest;
import com.manas.model.response.TransferResponse;

public interface TransactionService {
    TransferResponse transfer(TransferRequest transferRequest);
}
