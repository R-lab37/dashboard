package com.finance.dashboard.service;

import com.finance.dashboard.dto.request.RecordRequest;
import com.finance.dashboard.entity.FinancialRecord;
import com.finance.dashboard.entity.User;
import com.finance.dashboard.enums.Category;
import com.finance.dashboard.enums.RecordType;
import com.finance.dashboard.repository.FinancialRecordRepository;
import com.finance.dashboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class FinancialRecordService {

    private final FinancialRecordRepository recordRepository;
    private final UserRepository userRepository;

    public Page<FinancialRecord> getRecords(
            RecordType type,
            Category category,
            LocalDate from,
            LocalDate to,
            int page,
            int size
    ) {
        return recordRepository.findWithFilters(
                type,
                category,
                from,
                to,
                PageRequest.of(page, size)
        );
    }

    public FinancialRecord getById(Long id) {   // ✅ Long FIX
        return recordRepository.findById(id)
                .filter(r -> !r.isDeleted())
                .orElseThrow(() -> new RuntimeException("Record not found"));
    }

    public FinancialRecord create(RecordRequest request, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        FinancialRecord record = FinancialRecord.builder()
                .amount(request.getAmount())
                .type(request.getType())
                .category(request.getCategory())
                .date(request.getDate())
                .notes(request.getNotes())
                .createdBy(user)
                .build();

        return recordRepository.save(record);
    }

    public FinancialRecord update(Long id, RecordRequest request) {   // ✅ Long FIX

        FinancialRecord record = getById(id);

        record.setAmount(request.getAmount());
        record.setType(request.getType());
        record.setCategory(request.getCategory());
        record.setDate(request.getDate());
        record.setNotes(request.getNotes());

        return recordRepository.save(record);
    }

    public void delete(Long id) {   // ✅ Long FIX

        FinancialRecord record = getById(id);
        record.setDeleted(true);   // soft delete

        recordRepository.save(record);
    }
}