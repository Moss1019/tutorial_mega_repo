package com.autotesting.service;

import com.autotesting.calculator.ProfitLossCalculator;
import com.autotesting.model.Account;
import com.autotesting.repository.AccountRepository;
import com.autotesting.repository.TradeRepository;

import java.util.List;

public class AccountService implements AutoCloseable {
    private final AccountRepository repo;

    private final TradeRepository tradeRepo;

    public AccountService(AccountRepository repo,
                          TradeRepository tradeRepo) {
        this.repo = repo;
        this.tradeRepo = tradeRepo;
    }

    public Account create(Account account) {
        return repo.create(account);
    }

    public List<Account> readAll() {
        return repo.readAll();
    }

    public double updateProfitLossFor(String accountId) {
        var accounts = repo.readAll();
        var accountOpt = accounts.stream().filter(a -> a.accountId().equals(accountId)).findFirst();
        if(accountOpt.isEmpty()) {
            System.out.println("Account not found");
            return 0.0;
        }
        var account = accountOpt.get();
        var closedTrades = tradeRepo.getClosedTradesFor(account.accountId());
        var tradesClosedAfterLastUpdate = closedTrades
                .stream()
                .filter(t -> t.closeTime().isAfter(account.lastUpdated()))
                .toList();
        var calculator = new ProfitLossCalculator();
        var totalProfitLoss = account.profitLoss() + calculator.calculate(tradesClosedAfterLastUpdate);
        repo.updateProfitLoss(accountId, totalProfitLoss);
        return totalProfitLoss;
    }

    @Override
    public void close() {
        repo.close();
    }
}
