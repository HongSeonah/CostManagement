package com.hongseonah.costmanager.domain.internaltransfer.repository;

import com.hongseonah.costmanager.domain.internaltransfer.entity.InternalTransfer;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InternalTransferRepository extends JpaRepository<InternalTransfer, Long> {

    boolean existsByTransferCode(String transferCode);

    List<InternalTransfer> findByTransferDateBetween(LocalDate start, LocalDate end);
}
