package com.hongseonah.costmanager.domain.businessunit.service;

import com.hongseonah.costmanager.common.exception.BusinessException;
import com.hongseonah.costmanager.domain.businessunit.dto.request.BusinessUnitCreateRequest;
import com.hongseonah.costmanager.domain.businessunit.dto.request.BusinessUnitUpdateRequest;
import com.hongseonah.costmanager.domain.businessunit.dto.response.BusinessUnitResponse;
import com.hongseonah.costmanager.domain.businessunit.entity.BusinessUnit;
import com.hongseonah.costmanager.domain.businessunit.repository.BusinessUnitRepository;
import com.hongseonah.costmanager.domain.project.repository.ProjectRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BusinessUnitServiceImpl implements BusinessUnitService {

    private final BusinessUnitRepository businessUnitRepository;
    private final ProjectRepository projectRepository;

    public BusinessUnitServiceImpl(BusinessUnitRepository businessUnitRepository, ProjectRepository projectRepository) {
        this.businessUnitRepository = businessUnitRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    public List<BusinessUnitResponse> findAll() {
        return businessUnitRepository.findAll().stream().map(BusinessUnitResponse::from).toList();
    }

    @Override
    public BusinessUnitResponse findById(Long id) {
        BusinessUnit unit = businessUnitRepository.findById(id)
                .orElseThrow(() -> new BusinessException("본부를 찾을 수 없습니다."));
        return BusinessUnitResponse.from(unit);
    }

    @Override
    @Transactional
    public BusinessUnitResponse create(BusinessUnitCreateRequest request) {
        if (businessUnitRepository.existsByUnitCode(request.unitCode())) {
            throw new BusinessException("이미 등록된 본부 코드입니다.");
        }

        BusinessUnit unit = new BusinessUnit();
        unit.setUnitCode(request.unitCode());
        unit.setUnitName(request.unitName());
        unit.setManagerName(request.managerName());
        unit.setActiveProjectLimit(request.activeProjectLimit());
        return BusinessUnitResponse.from(businessUnitRepository.save(unit));
    }

    @Override
    @Transactional
    public BusinessUnitResponse update(Long id, BusinessUnitUpdateRequest request) {
        BusinessUnit unit = businessUnitRepository.findById(id)
                .orElseThrow(() -> new BusinessException("본부를 찾을 수 없습니다."));
        unit.setUnitName(request.unitName());
        unit.setManagerName(request.managerName());
        unit.setActiveProjectLimit(request.activeProjectLimit());
        return BusinessUnitResponse.from(businessUnitRepository.save(unit));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        BusinessUnit unit = businessUnitRepository.findById(id)
                .orElseThrow(() -> new BusinessException("본부를 찾을 수 없습니다."));
        if (!projectRepository.findByBusinessUnitId(id).isEmpty()) {
            throw new BusinessException("연결된 프로젝트가 있어 본부를 삭제할 수 없습니다.");
        }
        businessUnitRepository.delete(unit);
    }
}
