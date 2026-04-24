package com.hongseonah.costmanager.domain.internaltransfer.service;

import com.hongseonah.costmanager.common.exception.BusinessException;
import com.hongseonah.costmanager.domain.businessunit.entity.BusinessUnit;
import com.hongseonah.costmanager.domain.businessunit.repository.BusinessUnitRepository;
import com.hongseonah.costmanager.domain.internaltransfer.dto.request.InternalTransferCreateRequest;
import com.hongseonah.costmanager.domain.internaltransfer.dto.request.InternalTransferUpdateRequest;
import com.hongseonah.costmanager.domain.internaltransfer.dto.response.InternalTransferResponse;
import com.hongseonah.costmanager.domain.internaltransfer.entity.InternalTransfer;
import com.hongseonah.costmanager.domain.internaltransfer.repository.InternalTransferRepository;
import com.hongseonah.costmanager.domain.project.entity.CostProject;
import com.hongseonah.costmanager.domain.project.repository.ProjectRepository;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class InternalTransferServiceImpl implements InternalTransferService {

    private final InternalTransferRepository transferRepository;
    private final BusinessUnitRepository businessUnitRepository;
    private final ProjectRepository projectRepository;

    public InternalTransferServiceImpl(InternalTransferRepository transferRepository,
                                       BusinessUnitRepository businessUnitRepository,
                                       ProjectRepository projectRepository) {
        this.transferRepository = transferRepository;
        this.businessUnitRepository = businessUnitRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    public List<InternalTransferResponse> findAll(String month) {
        List<InternalTransfer> transfers = isBlank(month)
                ? transferRepository.findAll()
                : transferRepository.findByTransferDateBetween(parseMonth(month).atDay(1), parseMonth(month).atEndOfMonth());
        return transfers.stream().map(InternalTransferResponse::from).toList();
    }

    @Override
    public InternalTransferResponse findById(Long id) {
        return InternalTransferResponse.from(getTransfer(id));
    }

    @Override
    @Transactional
    public InternalTransferResponse create(InternalTransferCreateRequest request) {
        if (transferRepository.existsByTransferCode(request.transferCode())) {
            throw new BusinessException("이미 등록된 내부대체 코드입니다.");
        }
        if (request.sourceBusinessUnitId().equals(request.targetBusinessUnitId())) {
            throw new BusinessException("출발 본부와 도착 본부는 달라야 합니다.");
        }
        InternalTransfer transfer = new InternalTransfer();
        transfer.setTransferCode(request.transferCode());
        transfer.setTransferDate(request.transferDate());
        BusinessUnit sourceBusinessUnit = getBusinessUnit(request.sourceBusinessUnitId());
        BusinessUnit targetBusinessUnit = getBusinessUnit(request.targetBusinessUnitId());
        CostProject sourceProject = getProject(request.sourceProjectId());
        CostProject targetProject = getProject(request.targetProjectId());
        validateProjectOwnership(sourceBusinessUnit, sourceProject, "출발");
        validateProjectOwnership(targetBusinessUnit, targetProject, "도착");
        transfer.setSourceBusinessUnit(sourceBusinessUnit);
        transfer.setTargetBusinessUnit(targetBusinessUnit);
        transfer.setSourceProject(sourceProject);
        transfer.setTargetProject(targetProject);
        transfer.setAmount(request.amount());
        transfer.setMemo(request.memo());
        return InternalTransferResponse.from(transferRepository.save(transfer));
    }

    @Override
    @Transactional
    public InternalTransferResponse update(Long id, InternalTransferUpdateRequest request) {
        if (request.sourceBusinessUnitId().equals(request.targetBusinessUnitId())) {
            throw new BusinessException("출발 본부와 도착 본부는 달라야 합니다.");
        }
        InternalTransfer transfer = getTransfer(id);
        transfer.setTransferDate(request.transferDate());
        BusinessUnit sourceBusinessUnit = getBusinessUnit(request.sourceBusinessUnitId());
        BusinessUnit targetBusinessUnit = getBusinessUnit(request.targetBusinessUnitId());
        CostProject sourceProject = getProject(request.sourceProjectId());
        CostProject targetProject = getProject(request.targetProjectId());
        validateProjectOwnership(sourceBusinessUnit, sourceProject, "출발");
        validateProjectOwnership(targetBusinessUnit, targetProject, "도착");
        transfer.setSourceBusinessUnit(sourceBusinessUnit);
        transfer.setTargetBusinessUnit(targetBusinessUnit);
        transfer.setSourceProject(sourceProject);
        transfer.setTargetProject(targetProject);
        transfer.setAmount(request.amount());
        transfer.setMemo(request.memo());
        return InternalTransferResponse.from(transferRepository.save(transfer));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        transferRepository.delete(getTransfer(id));
    }

    private InternalTransfer getTransfer(Long id) {
        return transferRepository.findById(id)
                .orElseThrow(() -> new BusinessException("내부대체가액을 찾을 수 없습니다."));
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

    private void validateProjectOwnership(BusinessUnit businessUnit, CostProject project, String label) {
        if (project == null || project.getBusinessUnit() == null) {
            return;
        }
        if (!project.getBusinessUnit().getId().equals(businessUnit.getId())) {
            throw new BusinessException(label + " 본부와 연결된 프로젝트가 일치하지 않습니다.");
        }
    }

    private YearMonth parseMonth(String month) {
        return YearMonth.parse(month.trim());
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
