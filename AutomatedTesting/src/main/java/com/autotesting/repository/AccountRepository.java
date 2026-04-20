package com.autotesting.repository;

import com.autotesting.db.Tables;
import com.autotesting.db.tables.TBLAccount;
import com.autotesting.extension.AccountExtension;
import com.autotesting.model.Account;
import org.jooq.CloseableDSLContext;
import org.jooq.impl.DSL;

import java.util.List;

public class AccountRepository implements AutoCloseable {
    private static final TBLAccount ACC = Tables.ACCOUNT;

    private final CloseableDSLContext ctx;

    public AccountRepository(String connectionString, String username, String password) {
        ctx = DSL.using(connectionString, username, password);
    }

    public Account create(Account account) {
        var insert = ctx.insertInto(ACC)
                .columns(ACC.ACCOUNT_ID,
                        ACC.USERNAME,
                        ACC.BALANCE,
                        ACC.PROFIT_LOSS,
                        ACC.LAST_UPDATE)
                .values(account.accountId(),
                        account.username(),
                        account.balance(),
                        account.profitLoss(),
                        null);
        var inserted = insert.execute();
        if(inserted != 1) {
            System.out.println("Failed to insert account");
        }
        return account;
    }

    public List<Account> readAll() {
        var select = ctx.select()
                .from(ACC);
        var rs = select.fetch();
        return AccountExtension.fromRecords(rs);
    }

    public Account updateProfitLoss(String accountId, double profitLoss) {
        var update = ctx.update(ACC)
                .set(ACC.PROFIT_LOSS, profitLoss)
                .where(ACC.ACCOUNT_ID.eq(accountId));
        var updated = update.execute();
        if(updated != 1) {
            System.out.println("Failed to update account " + accountId);
        }
        var select = ctx.select()
                .from(ACC)
                .where(ACC.ACCOUNT_ID.eq(accountId));
        var rs = select.fetch();
        return AccountExtension.fromRecords(rs).getFirst();
    }

    @Override
    public void close() {
        ctx.close();
    }
}

