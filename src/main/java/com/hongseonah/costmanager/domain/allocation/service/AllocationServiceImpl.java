package com.hongseonah.costmanager.domain.allocation.service;

import com.hongseonah.costmanager.common.exception.BusinessException;
import com.hongseonah.costmanager.domain.allocation.dto.response.AllocationCloseResponse;
import com.hongseonah.costmanager.domain.allocation.dto.response.AllocationSummaryResponse;
import com.hongseonah.costmanager.domain.allocation.dto.response.BusinessUnitAllocationResponse;
import com.hongseonah.costmanager.domain.allocation.entity.MonthlySettlement;
import com.hongseonah.costmanager.domain.allocation.repository.MonthlySettlementRepository;
import com.hongseonah.costmanager.domain.businessunit.entity.BusinessUnit;
import com.hongseonah.costmanager.domain.businessunit.repository.BusinessUnitRepository;
import com.hongseonah.costmanager.domain.entry.entity.CostEntry;
import com.hongseonah.costmanager.domain.entry.entity.CostEntryCategory;
import com.hongseonah.costmanager.domain.entry.repository.CostEntryRepository;
import com.hongseonah.costmanager.domain.project.entity.CostProject;
import com.hongseonah.costmanager.domain.project.entity.ProjectStatus;
import com.hongseonah.costmanager.domain.project.repository.ProjectRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AllocationServiceImpl implements AllocationService {

    private static final EnumSet<CostEntryCategory> SHARED_COST_CATEGORIES = EnumSet.of(
            CostEntryCategory.INFRASTRUCTURE,
            CostEntryCategory.ETC
    );

    private final BusinessUnitRepository businessUnitRepository;
    private final ProjectRepository projectRepository;
    private final CostEntryRepository costEntryRepository;
    private final MonthlySettlementRepository monthlySettlementRepository;

    public AllocationServiceImpl(BusinessUnitRepository businessUnitRepository,
                                 ProjectRepository projectRepository,
                                 CostEntryRepository costEntryRepository,
                                 MonthlySettlementRepository monthlySettlementRepository) {
        this.businessUnitRepository = businessUnitRepository;
        this.projectRepository = projectRepository;
        this.costEntryRepository = costEntryRepository;
        this.monthlySettlementRepository = monthlySettlementRepository;
    }

    @Override
    public AllocationSummaryResponse getSummary(String month) {
        YearMonth targetMonth = parseMonth(month);
        LocalDate start = targetMonth.atDay(1);
        LocalDate end = targetMonth.atEndOfMonth();
        MonthlySettlement settlement = monthlySettlementRepository.findByMonth(targetMonth.toString()).orElse(null);

        List<BusinessUnit> businessUnits = businessUnitRepository.findAll();
        List<CostProject> projects = projectRepository.findAll();
        List<CostEntry> entries = costEntryRepository.findByEntryDateBetween(start, end);

        Map<Long, BigDecimal> directCostByUnit = new LinkedHashMap<>();
        Map<Long, BigDecimal> sharedCostByUnit = new LinkedHashMap<>();
        for (BusinessUnit unit : businessUnits) {
            directCostByUnit.put(unit.getId(), BigDecimal.ZERO);
            sharedCostByUnit.put(unit.getId(), BigDecimal.ZERO);
        }

        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal directCostTotal = BigDecimal.ZERO;
        BigDecimal sharedCostTotal = BigDecimal.ZERO;

        for (CostEntry entry : entries) {
            BigDecimal amount = safe(entry.getAmount());
            totalCost = totalCost.add(amount);

            CostProject project = entry.getProject();
            BusinessUnit unit = project == null ? null : project.getBusinessUnit();
            if (unit == null || !directCostByUnit.containsKey(unit.getId())) {
                continue;
            }

            if (SHARED_COST_CATEGORIES.contains(entry.getCategory())) {
                sharedCostTotal = sharedCostTotal.add(amount);
            } else {
                directCostTotal = directCostTotal.add(amount);
                directCostByUnit.put(unit.getId(), directCostByUnit.get(unit.getId()).add(amount));
            }
        }

        long totalActiveProjects = projects.stream()
                .filter(project -> project.getStatus() == ProjectStatus.ACTIVE)
                .count();
        BigDecimal finalSharedCostTotal = sharedCostTotal;

        List<BusinessUnitAllocationResponse> unitSummaries = businessUnits.stream()
                .map(unit -> {
                    List<CostProject> unitProjects = projects.stream()
                            .filter(project -> project.getBusinessUnit() != null
                                    && project.getBusinessUnit().getId().equals(unit.getId()))
                            .toList();

                    long projectCount = unitProjects.size();
                    long activeProjectCount = unitProjects.stream()
                            .filter(project -> project.getStatus() == ProjectStatus.ACTIVE)
                            .count();

                    BigDecimal shareRate = resolveShareRate(totalActiveProjects, businessUnits, activeProjectCount);
                    BigDecimal allocatedSharedCost = finalSharedCostTotal
                            .multiply(shareRate)
                            .setScale(2, RoundingMode.HALF_UP);

                    BigDecimal directCost = directCostByUnit.getOrDefault(unit.getId(), BigDecimal.ZERO);
                    BigDecimal totalAllocated = directCost.add(allocatedSharedCost);

                    return new BusinessUnitAllocationResponse(
                            unit.getId(),
                            unit.getUnitCode(),
                            unit.getUnitName(),
                            unit.getManagerName(),
                            projectCount,
                            activeProjectCount,
                            directCost.setScale(2, RoundingMode.HALF_UP),
                            allocatedSharedCost,
                            totalAllocated,
                            shareRate.multiply(BigDecimal.valueOf(100)).setScale(1, RoundingMode.HALF_UP)
                    );
                })
                .sorted(Comparator.comparing(BusinessUnitAllocationResponse::totalCost).reversed())
                .toList();

        return new AllocationSummaryResponse(
                targetMonth.toString(),
                settlement != null,
                settlement == null ? null : settlement.getClosedAt(),
                businessUnits.size(),
                projects.size(),
                entries.size(),
                totalActiveProjects,
                totalCost.setScale(2, RoundingMode.HALF_UP),
                directCostTotal.setScale(2, RoundingMode.HALF_UP),
                sharedCostTotal.setScale(2, RoundingMode.HALF_UP),
                unitSummaries
        );
    }

    @Override
    @Transactional
    public AllocationCloseResponse closeMonth(String month) {
        YearMonth targetMonth = parseMonth(month);
        String monthKey = targetMonth.toString();
        MonthlySettlement existing = monthlySettlementRepository.findByMonth(monthKey).orElse(null);
        if (existing != null) {
            return new AllocationCloseResponse(existing.getMonth(), existing.getClosedAt(), true);
        }

        MonthlySettlement created = new MonthlySettlement();
        created.setMonth(monthKey);
        created.setClosedAt(LocalDateTime.now());
        MonthlySettlement settlement = monthlySettlementRepository.save(created);

        return new AllocationCloseResponse(
                settlement.getMonth(),
                settlement.getClosedAt(),
                false
        );
    }

    @Override
    public byte[] exportMonth(String month) {
        AllocationSummaryResponse summary = getSummary(month);
        YearMonth targetMonth = YearMonth.parse(summary.month());
        LocalDate start = targetMonth.atDay(1);
        LocalDate end = targetMonth.atEndOfMonth();

        List<BusinessUnit> businessUnits = businessUnitRepository.findAll();
        List<CostProject> projects = projectRepository.findAll();
        List<CostEntry> entries = costEntryRepository.findByEntryDateBetween(start, end);

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            CellStyle moneyStyle = createMoneyStyle(workbook);
            CellStyle textStyle = createTextStyle(workbook);

            writeSummarySheet(workbook, summary, headerStyle, moneyStyle, textStyle);
            writeBusinessUnitSheet(workbook, summary, headerStyle, moneyStyle, textStyle);
            writeProjectSheet(workbook, projects, headerStyle, moneyStyle, textStyle, dateStyle);
            writeCostEntrySheet(workbook, entries, headerStyle, moneyStyle, textStyle, dateStyle);

            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException ex) {
            throw new BusinessException("엑셀 파일을 생성하지 못했습니다.");
        }
    }

    private YearMonth parseMonth(String month) {
        if (month == null || month.isBlank()) {
            return YearMonth.now();
        }
        try {
            return YearMonth.parse(month.trim());
        } catch (Exception ex) {
            throw new BusinessException("월 형식은 yyyy-MM 이어야 합니다.");
        }
    }

    private BigDecimal resolveShareRate(long totalActiveProjects,
                                        List<BusinessUnit> businessUnits,
                                        long activeProjectCount) {
        if (totalActiveProjects > 0) {
            return BigDecimal.valueOf(activeProjectCount)
                    .divide(BigDecimal.valueOf(totalActiveProjects), 6, RoundingMode.HALF_UP);
        }
        if (businessUnits.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.ONE.divide(BigDecimal.valueOf(businessUnits.size()), 6, RoundingMode.HALF_UP);
    }

    private BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor((short) 23);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createMoneyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
        return style;
    }

    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("yyyy-mm-dd"));
        return style;
    }

    private CellStyle createTextStyle(Workbook workbook) {
        return workbook.createCellStyle();
    }

    private void writeSummarySheet(Workbook workbook,
                                   AllocationSummaryResponse summary,
                                   CellStyle headerStyle,
                                   CellStyle moneyStyle,
                                   CellStyle textStyle) {
        Sheet sheet = workbook.createSheet("월 요약");
        int rowIndex = 0;

        Row titleRow = sheet.createRow(rowIndex++);
        titleRow.createCell(0).setCellValue("월 요약");

        rowIndex++;
        Row metaRow = sheet.createRow(rowIndex++);
        writeCell(metaRow, 0, "월", textStyle);
        writeCell(metaRow, 1, summary.month(), textStyle);
        writeCell(metaRow, 2, "마감 상태", textStyle);
        writeCell(metaRow, 3, summary.closed() ? "마감 완료" : "마감 전", textStyle);
        writeCell(metaRow, 4, "마감 일시", textStyle);
        writeCell(metaRow, 5, summary.closedAt() == null ? "-" : summary.closedAt().toString(), textStyle);
        writeCell(metaRow, 6, "구분", textStyle);
        writeCell(metaRow, 7, "직접비 / 공통비 / 배부", textStyle);

        Row statsRow = sheet.createRow(rowIndex++);
        writeCell(statsRow, 0, "총 원가", headerStyle);
        writeCell(statsRow, 1, "직접비", headerStyle);
        writeCell(statsRow, 2, "공통비", headerStyle);
        writeCell(statsRow, 3, "본부 수", headerStyle);
        writeCell(statsRow, 4, "프로젝트 수", headerStyle);
        writeCell(statsRow, 5, "원가 건수", headerStyle);

        Row valueRow = sheet.createRow(rowIndex++);
        writeMoneyCell(valueRow, 0, summary.totalCost(), moneyStyle);
        writeMoneyCell(valueRow, 1, summary.directCost(), moneyStyle);
        writeMoneyCell(valueRow, 2, summary.sharedCost(), moneyStyle);
        writeNumberCell(valueRow, 3, summary.businessUnitCount(), textStyle);
        writeNumberCell(valueRow, 4, summary.projectCount(), textStyle);
        writeNumberCell(valueRow, 5, summary.entryCount(), textStyle);

        rowIndex += 2;
        Row sectionRow = sheet.createRow(rowIndex++);
        writeCell(sectionRow, 0, "본부별 배부 현황", headerStyle);

        Row headerRow = sheet.createRow(rowIndex++);
        String[] headers = {"본부", "책임자", "프로젝트 수", "가동 프로젝트", "직접비", "배부 공통비", "합계", "배부 비율"};
        for (int i = 0; i < headers.length; i++) {
            writeCell(headerRow, i, headers[i], headerStyle);
        }

        int start = rowIndex;
        for (var unit : summary.businessUnits()) {
            Row row = sheet.createRow(rowIndex++);
            writeCell(row, 0, unit.unitName(), textStyle);
            writeCell(row, 1, unit.managerName(), textStyle);
            writeNumberCell(row, 2, unit.projectCount(), textStyle);
            writeNumberCell(row, 3, unit.activeProjectCount(), textStyle);
            writeMoneyCell(row, 4, unit.directCost(), moneyStyle);
            writeMoneyCell(row, 5, unit.sharedCost(), moneyStyle);
            writeMoneyCell(row, 6, unit.totalCost(), moneyStyle);
            writeCell(row, 7, unit.allocationRate().toPlainString() + "%", textStyle);
        }

        autoSizeColumns(sheet, 8);
        sheet.createFreezePane(0, start);
    }

    private void writeBusinessUnitSheet(Workbook workbook,
                                        AllocationSummaryResponse summary,
                                        CellStyle headerStyle,
                                        CellStyle moneyStyle,
                                        CellStyle textStyle) {
        Sheet sheet = workbook.createSheet("배부 상세");
        Row headerRow = sheet.createRow(0);
        String[] headers = {"본부", "책임자", "직접비", "배부 공통비", "합계", "배부 비율"};
        for (int i = 0; i < headers.length; i++) {
            writeCell(headerRow, i, headers[i], headerStyle);
        }

        int rowIndex = 1;
        for (var unit : summary.businessUnits()) {
            Row row = sheet.createRow(rowIndex++);
            writeCell(row, 0, unit.unitName(), textStyle);
            writeCell(row, 1, unit.managerName(), textStyle);
            writeMoneyCell(row, 2, unit.directCost(), moneyStyle);
            writeMoneyCell(row, 3, unit.sharedCost(), moneyStyle);
            writeMoneyCell(row, 4, unit.totalCost(), moneyStyle);
            writeCell(row, 5, unit.allocationRate().toPlainString() + "%", textStyle);
        }

        autoSizeColumns(sheet, 6);
        sheet.createFreezePane(0, 1);
    }

    private void writeProjectSheet(Workbook workbook,
                                   List<CostProject> projects,
                                   CellStyle headerStyle,
                                   CellStyle moneyStyle,
                                   CellStyle textStyle,
                                   CellStyle dateStyle) {
        Sheet sheet = workbook.createSheet("프로젝트");
        Row headerRow = sheet.createRow(0);
        String[] headers = {"프로젝트", "본부", "상태", "예산", "집행", "잔여", "시작일", "종료일"};
        for (int i = 0; i < headers.length; i++) {
            writeCell(headerRow, i, headers[i], headerStyle);
        }

        int rowIndex = 1;
        for (CostProject project : projects) {
            Row row = sheet.createRow(rowIndex++);
            writeCell(row, 0, project.getProjectName(), textStyle);
            writeCell(row, 1, project.getBusinessUnit().getUnitName(), textStyle);
            writeCell(row, 2, project.getStatus().name(), textStyle);
            writeMoneyCell(row, 3, safe(project.getBudgetAmount()), moneyStyle);
            writeMoneyCell(row, 4, safe(project.getSpentAmount()), moneyStyle);
            writeMoneyCell(row, 5, safe(project.getBudgetAmount()).subtract(safe(project.getSpentAmount())), moneyStyle);
            writeDateCell(row, 6, project.getStartDate(), dateStyle);
            writeDateCell(row, 7, project.getEndDate(), dateStyle);
        }

        autoSizeColumns(sheet, 8);
        sheet.createFreezePane(0, 1);
    }

    private void writeCostEntrySheet(Workbook workbook,
                                     List<CostEntry> entries,
                                     CellStyle headerStyle,
                                     CellStyle moneyStyle,
                                     CellStyle textStyle,
                                     CellStyle dateStyle) {
        Sheet sheet = workbook.createSheet("원가 항목");
        Row headerRow = sheet.createRow(0);
        String[] headers = {"발생일", "본부", "프로젝트", "구분", "항목", "금액", "메모"};
        for (int i = 0; i < headers.length; i++) {
            writeCell(headerRow, i, headers[i], headerStyle);
        }

        int rowIndex = 1;
        for (CostEntry entry : entries) {
            Row row = sheet.createRow(rowIndex++);
            writeDateCell(row, 0, entry.getEntryDate(), dateStyle);
            writeCell(row, 1, entry.getProject().getBusinessUnit().getUnitName(), textStyle);
            writeCell(row, 2, entry.getProject().getProjectName(), textStyle);
            writeCell(row, 3, entry.getCategory().name(), textStyle);
            writeCell(row, 4, entry.getItemName(), textStyle);
            writeMoneyCell(row, 5, entry.getAmount(), moneyStyle);
            writeCell(row, 6, Objects.toString(entry.getMemo(), ""), textStyle);
        }

        autoSizeColumns(sheet, 7);
        sheet.createFreezePane(0, 1);
    }

    private void writeCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value == null ? "" : value);
        if (style != null) {
            cell.setCellStyle(style);
        }
    }

    private void writeNumberCell(Row row, int column, long value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        if (style != null) {
            cell.setCellStyle(style);
        }
    }

    private void writeMoneyCell(Row row, int column, BigDecimal value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value == null ? 0d : value.doubleValue());
        if (style != null) {
            cell.setCellStyle(style);
        }
    }

    private void writeDateCell(Row row, int column, LocalDate value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value == null ? "" : value.toString());
        if (style != null) {
            cell.setCellStyle(style);
        }
    }

    private void autoSizeColumns(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
