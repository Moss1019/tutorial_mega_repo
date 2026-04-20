package com.autotesting.model;

import java.time.LocalDateTime;

public record Account(String accountId,
                      String username,
                      double balance,
                      double profitLoss,
                      LocalDateTime lastUpdated) {
}
