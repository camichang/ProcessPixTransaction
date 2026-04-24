package com.camilachangperes.process_pix_transaction.repository;

import com.camilachangperes.process_pix_transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
}
