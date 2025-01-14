package com.woowacamp.soolsool.core.receipt.domain.repository;

import com.woowacamp.soolsool.core.receipt.domain.Receipt;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    @Query("select r from Receipt r join fetch r.receiptItems where r.id = :receiptId")
    Optional<Receipt> findById(final Long receiptId);
}
