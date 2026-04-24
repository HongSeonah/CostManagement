package com.hongseonah.costmanager.domain.project.repository;

import com.hongseonah.costmanager.domain.project.entity.CostProject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<CostProject, Long> {
    boolean existsByProjectCode(String projectCode);
    Optional<CostProject> findByProjectCode(String projectCode);
    java.util.List<CostProject> findByBusinessUnitId(Long businessUnitId);
    Page<CostProject> findAll(Pageable pageable);
    Page<CostProject> findByBusinessUnitId(Long businessUnitId, Pageable pageable);
}
