package com.manas;

import com.manas.model.entity.Account;
import com.manas.model.entity.Balance;
import com.manas.model.entity.BalanceStatus;
import com.manas.model.entity.Transaction;
import com.manas.model.request.TransferRequest;
import com.manas.service.impl.TransactionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@RequiredArgsConstructor
public class TransactionServiceTest {

    @MockitoBean
    private final TransactionServiceImpl transactionService;

    private final MockMvc mockMvc;

    @BeforeEach
    void setUp(){
        Account acc1 = new Account();
        acc1.setName("Account 1");
        Balance balance1 = new Balance();
        balance1.setStatus(BalanceStatus.ACTIVE);
        balance1.setAccount(acc1);
        balance1.setBalance(BigDecimal.valueOf(1000));
        acc1.setBalances(List.of(balance1));

        Account acc2 = new Account();
        acc2.setName("Account 2");
        Balance balance2 = new Balance();
        balance2.setStatus(BalanceStatus.ACTIVE);
        balance2.setAccount(acc2);
        balance2.setBalance(BigDecimal.valueOf(1000));
        acc2.setBalances(List.of(balance2));
    }

    @Test
    public void testTransfer_TakesValidData_shouldReturnTrue() throws Exception{
//        TransferRequest transferRequest = TransferRequest.builder()
//                .amount(BigDecimal.valueOf(500))
//                .fromBalanceId()



    }
}
