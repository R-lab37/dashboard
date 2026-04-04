package com.finance.dashboard.service;

import com.finance.dashboard.dto.response.DashboardSummaryResponse;
import com.finance.dashboard.enums.RecordType;
import com.finance.dashboard.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final FinancialRecordRepository recordRepository;

    public DashboardSummaryResponse getSummary() {

        BigDecimal totalIncome =
                recordRepository.sumByType(RecordType.INCOME);

        BigDecimal totalExpenses =
                recordRepository.sumByType(RecordType.EXPENSE);

        BigDecimal netBalance =
                totalIncome.subtract(totalExpenses);

        return DashboardSummaryResponse.builder()
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netBalance(netBalance)
                .incomeByCategory(toCategoryMap(RecordType.INCOME))
                .expensesByCategory(toCategoryMap(RecordType.EXPENSE))
                .monthlyIncomeTrend(toTrendList(RecordType.INCOME))
                .monthlyExpenseTrend(toTrendList(RecordType.EXPENSE))
                .recentActivity(toRecentList())
                .build();
    }

    private Map<String, BigDecimal> toCategoryMap(RecordType type) {

        return recordRepository.sumGroupedByCategory(type).stream()
                .collect(Collectors.toMap(
                        row -> row[0].toString(),
                        row -> (BigDecimal) row[1]
                ));
    }

    private List<DashboardSummaryResponse.MonthlyTrend> toTrendList(RecordType type) {

        return recordRepository.monthlyTrend(type).stream()
                .map(row -> new DashboardSummaryResponse.MonthlyTrend(
                        row[0].toString(),
                        (BigDecimal) row[1]
                ))
                .collect(Collectors.toList());
    }

    private List<DashboardSummaryResponse.RecentActivity> toRecentList() {

        return recordRepository
                .findTop10ByDeletedFalseOrderByCreatedAtDesc()
                .stream()
                .map(r -> new DashboardSummaryResponse.RecentActivity(
                        r.getId(),
                        r.getAmount(),
                        r.getType(),
                        r.getCategory(),
                        r.getDate(),
                        r.getNotes()
                ))
                .collect(Collectors.toList());
    }
}