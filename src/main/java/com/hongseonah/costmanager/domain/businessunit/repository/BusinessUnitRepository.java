package com.hongseonah.costmanager.domain.businessunit.repository;

import com.hongseonah.costmanager.domain.businessunit.entity.BusinessUnit;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessUnitRepository extends JpaRepository<BusinessUnit, Long> {
    boolean existsByUnitCode(String unitCode);
    Optional<BusinessUnit> findByUnitCode(String unitCode);
}

