package com.manas;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manas.model.entity.Account;
import com.manas.model.entity.Balance;
import com.manas.model.entity.BalanceStatus;
import com.manas.model.request.TransferRequest;
import com.manas.repo.AccountRepo;
import com.manas.repo.BalanceRepo;
import com.manas.repo.TransactionRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionServiceTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private BalanceRepo balanceRepo;

    @Autowired
    private TransactionRepo transactionRepo;

    private Long sourceBalanceId;
    private Long destinationBalanceId;

    @BeforeEach
    void setUp() {
        Account acc1 = new Account();
        acc1.setName("Account 1");
        acc1 = accountRepo.save(acc1);

        Balance balance1 = new Balance();
        balance1.setStatus(BalanceStatus.ACTIVE);
        balance1.setAccount(acc1);
        balance1.setBalance(BigDecimal.valueOf(1000));
        balance1 = balanceRepo.save(balance1);

        sourceBalanceId = balance1.getId();

        Account acc2 = new Account();
        acc2.setName("Account 2");
        acc2 = accountRepo.save(acc2);

        Balance balance2 = new Balance();
        balance2.setStatus(BalanceStatus.ACTIVE);
        balance2.setAccount(acc2);
        balance2.setBalance(BigDecimal.valueOf(1000));
        balance2 = balanceRepo.save(balance2);

        destinationBalanceId = balance2.getId();
    }

    @AfterEach
    void tearDown() {
        transactionRepo.deleteAll();
        balanceRepo.deleteAll();
        accountRepo.deleteAll();
    }

    @Test
    public void testTransfer_TakesValidData_shouldReturnOk() throws Exception {
        TransferRequest transferRequest = TransferRequest.builder()
                .amount(BigDecimal.valueOf(500))
                .fromBalanceId(sourceBalanceId)
                .toBalanceId(destinationBalanceId)
                .build();

        String idempotencyKey = UUID.randomUUID().toString();

        mockMvc.perform(post("/api/transfers")
                        .header("Idempotency-Key", idempotencyKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.transferredTime").exists());

        Balance updatedSource = balanceRepo.findById(sourceBalanceId).orElseThrow();
        Balance updatedDestination = balanceRepo.findById(destinationBalanceId).orElseThrow();

        assertEquals(0, BigDecimal.valueOf(500).compareTo(updatedSource.getBalance()));
        assertEquals(0, BigDecimal.valueOf(1500).compareTo(updatedDestination.getBalance()));
    }
}