package com.manas.config;

import com.manas.model.entity.Account;
import com.manas.model.entity.Transaction;
import com.manas.model.entity.TransactionStatus;
import com.manas.repo.TransactionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransactionLogger {
    private final TransactionRepo transactionRepo;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Transaction logTransaction(Account from, Account to, BigDecimal amount, TransactionStatus status) {
        Transaction t = new Transaction();
        t.setFromAccount(from);
        t.setToAccount(to);
        t.setAmount(amount);
        t.setStatus(status);
        return transactionRepo.save(t);
    }
}
