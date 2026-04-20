package com.autotesting.calculator;

import com.autotesting.model.Trade;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class ProfitLossCalculatorTest {
    @Test()
    public void testSomething() {
        // AAA

        // Arrange - set up test input data, expected outputs and the class to test
        List<Trade> testClosedTrades = new LinkedList<>();
        testClosedTrades.add(createTestTrade(1.0, 1.1));

        double expectedProfit = 0.1;

        ProfitLossCalculator sut = new ProfitLossCalculator();

        // Action - perform the operation to test to get an actual result

        double actual = sut.calculate(testClosedTrades);

        // Assert - validate the result and set up informational messages in case of failure

        Assertions.assertEquals(expectedProfit, actual);
    }

    private Trade createTestTrade(double openPrice, double closePrice) {
        return new Trade(UUID.randomUUID().toString(),
                "eur/usd",
                openPrice,
                LocalDateTime.now(),
                LocalDateTime.now(),
                closePrice,
                false,
                UUID.randomUUID().toString());
    }
}
