package com.camilachangperes.process_pix_transaction.repository;

import com.camilachangperes.process_pix_transaction.model.Idempotency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IdempotencyRepository extends JpaRepository<Idempotency, String> {
    Optional<Idempotency> findByIdempotencyKey(String idempotencyKey);
}
