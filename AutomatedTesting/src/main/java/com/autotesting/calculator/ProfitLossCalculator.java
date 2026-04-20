package com.autotesting.calculator;

import com.autotesting.model.Trade;

import java.util.List;

public class ProfitLossCalculator {
    public double calculate(List<Trade> closedTrades) {
        var totalProfitLoss = 0.0;
        for(var t: closedTrades) {
            if(t.isOpen()) {
                continue;
            }
            totalProfitLoss += (t.closePrice() - t.openPrice());
        }
        return Math.round(totalProfitLoss * 1000.0) / 1000.0;
    }
}
