package com.hongseonah.costmanager.domain.employee.service;

import com.hongseonah.costmanager.common.exception.BusinessException;
import com.hongseonah.costmanager.domain.businessunit.entity.BusinessUnit;
import com.hongseonah.costmanager.domain.businessunit.repository.BusinessUnitRepository;
import com.hongseonah.costmanager.domain.employee.dto.request.EmployeeCreateRequest;
import com.hongseonah.costmanager.domain.employee.dto.request.EmployeeUpdateRequest;
import com.hongseonah.costmanager.domain.employee.dto.response.EmployeeResponse;
import com.hongseonah.costmanager.domain.employee.entity.Employee;
import com.hongseonah.costmanager.domain.employee.repository.EmployeeRepository;
import com.hongseonah.costmanager.domain.project.entity.CostProject;
import com.hongseonah.costmanager.domain.project.repository.ProjectRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final BusinessUnitRepository businessUnitRepository;
    private final ProjectRepository projectRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               BusinessUnitRepository businessUnitRepository,
                               ProjectRepository projectRepository) {
        this.employeeRepository = employeeRepository;
        this.businessUnitRepository = businessUnitRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    public List<EmployeeResponse> findAll(Long businessUnitId) {
        List<Employee> employees = businessUnitId == null
                ? employeeRepository.findAll()
                : employeeRepository.findByBusinessUnitId(businessUnitId);
        return employees.stream().map(EmployeeResponse::from).toList();
    }

    @Override
    public EmployeeResponse findById(Long id) {
        return EmployeeResponse.from(getEmployee(id));
    }

    @Override
    @Transactional
    public EmployeeResponse create(EmployeeCreateRequest request) {
        if (employeeRepository.existsByEmployeeCode(request.employeeCode())) {
            throw new BusinessException("이미 등록된 인력 코드입니다.");
        }

        Employee employee = new Employee();
        employee.setEmployeeCode(request.employeeCode());
        employee.setEmployeeName(request.employeeName());
        employee.setPositionName(request.positionName());
        employee.setBusinessUnit(getBusinessUnit(request.businessUnitId()));
        employee.setAssignedProject(getProject(request.assignedProjectId()));
        employee.setStatus(request.status());
        employee.setMonthlyLaborCost(request.monthlyLaborCost());
        employee.setJoinedDate(request.joinedDate());

        return EmployeeResponse.from(employeeRepository.save(employee));
    }

    @Override
    @Transactional
    public EmployeeResponse update(Long id, EmployeeUpdateRequest request) {
        Employee employee = getEmployee(id);
        employee.setEmployeeName(request.employeeName());
        employee.setPositionName(request.positionName());
        employee.setBusinessUnit(getBusinessUnit(request.businessUnitId()));
        employee.setAssignedProject(getProject(request.assignedProjectId()));
        employee.setStatus(request.status());
        employee.setMonthlyLaborCost(request.monthlyLaborCost());
        employee.setJoinedDate(request.joinedDate());
        return EmployeeResponse.from(employeeRepository.save(employee));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        employeeRepository.delete(getEmployee(id));
    }

    private Employee getEmployee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("인력을 찾을 수 없습니다."));
    }

    private BusinessUnit getBusinessUnit(Long id) {
        return businessUnitRepository.findById(id)
                .orElseThrow(() -> new BusinessException("본부를 찾을 수 없습니다."));
    }

    private CostProject getProject(Long id) {
        if (id == null) {
            return null;
        }
        return projectRepository.findById(id)
                .orElseThrow(() -> new BusinessException("프로젝트를 찾을 수 없습니다."));
    }
}
