package com.hongseonah.costmanager.domain.project.service;

import com.hongseonah.costmanager.common.exception.BusinessException;
import com.hongseonah.costmanager.common.response.PageResponse;
import com.hongseonah.costmanager.domain.businessunit.entity.BusinessUnit;
import com.hongseonah.costmanager.domain.businessunit.repository.BusinessUnitRepository;
import com.hongseonah.costmanager.domain.project.dto.request.ProjectCreateRequest;
import com.hongseonah.costmanager.domain.project.dto.request.ProjectUpdateRequest;
import com.hongseonah.costmanager.domain.project.dto.response.ProjectResponse;
import com.hongseonah.costmanager.domain.project.entity.CostProject;
import com.hongseonah.costmanager.domain.project.repository.ProjectRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final BusinessUnitRepository businessUnitRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository, BusinessUnitRepository businessUnitRepository) {
        this.projectRepository = projectRepository;
        this.businessUnitRepository = businessUnitRepository;
    }

    @Override
    public PageResponse<ProjectResponse> findAll(Long businessUnitId, int page, int size) {
        var pageable = PageRequest.of(page, size);
        var result = businessUnitId == null
                ? projectRepository.findAll(pageable)
                : projectRepository.findByBusinessUnitId(businessUnitId, pageable);

        return new PageResponse<>(
                result.getContent().stream().map(ProjectResponse::from).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Override
    public ProjectResponse findById(Long id) {
        return ProjectResponse.from(getProject(id));
    }

    @Override
    @Transactional
    public ProjectResponse create(ProjectCreateRequest request) {
        if (projectRepository.existsByProjectCode(request.projectCode())) {
            throw new BusinessException("이미 등록된 프로젝트 코드입니다.");
        }

        CostProject project = new CostProject();
        project.setProjectCode(request.projectCode());
        project.setProjectName(request.projectName());
        project.setClientName(request.clientName());
        project.setBusinessUnit(getBusinessUnit(request.businessUnitId()));
        project.setStatus(request.status());
        project.setBudgetAmount(request.budgetAmount());
        project.setStartDate(request.startDate());
        project.setEndDate(request.endDate());

        return ProjectResponse.from(projectRepository.save(project));
    }

    @Override
    @Transactional
    public ProjectResponse update(Long id, ProjectUpdateRequest request) {
        CostProject project = getProject(id);
        project.setProjectName(request.projectName());
        project.setClientName(request.clientName());
        project.setBusinessUnit(getBusinessUnit(request.businessUnitId()));
        project.setStatus(request.status());
        project.setBudgetAmount(request.budgetAmount());
        project.setStartDate(request.startDate());
        project.setEndDate(request.endDate());
        return ProjectResponse.from(projectRepository.save(project));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        CostProject project = getProject(id);
        projectRepository.delete(project);
    }

    private CostProject getProject(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new BusinessException("프로젝트를 찾을 수 없습니다."));
    }

    private BusinessUnit getBusinessUnit(Long id) {
        return businessUnitRepository.findById(id)
                .orElseThrow(() -> new BusinessException("본부를 찾을 수 없습니다."));
    }
}
