package com.manas.service.impl;

import com.manas.config.TransactionLogger;
import com.manas.exception.InsufficientBalanceException;
import com.manas.exception.ResourceNotFoundException;
import com.manas.model.entity.Balance;
import com.manas.model.entity.BalanceStatus;
import com.manas.model.entity.Transaction;
import com.manas.model.entity.TransactionStatus;
import com.manas.model.request.TransferRequest;
import com.manas.model.response.TransferResponse;
import com.manas.repo.AccountRepo;
import com.manas.repo.BalanceRepo;
import com.manas.repo.TransactionRepo;
import com.manas.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final BalanceRepo balanceRepo;
    private final TransactionRepo transactionRepo;
    private final TransactionLogger tLogger;

    @Transactional
    @Override
    public TransferResponse transfer(TransferRequest transferRequest, String idempotencyKey) {

        if(transactionRepo.existsByIdempotencyKey(idempotencyKey)){
            Transaction tx = transactionRepo.findByIdempotencyKey(idempotencyKey);
            return new TransferResponse(tx.getStatus(), tx.getCreatedAt());
        }

        Balance fromBalance = balanceRepo.findBalanceWithLock(transferRequest.fromBalanceId())
                .orElseThrow(() -> new ResourceNotFoundException("Balance with id: " + transferRequest.fromBalanceId() + " not found"));

        Balance toBalance = balanceRepo.findBalanceWithLock(transferRequest.toBalanceId())
                .orElseThrow(() -> new ResourceNotFoundException("Balance with id: " + transferRequest.toBalanceId() + " not found"));

        try {
            isValidTransfer(fromBalance, toBalance, transferRequest.amount());

            fromBalance.setBalance(fromBalance.getBalance().subtract(transferRequest.amount()));
            toBalance.setBalance(toBalance.getBalance().add(transferRequest.amount()));

            Transaction savedTx = tLogger.logTransaction(fromBalance.getAccount(), toBalance.getAccount(), transferRequest.amount(), TransactionStatus.SUCCESS);

            return new TransferResponse(savedTx.getStatus(), savedTx.getCreatedAt());

        } catch (RuntimeException e) {
            tLogger.logTransaction(fromBalance.getAccount(), toBalance.getAccount(), transferRequest.amount(), TransactionStatus.FAILED);
            throw e;
        }
    }

    private void isValidTransfer(Balance fromB, Balance toB, BigDecimal amount){
        if(fromB.getStatus() != BalanceStatus.ACTIVE || toB.getStatus() != BalanceStatus.ACTIVE){
            throw new IllegalStateException("One of the accounts isn't active");
        } if(fromB.getBalance().compareTo(amount) < 0){
            throw new InsufficientBalanceException("Insufficient balance");
        } if(fromB.getId().equals(toB.getId())){
            throw new IllegalArgumentException("Can't transfer money to the same balance");
        }
    }
}
