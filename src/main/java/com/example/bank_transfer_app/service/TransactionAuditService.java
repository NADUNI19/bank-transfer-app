
package com.example.bank_transfer_app.service;

import com.example.bank_transfer_app.model.*;
import com.example.bank_transfer_app.repository.BankTransactionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransactionAuditService {

    private final BankTransactionRepository transactionRepository;

    public TransactionAuditService(
            BankTransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordFailure(String fromAccount,
                              String toAccount,
                              BigDecimal amount) {

        BankTransaction transaction = new BankTransaction(
                fromAccount,
                toAccount,
                amount,
                TransactionStatus.FAILED
        );

        transactionRepository.save(transaction);
    }
}
