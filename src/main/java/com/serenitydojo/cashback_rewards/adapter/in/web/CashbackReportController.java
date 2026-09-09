package com.serenitydojo.cashback_rewards.adapter.in.web;

import com.serenitydojo.cashback_rewards.application.port.in.GetMonthlyCashbackReportUseCase;
import com.serenitydojo.cashback_rewards.domain.model.CashbackRecord;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CashbackReportController {

    private final GetMonthlyCashbackReportUseCase getMonthlyCashbackReport;

    public CashbackReportController(GetMonthlyCashbackReportUseCase getMonthlyCashbackReport) {
        this.getMonthlyCashbackReport = getMonthlyCashbackReport;
    }

    @GetMapping("/{customerId}/cashback/reports/{month}")
    public MonthlyReportResponse monthlyReport(@PathVariable String customerId,
                                                @PathVariable String month,
                                                @RequestParam String timezone) {
        List<CashbackRecord> entries = getMonthlyCashbackReport.reportFor(
                customerId, YearMonth.parse(month), ZoneId.of(timezone));

        return new MonthlyReportResponse(entries.stream()
                .map(record -> new EntryResponse(record.postedAt(), record.merchantName(), record.cashbackAmount()))
                .toList());
    }

    public record MonthlyReportResponse(List<EntryResponse> entries) {
    }

    public record EntryResponse(Instant postedAt, String merchantName, BigDecimal cashbackAmount) {
    }
}
