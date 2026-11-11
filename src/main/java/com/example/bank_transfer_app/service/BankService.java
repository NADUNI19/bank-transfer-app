
package com.example.bank_transfer_app.service;

import com.example.bank_transfer_app.exception.*;
import com.example.bank_transfer_app.model.*;
import com.example.bank_transfer_app.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
public class BankService {

    private final CustomerRepository customerRepository;
    private final BankTransactionRepository transactionRepository;

    public BankService(
            CustomerRepository customerRepository,
            BankTransactionRepository transactionRepository) {

        this.customerRepository = customerRepository;
        this.transactionRepository = transactionRepository;
    }


    @Transactional(rollbackFor = Exception.class)
    public void transfer(
            String fromAccount,
            String toAccount,
            BigDecimal amount,
            boolean simulateFailure) {


        if (fromAccount == null || fromAccount.isBlank()
                || toAccount == null || toAccount.isBlank()) {

            throw new InvalidTransferException(
                    "Account numbers are required.");
        }


        if (fromAccount.equals(toAccount)) {

            throw new InvalidTransferException(
                    "Sender and receiver must be different accounts.");
        }

        if (amount == null
                || amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new InvalidTransferException(
                    "Amount must be greater than zero.");
        }


        customerRepository.getByAccountNumber(fromAccount)
                .orElseThrow(() ->
                        new AccountNotFoundException(
                                "Sender account not found."));


        customerRepository.getByAccountNumber(toAccount)
                .orElseThrow(() ->
                        new AccountNotFoundException(
                                "Receiver account not found."));


        int debited = customerRepository.debit(fromAccount, amount);

        if (debited == 0) {

            throw new InsufficientBalanceException(
                    "Insufficient balance.");
        }

        if (simulateFailure) {

            throw new RuntimeException(
                    "Simulated system failure. All changes were rolled back.");
        }


        int credited = customerRepository.credit(toAccount, amount);

        if (credited == 0) {

            throw new AccountNotFoundException(
                    "Receiver account not found.");
        }


        BankTransaction transaction = new BankTransaction(
                fromAccount,
                toAccount,
                amount,
                TransactionStatus.SUCCESS
        );

        transactionRepository.save(transaction);
    }


    @Transactional
    public void createCustomer(Customer customer) {

        if (customerRepository.existsById(
                customer.getAccountNumber())) {

            throw new IllegalArgumentException(
                    "Account number already exists.");
        }

        if (customerRepository.countEmails(customer.getEmail()) > 0) {

            throw new IllegalArgumentException(
                    "Email already exists.");
        }

        customerRepository.save(customer);
    }
}
