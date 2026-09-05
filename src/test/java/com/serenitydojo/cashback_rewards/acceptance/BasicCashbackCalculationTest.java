package com.serenitydojo.cashback_rewards.acceptance;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Basic Cashback Calculation")
class BasicCashbackCalculationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Nested
    @DisplayName("Must calculate cashback as the merchant's configured rate applied to the purchase amount, rounded down to 2 decimal places")
    class CashbackIsTheMerchantRateAppliedToThePurchaseAmount {

        @Test
        @DisplayName("The one where a $100.00 purchase earns 5.00% cashback")
        void theOneWhereAStandardPurchaseEarnsCashback() throws Exception {
            String merchantId = registerMerchant("Bookshop", "5.00");

            MvcResult purchase = makePurchase(merchantId, "customer-1", "100.00")
                    .andExpect(status().isCreated())
                    .andReturn();

            assertThat(cashbackAmountIn(purchase)).isEqualByComparingTo("5.00");
        }

        @Test
        @DisplayName("The one where a fractional cent is rounded down")
        void theOneWhereAFractionalCentIsRoundedDown() throws Exception {
            String merchantId = registerMerchant("Coffee Roasters", "3.00");

            MvcResult purchase = makePurchase(merchantId, "customer-2", "49.99")
                    .andExpect(status().isCreated())
                    .andReturn();

            assertThat(cashbackAmountIn(purchase)).isEqualByComparingTo("1.49");
        }

        @Test
        @DisplayName("The one where the merchant rate has two decimal places")
        void theOneWhereTheMerchantRateHasTwoDecimalPlaces() throws Exception {
            String merchantId = registerMerchant("Garden Centre", "2.25");

            MvcResult purchase = makePurchase(merchantId, "customer-3", "25.00")
                    .andExpect(status().isCreated())
                    .andReturn();

            assertThat(cashbackAmountIn(purchase)).isEqualByComparingTo("0.56");
        }

        @Test
        @DisplayName("The one where the purchase is exactly the $1.00 minimum")
        void theOneWhereThePurchaseIsExactlyTheMinimum() throws Exception {
            String merchantId = registerMerchant("Corner Store", "10.00");

            MvcResult purchase = makePurchase(merchantId, "customer-4", "1.00")
                    .andExpect(status().isCreated())
                    .andReturn();

            assertThat(cashbackAmountIn(purchase)).isEqualByComparingTo("0.10");
        }

        @Test
        @DisplayName("The one where the purchase amount is $0.99 - rejected, below the $1.00 minimum")
        void theOneWhereThePurchaseIsBelowTheMinimum() throws Exception {
            String merchantId = registerMerchant("Newsagent", "5.00");

            makePurchase(merchantId, "customer-5", "0.99")
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("The one where the purchase amount is negative - rejected as invalid")
        void theOneWhereThePurchaseAmountIsNegative() throws Exception {
            String merchantId = registerMerchant("Hardware Store", "5.00");

            makePurchase(merchantId, "customer-6", "-10.00")
                    .andExpect(status().isBadRequest());
        }
    }

    private String registerMerchant(String name, String cashbackRate) throws Exception {
        MvcResult result = mockMvc.perform(post("/merchants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "%s",
                                  "cashbackRate": %s
                                }
                                """.formatted(name, cashbackRate)))
                .andExpect(status().isCreated())
                .andReturn();

        return jsonOf(result).get("id").asText();
    }

    private org.springframework.test.web.servlet.ResultActions makePurchase(String merchantId,
                                                                           String customerId,
                                                                           String amount) throws Exception {
        return mockMvc.perform(post("/purchases")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "merchantId": "%s",
                          "customerId": "%s",
                          "amount": %s
                        }
                        """.formatted(merchantId, customerId, amount)));
    }

    private BigDecimal cashbackAmountIn(MvcResult result) throws Exception {
        return jsonOf(result).get("cashbackAmount").decimalValue();
    }

    private JsonNode jsonOf(MvcResult result) throws Exception {
        return objectMapper.reader()
                .with(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
                .readTree(result.getResponse().getContentAsString());
    }
}
