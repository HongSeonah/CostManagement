package com.hongseonah.costmanager.domain.employee.repository;

import com.hongseonah.costmanager.domain.employee.entity.Employee;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByEmployeeCode(String employeeCode);

    List<Employee> findByBusinessUnitId(Long businessUnitId);
}
