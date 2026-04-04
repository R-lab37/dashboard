package com.finance.dashboard.dto.response;

import com.finance.dashboard.enums.Category;
import com.finance.dashboard.enums.RecordType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class DashboardSummaryResponse {

    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal netBalance;

    private Map<String, BigDecimal> incomeByCategory;
    private Map<String, BigDecimal> expensesByCategory;

    private List<MonthlyTrend> monthlyIncomeTrend;
    private List<MonthlyTrend> monthlyExpenseTrend;

    private List<RecentActivity> recentActivity;

    // -------- INNER CLASSES --------

    @Data
    @AllArgsConstructor
    public static class MonthlyTrend {
        private String month;   // e.g. "2024-03"
        private BigDecimal amount;
    }

    @Data
    @AllArgsConstructor
    public static class RecentActivity {
        private Long id;   // ✅ FIXED (Long instead of String)
        private BigDecimal amount;
        private RecordType type;
        private Category category;
        private LocalDate date;
        private String notes;
    }
}