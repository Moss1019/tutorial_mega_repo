package com.jpatutorial;

import com.jpatutorial.model.Account;
import com.jpatutorial.model.Trade;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

// CRUD
/*
Creation
Update
Delete
Read
 */

public class Program {
    static void main() {
        try (EntityManagerFactory factory = Persistence.createEntityManagerFactory("mariadb_ctx");
             EntityManager ctx = factory.createEntityManager()) {

//            createAccount(ctx, "hennerJ");
//            deleteAccount(ctx, "9de0e459-7b2a-4160-bba2-f9b57ab7dd32");

//            List<Account> accounts = readAllAccounts(ctx);
//
//            if(accounts != null) {
//                createTrade(ctx, 1.14, accounts.getFirst());
//            }

            List<Trade> trades = readTradesFor(ctx, "hjaarD");

            for(Trade t: trades) {
                System.out.println(t.tradeId);
            }

        }
    }

    private static void createAccount(EntityManager ctx, String username) {
        Account newAccount = new Account(UUID.randomUUID().toString(),
                username,
                0.0);

        try {
            ctx.getTransaction().begin();

            ctx.persist(newAccount);

            ctx.getTransaction().commit();
        } catch (Exception ex) {
            ctx.getTransaction().rollback();
            System.out.println(ex.getMessage());
        }
    }

    private static void createTrade(EntityManager ctx, double openPrice, Account owningAccount) {
        Trade trade = new Trade(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                openPrice,
                true,
                0.0,
                null,
                owningAccount
        );

        try {
            ctx.getTransaction().begin();

            ctx.persist(trade);

            ctx.getTransaction().commit();
        } catch (Exception ex) {
            ctx.getTransaction().rollback();
            System.out.println(ex.getMessage());
        }
    }

    private static void updateBalance(EntityManager ctx, String accountId, double newBalance) {
        String hq = "update account_ent a set a.balance = :balance where a.accountId = :account_id";

        try {
            ctx.getTransaction().begin();

            int affectedRows = ctx.createQuery(hq)
                    .setParameter("balance", newBalance)
                    .setParameter("account_id", accountId)
                    .executeUpdate();

            System.out.println("Updated " + affectedRows + " row(s)");

            ctx.getTransaction().commit();
        } catch (Exception ex) {
            ctx.getTransaction().rollback();
            System.out.println(ex.getMessage());
        }
    }

    private static void deleteAccount(EntityManager ctx, String accountId) {
        String hq = "delete account_ent a where a.accountId = :account_id";

        try {
            ctx.getTransaction().begin();

            int affectedRows = ctx.createQuery(hq)
                    .setParameter("account_id", accountId)
                    .executeUpdate();

            System.out.println("Deleted " + affectedRows + " row(s)");

            ctx.getTransaction().commit();
        } catch (Exception ex) {
            ctx.getTransaction().rollback();
            System.out.println(ex.getMessage());
        }
    }

    private static List<Account> readAllAccounts(EntityManager ctx) {
        String hq = "select a from account_ent a";

        try {
            List<Account> accounts = ctx.createQuery(hq, Account.class)
                    .getResultList();
            return accounts;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    private static List<Trade> readTradesFor(EntityManager ctx, String username) {
        String hq = "select t from trade_ent t where t.owningAccount.username = :username";

        try {
            List<Trade> trades = ctx.createQuery(hq, Trade.class)
                    .setParameter("username", username)
                    .getResultList();
            return trades;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }
}
