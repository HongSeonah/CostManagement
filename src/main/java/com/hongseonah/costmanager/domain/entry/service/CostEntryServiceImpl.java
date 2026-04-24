package com.hongseonah.costmanager.domain.entry.service;

import com.hongseonah.costmanager.common.exception.BusinessException;
import com.hongseonah.costmanager.domain.entry.dto.request.CostEntryCreateRequest;
import com.hongseonah.costmanager.domain.entry.dto.response.CostEntryResponse;
import com.hongseonah.costmanager.domain.entry.entity.CostEntry;
import com.hongseonah.costmanager.domain.entry.repository.CostEntryRepository;
import com.hongseonah.costmanager.domain.allocation.repository.MonthlySettlementRepository;
import com.hongseonah.costmanager.domain.project.entity.CostProject;
import com.hongseonah.costmanager.domain.project.repository.ProjectRepository;
import java.util.List;
import java.time.YearMonth;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CostEntryServiceImpl implements CostEntryService {

    private final CostEntryRepository costEntryRepository;
    private final ProjectRepository projectRepository;
    private final MonthlySettlementRepository monthlySettlementRepository;

    public CostEntryServiceImpl(CostEntryRepository costEntryRepository,
                                ProjectRepository projectRepository,
                                MonthlySettlementRepository monthlySettlementRepository) {
        this.costEntryRepository = costEntryRepository;
        this.projectRepository = projectRepository;
        this.monthlySettlementRepository = monthlySettlementRepository;
    }

    @Override
    public List<CostEntryResponse> findAll() {
        return costEntryRepository.findAll().stream().map(CostEntryResponse::from).toList();
    }

    @Override
    @Transactional
    public CostEntryResponse create(CostEntryCreateRequest request) {
        CostProject project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new BusinessException("프로젝트를 찾을 수 없습니다."));
        String monthKey = YearMonth.from(request.entryDate()).toString();
        if (monthlySettlementRepository.existsByMonth(monthKey)) {
            throw new BusinessException("마감된 월에는 원가를 등록할 수 없습니다.");
        }

        CostEntry entry = new CostEntry();
        entry.setProject(project);
        entry.setEntryDate(request.entryDate());
        entry.setCategory(request.category());
        entry.setItemName(request.itemName());
        entry.setAmount(request.amount());
        entry.setMemo(request.memo());

        project.setSpentAmount(project.getSpentAmount().add(request.amount()));
        projectRepository.save(project);

        return CostEntryResponse.from(costEntryRepository.save(entry));
    }
}
