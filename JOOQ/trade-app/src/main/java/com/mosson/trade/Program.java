package com.mosson.trade;

import com.mosson.trade.db.Tables;
import com.mosson.trade.db.routines.RTNCloseTrade;
import com.mosson.trade.db.tables.TBLAccount;
import com.mosson.trade.db.tables.TBLTrade;
import org.jooq.CloseableDSLContext;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.UUID;

public class Program {
    private final static TBLAccount ACC = Tables.ACCOUNT;

    private final static TBLTrade TRD = Tables.TRADE;

    static void main() {
        try (CloseableDSLContext ctx = DSL.using("jdbc:postgresql://localhost:5432/trade_db", "postgres", "secret123!")) {
            closeTrade(ctx, "trade-125", 2.0);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    //    insert into account(account_id, user_name, balance, profit_loss)
//    values(?, ?, 100.0, 0.0);
    private static void insertAccount(DSLContext ctx, String userName) {
        var insert = ctx.insertInto(ACC)
                .columns(ACC.ACCOUNT_ID,
                        ACC.USER_NAME,
                        ACC.BALANCE,
                        ACC.PROFIT_LOSS)
                .values(UUID.randomUUID().toString(),
                        userName,
                        0.0,
                        0.0);
        System.out.println(insert.getSQL());
        System.out.println(insert.getParams());
        System.out.println("Inserted " + insert.execute() + " account(s)");
    }

    //    update account
//    set balance = 100.0
//    where account_id = 'bde9c943-39fe-431c-b9ec-8e39ed188d20';
    private static void updateAccount(DSLContext ctx, double balance, String accountId) {
        var update = ctx.update(ACC)
                .set(ACC.BALANCE, balance)
                .where(ACC.ACCOUNT_ID.eq(accountId));
        System.out.println(update.getSQL());
        System.out.println(update.getParams());
        System.out.println("Updated " + update.execute() + " account(s)");
    }

    private static void deleteAccountTheOldFashionedWay(Connection connection, String accountId) {
        var deleteSql = "delete from account where account_id = ?";
        try (var deleteStatement = connection.prepareStatement(deleteSql)) {
            deleteStatement.setString(1, accountId);
            deleteStatement.execute();
        } catch (Exception ex) {
            System.out.println("Error while performing delete: " + accountId + " | " + ex.getMessage());
        }
    }

//    delete from account
//    where account_id = 'test-account-123';
    private static void deleteAccount(DSLContext ctx, String accountId) {
        var delete = ctx.deleteFrom(ACC)
                .where(ACC.ACCOUNT_ID.eq(accountId));

        System.out.println(delete.getSQL());
        System.out.println(delete.getParams());
        System.out.println("Deleted " + delete.execute() + " account(s)");
    }

    // select * from account;
    private static void selectAccounts(DSLContext ctx) {
        var select = ctx.select()
                .from(ACC);
        var result = select.fetch();
        var res = new LinkedList<Account>();
        for (var r : result) {
            res.add(new Account(
                    r.get(ACC.ACCOUNT_ID),
                    r.get(ACC.USER_NAME).trim(),
                    r.get(ACC.BALANCE),
                    r.get(ACC.PROFIT_LOSS)
            ));
        }

        for (var a : res) {
            System.out.println(a);
        }
    }

//    select * from trade t
//    join account a
//    on t.account_id = a.account_id
//    where a.user_name = 'NewUser';
    private static void selectTradesForUser(DSLContext ctx, String userName) {
        var select = ctx.select()
                .from(TRD)
                .join(ACC)
                .on(TRD.ACCOUNT_ID.eq(ACC.ACCOUNT_ID))
                .where(ACC.USER_NAME.eq(userName));

        var result = select.fetch();
        var res = new LinkedList<Trade>();
        for (var r : result) {
            res.add(new Trade(
                    r.get(TRD.TRADE_ID),
                    r.get(TRD.INSTRUMENT).trim(),
                    r.get(TRD.OPEN_PRICE),
                    r.get(TRD.OPEN_TIME),
                    r.get(TRD.IS_OPEN),
                    r.get(TRD.ACCOUNT_ID)
            ));
        }

        for (var t : res) {
            System.out.println(t);
        }
    }

    private static void closeTrade(DSLContext ctx, String tradeToCloseId, double closingPrice) {
        var rtnCloseTrade = new RTNCloseTrade();
        rtnCloseTrade.setTradeToCloseId(tradeToCloseId);
        rtnCloseTrade.setCloseingPrice(closingPrice);
        rtnCloseTrade.execute(ctx.configuration());
    }

    public record Account(String accountId, String userName, double balance, double profitLoss) {

    }

    public record Trade(String tradeId, String instrument, double openPrice, LocalDateTime openTime, boolean isOpen, String accountId) {
    }
}
