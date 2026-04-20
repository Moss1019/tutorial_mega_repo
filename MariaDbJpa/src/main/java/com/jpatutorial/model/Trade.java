package com.jpatutorial.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity(name = "trade_ent")
@Table(name = "trade", indexes = { @Index(name = "idx_trade_open_time", columnList = "open_time") })
public class Trade {
    @Id()
    @Column(name = "trade_id")
    public String tradeId;

    @Column(name = "open_time")
    public LocalDateTime openTime;

    @Column(name = "open_price")
    public double openPrice;

    @Column(name = "is_open")
    public boolean isOpen;

    @Column(name = "close_price")
    public double closePrice;

    @Column(name = "close_time")
    public LocalDateTime closeTime;

    @ManyToOne()
    @JoinColumn(name = "account_id")
    public Account owningAccount;

    public Trade() {
    }

    public Trade(String tradeId, LocalDateTime openTime, double openPrice, boolean isOpen, double closePrice, LocalDateTime closeTime, Account owningAccount) {
        this.tradeId = tradeId;
        this.openTime = openTime;
        this.openPrice = openPrice;
        this.isOpen = isOpen;
        this.closePrice = closePrice;
        this.closeTime = closeTime;
        this.owningAccount = owningAccount;
    }
}
