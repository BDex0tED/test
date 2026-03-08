package com.manas.controller;

import com.manas.model.request.TransferRequest;
import com.manas.model.response.TransferResponse;
import com.manas.service.TransactionService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransferResponse> transfer(@Valid @RequestBody TransferRequest transferRequest,
                                                     @Parameter(in = ParameterIn.HEADER, name = "Idempotency-Key", description = "Unique key to prevent duplicate transfers")
                                                     @RequestHeader("Idempotency-Key") String idempotencyKey){
        return ResponseEntity.ok(transactionService.transfer(transferRequest, idempotencyKey));
    }
}
