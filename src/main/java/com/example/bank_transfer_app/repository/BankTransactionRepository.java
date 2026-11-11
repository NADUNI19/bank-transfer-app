
package com.example.bank_transfer_app.repository;

import com.example.bank_transfer_app.model.BankTransaction;
import com.example.bank_transfer_app.model.TransactionStatus;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankTransactionRepository
        extends JpaRepository<BankTransaction, Long> {

    // Get all transactions ordered by latest date
    @Query("SELECT t FROM BankTransaction t " +
            "ORDER BY t.createdAt DESC")
    List<BankTransaction> findAllLatest();


    // Get transactions where customer is sender OR receiver
    // Includes both SUCCESS and FAILED transactions
    @Query("SELECT t FROM BankTransaction t " +
            "WHERE t.fromAccount = :accountNumber " +
            "OR t.toAccount = :accountNumber " +
            "ORDER BY t.createdAt DESC")
    List<BankTransaction> findHistory(
            @Param("accountNumber") String accountNumber);


    // Get transactions by status
    @Query("SELECT t FROM BankTransaction t " +
            "WHERE t.status = :status " +
            "ORDER BY t.createdAt DESC")
    List<BankTransaction> findByStatusLatest(
            @Param("status") TransactionStatus status);


    // Count transactions by status
    @Query("SELECT COUNT(t) FROM BankTransaction t " +
            "WHERE t.status = :status")
    long countByStatus(
            @Param("status") TransactionStatus status);

}
