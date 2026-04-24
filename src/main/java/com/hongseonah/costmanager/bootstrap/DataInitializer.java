package com.hongseonah.costmanager.bootstrap;

import com.hongseonah.costmanager.domain.businessunit.entity.BusinessUnit;
import com.hongseonah.costmanager.domain.businessunit.repository.BusinessUnitRepository;
import com.hongseonah.costmanager.domain.entry.entity.CostEntry;
import com.hongseonah.costmanager.domain.entry.entity.CostEntryCategory;
import com.hongseonah.costmanager.domain.entry.repository.CostEntryRepository;
import com.hongseonah.costmanager.domain.project.entity.CostProject;
import com.hongseonah.costmanager.domain.project.entity.ProjectStatus;
import com.hongseonah.costmanager.domain.project.repository.ProjectRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seed(BusinessUnitRepository businessUnitRepository,
                           ProjectRepository projectRepository,
                           CostEntryRepository costEntryRepository) {
        return args -> {
            if (businessUnitRepository.count() > 0 || projectRepository.count() > 0) {
                return;
            }

            List<BusinessUnit> businessUnits = List.of(
                    createBusinessUnit("HQ-001", "전략본부", "김민재", 4),
                    createBusinessUnit("HQ-002", "영업본부", "이서연", 4),
                    createBusinessUnit("HQ-003", "운영본부", "박준호", 4),
                    createBusinessUnit("HQ-004", "재무본부", "최지은", 4),
                    createBusinessUnit("HQ-005", "디지털혁신본부", "한도윤", 4)
            );
            businessUnitRepository.saveAll(businessUnits);

            Map<String, CostProject> projects = new LinkedHashMap<>();
            projects.put("PRJ-001", createProject("PRJ-001", "원가 기준 정비", "전략기획팀", businessUnits.get(0),
                    ProjectStatus.ACTIVE, "28000000", LocalDate.now().minusMonths(3), LocalDate.now().plusMonths(3)));
            projects.put("PRJ-002", createProject("PRJ-002", "손익 관리 고도화", "전략기획팀", businessUnits.get(0),
                    ProjectStatus.ACTIVE, "34000000", LocalDate.now().minusMonths(2), LocalDate.now().plusMonths(4)));
            projects.put("PRJ-003", createProject("PRJ-003", "신규 수익성 분석", "전략기획팀", businessUnits.get(0),
                    ProjectStatus.ON_HOLD, "18000000", LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(2)));
            projects.put("PRJ-004", createProject("PRJ-004", "사업계획 손익 시뮬레이션", "전략기획팀", businessUnits.get(0),
                    ProjectStatus.ACTIVE, "22000000", LocalDate.now().minusWeeks(6), LocalDate.now().plusMonths(5)));

            projects.put("PRJ-005", createProject("PRJ-005", "고객사 단가 협상", "영업1팀", businessUnits.get(1),
                    ProjectStatus.ACTIVE, "25000000", LocalDate.now().minusMonths(2), LocalDate.now().plusMonths(2)));
            projects.put("PRJ-006", createProject("PRJ-006", "제안서 원가 산정", "영업1팀", businessUnits.get(1),
                    ProjectStatus.ACTIVE, "15000000", LocalDate.now().minusMonths(2), LocalDate.now().plusMonths(1)));
            projects.put("PRJ-007", createProject("PRJ-007", "특판 할인 정책 검토", "영업2팀", businessUnits.get(1),
                    ProjectStatus.ON_HOLD, "9000000", LocalDate.now().minusWeeks(7), LocalDate.now().plusMonths(1)));
            projects.put("PRJ-008", createProject("PRJ-008", "대형 거래처 마진 점검", "영업2팀", businessUnits.get(1),
                    ProjectStatus.ACTIVE, "31000000", LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(3)));

            projects.put("PRJ-009", createProject("PRJ-009", "운영비 절감 과제", "운영혁신팀", businessUnits.get(2),
                    ProjectStatus.ACTIVE, "20000000", LocalDate.now().minusMonths(3), LocalDate.now().plusMonths(2)));
            projects.put("PRJ-010", createProject("PRJ-010", "정산 프로세스 개선", "운영혁신팀", businessUnits.get(2),
                    ProjectStatus.ACTIVE, "12000000", LocalDate.now().minusMonths(2), LocalDate.now().plusMonths(1)));
            projects.put("PRJ-011", createProject("PRJ-011", "배부 로직 재정의", "운영혁신팀", businessUnits.get(2),
                    ProjectStatus.CLOSED, "8000000", LocalDate.now().minusMonths(4), LocalDate.now().minusDays(10)));
            projects.put("PRJ-012", createProject("PRJ-012", "실적 관리 자동화", "운영혁신팀", businessUnits.get(2),
                    ProjectStatus.ACTIVE, "27000000", LocalDate.now().minusWeeks(8), LocalDate.now().plusMonths(4)));

            projects.put("PRJ-013", createProject("PRJ-013", "예산 집행 통제", "재무관리팀", businessUnits.get(3),
                    ProjectStatus.ACTIVE, "19000000", LocalDate.now().minusMonths(2), LocalDate.now().plusMonths(2)));
            projects.put("PRJ-014", createProject("PRJ-014", "월말 결산 보정", "재무관리팀", businessUnits.get(3),
                    ProjectStatus.ACTIVE, "24000000", LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(1)));
            projects.put("PRJ-015", createProject("PRJ-015", "정산 기준표 개정", "재무관리팀", businessUnits.get(3),
                    ProjectStatus.ON_HOLD, "11000000", LocalDate.now().minusWeeks(5), LocalDate.now().plusMonths(2)));
            projects.put("PRJ-016", createProject("PRJ-016", "내부통제 리포트", "재무관리팀", businessUnits.get(3),
                    ProjectStatus.ACTIVE, "16000000", LocalDate.now().minusMonths(3), LocalDate.now().plusMonths(3)));

            projects.put("PRJ-017", createProject("PRJ-017", "회계 자동화 플랫폼", "디지털혁신팀", businessUnits.get(4),
                    ProjectStatus.ACTIVE, "42000000", LocalDate.now().minusMonths(3), LocalDate.now().plusMonths(6)));
            projects.put("PRJ-018", createProject("PRJ-018", "데이터 정합성 검증", "디지털혁신팀", businessUnits.get(4),
                    ProjectStatus.ACTIVE, "17000000", LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(3)));
            projects.put("PRJ-019", createProject("PRJ-019", "보고서 템플릿 표준화", "디지털혁신팀", businessUnits.get(4),
                    ProjectStatus.CLOSED, "7000000", LocalDate.now().minusMonths(5), LocalDate.now().minusDays(7)));
            projects.put("PRJ-020", createProject("PRJ-020", "운영 대시보드 개편", "디지털혁신팀", businessUnits.get(4),
                    ProjectStatus.ACTIVE, "26000000", LocalDate.now().minusWeeks(6), LocalDate.now().plusMonths(4)));

            projectRepository.saveAll(projects.values());

            saveEntry(costEntryRepository, projects.get("PRJ-001"), LocalDate.now().minusDays(8),
                    CostEntryCategory.PERSONNEL, "기획 인력 투입", "원가 기준 정리", "4200000");
            saveEntry(costEntryRepository, projects.get("PRJ-002"), LocalDate.now().minusDays(6),
                    CostEntryCategory.OUTSOURCING, "외부 분석 자문", "손익 분석 검토", "2600000");
            saveEntry(costEntryRepository, projects.get("PRJ-005"), LocalDate.now().minusDays(5),
                    CostEntryCategory.ETC, "제안서 자료 제작", "고객사 협상 자료", "1800000");
            saveEntry(costEntryRepository, projects.get("PRJ-009"), LocalDate.now().minusDays(4),
                    CostEntryCategory.INFRASTRUCTURE, "운영 서버 사용료", "운영 자동화 검증", "900000");
            saveEntry(costEntryRepository, projects.get("PRJ-013"), LocalDate.now().minusDays(3),
                    CostEntryCategory.OUTSOURCING, "결산 검증 용역", "월말 결산 보정", "3500000");
            saveEntry(costEntryRepository, projects.get("PRJ-017"), LocalDate.now().minusDays(2),
                    CostEntryCategory.PERSONNEL, "개발 인력 투입", "플랫폼 자동화", "5200000");

            updateSpent(projects.get("PRJ-001"), new BigDecimal("4200000"));
            updateSpent(projects.get("PRJ-002"), new BigDecimal("2600000"));
            updateSpent(projects.get("PRJ-005"), new BigDecimal("1800000"));
            updateSpent(projects.get("PRJ-009"), new BigDecimal("900000"));
            updateSpent(projects.get("PRJ-013"), new BigDecimal("3500000"));
            updateSpent(projects.get("PRJ-017"), new BigDecimal("5200000"));

            projectRepository.saveAll(List.of(
                    projects.get("PRJ-001"),
                    projects.get("PRJ-002"),
                    projects.get("PRJ-005"),
                    projects.get("PRJ-009"),
                    projects.get("PRJ-013"),
                    projects.get("PRJ-017")
            ));
        };
    }

    private BusinessUnit createBusinessUnit(String unitCode, String unitName, String managerName, int activeProjectLimit) {
        BusinessUnit unit = new BusinessUnit();
        unit.setUnitCode(unitCode);
        unit.setUnitName(unitName);
        unit.setManagerName(managerName);
        unit.setActiveProjectLimit(activeProjectLimit);
        return unit;
    }

    private CostProject createProject(String projectCode,
                                      String projectName,
                                      String clientName,
                                      BusinessUnit businessUnit,
                                      ProjectStatus status,
                                      String budgetAmount,
                                      LocalDate startDate,
                                      LocalDate endDate) {
        CostProject project = new CostProject();
        project.setProjectCode(projectCode);
        project.setProjectName(projectName);
        project.setClientName(clientName);
        project.setBusinessUnit(businessUnit);
        project.setStatus(status);
        project.setBudgetAmount(new BigDecimal(budgetAmount));
        project.setStartDate(startDate);
        project.setEndDate(endDate);
        return project;
    }

    private void saveEntry(CostEntryRepository costEntryRepository,
                           CostProject project,
                           LocalDate entryDate,
                           CostEntryCategory category,
                           String itemName,
                           String memo,
                           String amount) {
        CostEntry entry = new CostEntry();
        entry.setProject(project);
        entry.setEntryDate(entryDate);
        entry.setCategory(category);
        entry.setItemName(itemName);
        entry.setAmount(new BigDecimal(amount));
        entry.setMemo(memo);
        costEntryRepository.save(entry);
    }

    private void updateSpent(CostProject project, BigDecimal amount) {
        project.setSpentAmount(project.getSpentAmount().add(amount));
    }
}
