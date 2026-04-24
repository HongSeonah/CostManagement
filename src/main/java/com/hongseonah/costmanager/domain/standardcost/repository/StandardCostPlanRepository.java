package com.hongseonah.costmanager.domain.standardcost.repository;

import com.hongseonah.costmanager.domain.standardcost.entity.StandardCostPlan;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StandardCostPlanRepository extends JpaRepository<StandardCostPlan, Long> {

    boolean existsByStandardCode(String standardCode);

    List<StandardCostPlan> findByPlanMonth(String planMonth);
}
