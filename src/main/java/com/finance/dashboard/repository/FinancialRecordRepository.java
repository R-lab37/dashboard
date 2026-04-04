package com.finance.dashboard.repository;

import com.finance.dashboard.entity.FinancialRecord;
import com.finance.dashboard.enums.Category;
import com.finance.dashboard.enums.RecordType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {

    // Filtered list — exclude soft-deleted records
    @Query("""
        SELECT r FROM FinancialRecord r
        WHERE r.deleted = false
          AND (:type IS NULL OR r.type = :type)
          AND (:category IS NULL OR r.category = :category)
          AND (:from IS NULL OR r.date >= :from)
          AND (:to IS NULL OR r.date <= :to)
        ORDER BY r.date DESC
    """)
    Page<FinancialRecord> findWithFilters(
            @Param("type") RecordType type,
            @Param("category") Category category,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            Pageable pageable
    );

    // Dashboard aggregates
    @Query("""
        SELECT COALESCE(SUM(r.amount),0)
        FROM FinancialRecord r
        WHERE r.deleted = false AND r.type = :type
    """)
    BigDecimal sumByType(@Param("type") RecordType type);

    @Query("""
        SELECT r.category, SUM(r.amount)
        FROM FinancialRecord r
        WHERE r.deleted = false AND r.type = :type
        GROUP BY r.category
    """)
    List<Object[]> sumGroupedByCategory(@Param("type") RecordType type);

    // ✅ MySQL FIX (DATE_FORMAT instead of TO_CHAR)
    @Query("""
        SELECT FUNCTION('DATE_FORMAT', r.date, '%Y-%m'), SUM(r.amount)
        FROM FinancialRecord r
        WHERE r.deleted = false AND r.type = :type
        GROUP BY FUNCTION('DATE_FORMAT', r.date, '%Y-%m')
        ORDER BY 1 ASC
    """)
    List<Object[]> monthlyTrend(@Param("type") RecordType type);

    List<FinancialRecord> findTop10ByDeletedFalseOrderByCreatedAtDesc();
}