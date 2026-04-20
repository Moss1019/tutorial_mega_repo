package com.autotesting.service;

import com.autotesting.model.Trade;
import com.autotesting.repository.TradeRepository;

import java.util.List;

public class TradeService {
    private final TradeRepository repo;

    public TradeService(TradeRepository repo) {
        this.repo = repo;
    }

    public Trade create(Trade trade) {
        return repo.create(trade);
    }

    public List<Trade> readAll() {
        return repo.readAll();
    }

    public List<Trade> getFor(String accountId) {
        return repo.getFor(accountId);
    }

    public List<Trade> getOpenTradesFor(String accountId) {
        return repo.getOpenTradesFor(accountId);
    }

    public List<Trade> getClosedTradesFor(String accountId) {
        return repo.getClosedTradesFor(accountId);
    }
}

