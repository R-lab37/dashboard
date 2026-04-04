package com.finance.dashboard.dto.request;

import com.finance.dashboard.enums.Category;
import com.finance.dashboard.enums.RecordType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RecordRequest {

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    @NotNull
    private RecordType type;

    @NotNull
    private Category category;

    @NotNull
    private LocalDate date;

    private String notes;
}