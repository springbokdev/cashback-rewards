package com.serenitydojo.cashback_rewards.acceptance;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Cashback Monthly Report")
class CashbackMonthlyReportAcceptanceIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Nested
    @DisplayName("Rule: Must report cashback for a single calendar month in the member's local timezone")
    class ReportsForASingleCalendarMonthInTheMembersLocalTimezone {

        @Test
        @DisplayName("The one where a member requests their report for March 2026 — the report contains every "
                + "cashback event with a posting date between 1 March 00:00 and 31 March 23:59 in the member's local timezone")
        void containsEveryEventPostedWithinTheLocalCalendarMonth() throws Exception {
            registerCategory("5999", "Groceries", "0.02");
            registerMerchant("OrchardMarket", true);

            recordPurchase("cust-100", "OrchardMarket", "50.00", "5999", "2026-03-01T00:00:00Z");
            recordPurchase("cust-100", "OrchardMarket", "30.00", "5999", "2026-03-31T23:59:00Z");
            recordPurchase("cust-100", "OrchardMarket", "10.00", "5999", "2026-02-28T23:59:00Z");
            recordPurchase("cust-100", "OrchardMarket", "20.00", "5999", "2026-04-01T00:00:00Z");

            MonthlyReportResponse march = monthlyReport("cust-100", "2026-03", "Z");

            assertThat(march.entries()).hasSize(2);
            assertThat(march.entries())
                    .extracting(Entry::cashbackAmount)
                    .usingElementComparator(BigDecimal::compareTo)
                    .containsExactlyInAnyOrder(new BigDecimal("1.00"), new BigDecimal("0.60"));
        }

        @Test
        @DisplayName("The one where a member in UTC+13 has a transaction that posts at 11pm local on 31 March "
                + "(already 1 April UTC) — it appears in the March report, not April, because we use the "
                + "member's local month (consistent with how the monthly cap is applied)")
        void lateMonthEndTransactionStaysInTheLocalMonthEvenWhenTheUtcDateHasRolledOver() throws Exception {
            registerCategory("5542", "Fuel", "0.02");
            registerMerchant("HighwayFuel", true);

            // Spec says "UTC+13"; that offset can never make 11pm local roll forward in UTC
            // (UTC = local - 13h stays on the same UTC day). Using -11:00 instead, which is
            // physically behind UTC, is the offset that actually produces the described
            // effect: 11pm local 31 March = 10:00 UTC on 1 April.
            recordPurchase("cust-200", "HighwayFuel", "30.00", "5542", "2026-03-31T23:00:00-11:00");

            MonthlyReportResponse march = monthlyReport("cust-200", "2026-03", "-11:00");
            MonthlyReportResponse april = monthlyReport("cust-200", "2026-04", "-11:00");

            assertThat(march.entries()).hasSize(1);
            assertThat(march.entries().getFirst().merchantName()).isEqualTo("HighwayFuel");
            assertThat(april.entries()).isEmpty();
        }
    }

    private void registerCategory(String mcc, String name, String cashbackRate) throws Exception {
        mockMvc.perform(post("/api/categories")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "mcc": "%s",
                                  "name": "%s",
                                  "cashbackRate": "%s"
                                }
                                """.formatted(mcc, name, cashbackRate)))
                .andExpect(status().isCreated());
    }

    private void registerMerchant(String name, boolean partner) throws Exception {
        mockMvc.perform(post("/api/merchants")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "%s",
                                  "partner": %s
                                }
                                """.formatted(name, partner)))
                .andExpect(status().isCreated());
    }

    private void recordPurchase(String customerId, String merchantName, String amount, String mcc, String purchasedAt) throws Exception {
        mockMvc.perform(post("/api/purchases")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": "%s",
                                  "merchantName": "%s",
                                  "amount": "%s",
                                  "mcc": "%s",
                                  "purchasedAt": "%s"
                                }
                                """.formatted(customerId, merchantName, amount, mcc, purchasedAt)))
                .andExpect(status().is2xxSuccessful());
    }

    private MonthlyReportResponse monthlyReport(String customerId, String month, String timezone) throws Exception {
        String body = mockMvc.perform(get("/api/customers/{customerId}/cashback/reports/{month}", customerId, month)
                        .param("timezone", timezone))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readValue(body, new TypeReference<>() {
        });
    }

    record MonthlyReportResponse(List<Entry> entries) {
    }

    record Entry(String postedAt, String merchantName, BigDecimal cashbackAmount) {
    }
}
