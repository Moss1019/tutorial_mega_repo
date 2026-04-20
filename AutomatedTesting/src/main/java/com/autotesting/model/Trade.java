package com.autotesting.model;

import java.time.LocalDateTime;

public record Trade(String tradeId,
                    String instrument,
                    double openPrice,
                    LocalDateTime openTime,
                    LocalDateTime closeTime,
                    double closePrice,
                    boolean isOpen,
                    String accountId) {
}
