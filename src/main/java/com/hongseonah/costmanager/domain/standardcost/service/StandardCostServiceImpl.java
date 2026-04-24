package com.hongseonah.costmanager.domain.standardcost.service;

import com.hongseonah.costmanager.common.exception.BusinessException;
import com.hongseonah.costmanager.domain.businessunit.entity.BusinessUnit;
import com.hongseonah.costmanager.domain.businessunit.repository.BusinessUnitRepository;
import com.hongseonah.costmanager.domain.project.entity.CostProject;
import com.hongseonah.costmanager.domain.project.repository.ProjectRepository;
import com.hongseonah.costmanager.domain.standardcost.dto.request.StandardCostCreateRequest;
import com.hongseonah.costmanager.domain.standardcost.dto.request.StandardCostUpdateRequest;
import com.hongseonah.costmanager.domain.standardcost.dto.response.StandardCostResponse;
import com.hongseonah.costmanager.domain.standardcost.entity.StandardCostBasisType;
import com.hongseonah.costmanager.domain.standardcost.entity.StandardCostPlan;
import com.hongseonah.costmanager.domain.standardcost.repository.StandardCostPlanRepository;
import java.time.YearMonth;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class StandardCostServiceImpl implements StandardCostService {

    private final StandardCostPlanRepository planRepository;
    private final BusinessUnitRepository businessUnitRepository;
    private final ProjectRepository projectRepository;

    public StandardCostServiceImpl(StandardCostPlanRepository planRepository,
                                   BusinessUnitRepository businessUnitRepository,
                                   ProjectRepository projectRepository) {
        this.planRepository = planRepository;
        this.businessUnitRepository = businessUnitRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    public List<StandardCostResponse> findAll(String month) {
        List<StandardCostPlan> plans = isBlank(month)
                ? planRepository.findAll()
                : planRepository.findByPlanMonth(parseMonth(month).toString());
        return plans.stream().map(StandardCostResponse::from).toList();
    }

    @Override
    public StandardCostResponse findById(Long id) {
        return StandardCostResponse.from(getPlan(id));
    }

    @Override
    @Transactional
    public StandardCostResponse create(StandardCostCreateRequest request) {
        if (planRepository.existsByStandardCode(request.standardCode())) {
            throw new BusinessException("이미 등록된 표준원가 코드입니다.");
        }
        BusinessUnit businessUnit = getBusinessUnit(request.businessUnitId());
        CostProject project = getProject(request.projectId());
        validateBasisRelation(request.basisType(), businessUnit, project);
        StandardCostPlan plan = new StandardCostPlan();
        plan.setStandardCode(request.standardCode());
        plan.setPlanMonth(parseMonth(request.planMonth()).toString());
        plan.setBusinessUnit(businessUnit);
        plan.setProject(project);
        plan.setBasisType(request.basisType());
        plan.setStandardAmount(request.standardAmount());
        plan.setMemo(request.memo());
        return StandardCostResponse.from(planRepository.save(plan));
    }

    @Override
    @Transactional
    public StandardCostResponse update(Long id, StandardCostUpdateRequest request) {
        StandardCostPlan plan = getPlan(id);
        BusinessUnit businessUnit = getBusinessUnit(request.businessUnitId());
        CostProject project = getProject(request.projectId());
        validateBasisRelation(request.basisType(), businessUnit, project);
        plan.setPlanMonth(parseMonth(request.planMonth()).toString());
        plan.setBusinessUnit(businessUnit);
        plan.setProject(project);
        plan.setBasisType(request.basisType());
        plan.setStandardAmount(request.standardAmount());
        plan.setMemo(request.memo());
        return StandardCostResponse.from(planRepository.save(plan));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        planRepository.delete(getPlan(id));
    }

    private StandardCostPlan getPlan(Long id) {
        return planRepository.findById(id)
                .orElseThrow(() -> new BusinessException("표준원가를 찾을 수 없습니다."));
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

    private void validateBasisRelation(StandardCostBasisType basisType,
                                       BusinessUnit businessUnit,
                                       CostProject project) {
        if (basisType == StandardCostBasisType.PROJECT && project == null) {
            throw new BusinessException("프로젝트 기준 표준원가는 프로젝트를 선택해야 합니다.");
        }
        if (basisType == StandardCostBasisType.BUSINESS_UNIT && project != null) {
            throw new BusinessException("본부 기준 표준원가는 프로젝트를 선택하지 않습니다.");
        }
        if (project != null
                && project.getBusinessUnit() != null
                && !project.getBusinessUnit().getId().equals(businessUnit.getId())) {
            throw new BusinessException("선택한 프로젝트의 본부와 표준원가 본부가 일치하지 않습니다.");
        }
    }

    private YearMonth parseMonth(String month) {
        if (month == null || month.isBlank()) {
            return YearMonth.now();
        }
        return YearMonth.parse(month.trim());
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
