package com.hongseonah.costmanager.domain.analysis.service;

import com.hongseonah.costmanager.domain.allocation.service.AllocationService;
import com.hongseonah.costmanager.domain.analysis.dto.response.AnalysisSummaryResponse;
import com.hongseonah.costmanager.domain.analysis.dto.response.BusinessUnitAnalysisResponse;
import com.hongseonah.costmanager.domain.businessunit.repository.BusinessUnitRepository;
import com.hongseonah.costmanager.domain.employee.entity.EmployeeStatus;
import com.hongseonah.costmanager.domain.employee.repository.EmployeeRepository;
import com.hongseonah.costmanager.domain.internaltransfer.repository.InternalTransferRepository;
import com.hongseonah.costmanager.domain.project.entity.ProjectStatus;
import com.hongseonah.costmanager.domain.project.repository.ProjectRepository;
import com.hongseonah.costmanager.domain.standardcost.repository.StandardCostPlanRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AnalysisServiceImpl implements AnalysisService {

    private final BusinessUnitRepository businessUnitRepository;
    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;
    private final StandardCostPlanRepository standardCostPlanRepository;
    private final InternalTransferRepository internalTransferRepository;
    private final AllocationService allocationService;

    public AnalysisServiceImpl(BusinessUnitRepository businessUnitRepository,
                               ProjectRepository projectRepository,
                               EmployeeRepository employeeRepository,
                               StandardCostPlanRepository standardCostPlanRepository,
                               InternalTransferRepository internalTransferRepository,
                               AllocationService allocationService) {
        this.businessUnitRepository = businessUnitRepository;
        this.projectRepository = projectRepository;
        this.employeeRepository = employeeRepository;
        this.standardCostPlanRepository = standardCostPlanRepository;
        this.internalTransferRepository = internalTransferRepository;
        this.allocationService = allocationService;
    }

    @Override
    public AnalysisSummaryResponse getSummary(String month) {
        YearMonth targetMonth = parseMonth(month);
        String monthKey = targetMonth.toString();
        LocalDate start = targetMonth.atDay(1);
        LocalDate end = targetMonth.atEndOfMonth();

        var allocationSummary = allocationService.getSummary(monthKey);
        var businessUnits = businessUnitRepository.findAll();
        var projects = projectRepository.findAll();
        var employees = employeeRepository.findAll();
        var standardPlans = standardCostPlanRepository.findByPlanMonth(monthKey);
        var transfers = internalTransferRepository.findByTransferDateBetween(start, end);

        BigDecimal laborCostTotal = employees.stream()
                .filter(employee -> employee.getStatus() != EmployeeStatus.LEFT)
                .map(employee -> safe(employee.getMonthlyLaborCost()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal standardCostTotal = standardPlans.stream()
                .map(plan -> safe(plan.getStandardAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal transferAmountTotal = transfers.stream()
                .map(transfer -> safe(transfer.getAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<BusinessUnitAnalysisResponse> unitSummaries = businessUnits.stream()
                .map(unit -> {
                    var unitProjects = projects.stream()
                            .filter(project -> project.getBusinessUnit() != null
                                    && project.getBusinessUnit().getId().equals(unit.getId()))
                            .toList();
                    var unitEmployees = employees.stream()
                            .filter(employee -> employee.getBusinessUnit() != null
                                    && employee.getBusinessUnit().getId().equals(unit.getId())
                                    && employee.getStatus() != EmployeeStatus.LEFT)
                            .toList();
                    var unitStandards = standardPlans.stream()
                            .filter(plan -> plan.getBusinessUnit() != null
                                    && plan.getBusinessUnit().getId().equals(unit.getId()))
                            .toList();
                    var outgoingTransfers = transfers.stream()
                            .filter(transfer -> transfer.getSourceBusinessUnit() != null
                                    && transfer.getSourceBusinessUnit().getId().equals(unit.getId()))
                            .toList();
                    var incomingTransfers = transfers.stream()
                            .filter(transfer -> transfer.getTargetBusinessUnit() != null
                                    && transfer.getTargetBusinessUnit().getId().equals(unit.getId()))
                            .toList();

                    BigDecimal actualCost = allocationSummary.businessUnits().stream()
                            .filter(item -> item.id().equals(unit.getId()))
                            .map(item -> safe(item.totalCost()))
                            .findFirst()
                            .orElse(BigDecimal.ZERO);
                    BigDecimal laborCost = unitEmployees.stream()
                            .map(employee -> safe(employee.getMonthlyLaborCost()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal standardCost = unitStandards.stream()
                            .map(plan -> safe(plan.getStandardAmount()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal transferInAmount = incomingTransfers.stream()
                            .map(transfer -> safe(transfer.getAmount()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal transferOutAmount = outgoingTransfers.stream()
                            .map(transfer -> safe(transfer.getAmount()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal transferNetAmount = transferInAmount.subtract(transferOutAmount);
                    BigDecimal variance = actualCost.add(laborCost).add(transferNetAmount).subtract(standardCost);
                    BigDecimal performanceRate = standardCost.compareTo(BigDecimal.ZERO) == 0
                            ? BigDecimal.ZERO
                            : actualCost.add(laborCost).add(transferNetAmount)
                                    .multiply(BigDecimal.valueOf(100))
                                    .divide(standardCost, 1, RoundingMode.HALF_UP);

                    return new BusinessUnitAnalysisResponse(
                            unit.getId(),
                            unit.getUnitCode(),
                            unit.getUnitName(),
                            unit.getManagerName(),
                            unitEmployees.size(),
                            unitProjects.size(),
                            actualCost.setScale(2, RoundingMode.HALF_UP),
                            standardCost.setScale(2, RoundingMode.HALF_UP),
                            laborCost.setScale(2, RoundingMode.HALF_UP),
                            transferInAmount.setScale(2, RoundingMode.HALF_UP),
                            transferOutAmount.setScale(2, RoundingMode.HALF_UP),
                            transferNetAmount.setScale(2, RoundingMode.HALF_UP),
                            variance.setScale(2, RoundingMode.HALF_UP),
                            performanceRate
                    );
                })
                .sorted(Comparator.comparing(BusinessUnitAnalysisResponse::variance).reversed())
                .toList();

        long employeeCount = employees.stream()
                .filter(employee -> employee.getStatus() != EmployeeStatus.LEFT)
                .count();

        return new AnalysisSummaryResponse(
                monthKey,
                businessUnits.size(),
                projects.stream().filter(project -> project.getStatus() == ProjectStatus.ACTIVE).count(),
                employeeCount,
                allocationSummary.totalCost().add(laborCostTotal).setScale(2, RoundingMode.HALF_UP),
                standardCostTotal.setScale(2, RoundingMode.HALF_UP),
                laborCostTotal.setScale(2, RoundingMode.HALF_UP),
                transferAmountTotal.setScale(2, RoundingMode.HALF_UP),
                allocationSummary.totalCost()
                        .add(laborCostTotal)
                        .subtract(standardCostTotal)
                        .setScale(2, RoundingMode.HALF_UP),
                unitSummaries
        );
    }

    private YearMonth parseMonth(String month) {
        if (month == null || month.isBlank()) {
            return YearMonth.now();
        }
        return YearMonth.parse(month.trim());
    }

    private BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
