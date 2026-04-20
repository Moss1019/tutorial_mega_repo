package com.autotesting.repository;

import com.autotesting.db.Tables;
import com.autotesting.db.tables.TBLTrade;
import com.autotesting.extension.TradeExtension;
import com.autotesting.model.Trade;
import org.jooq.CloseableDSLContext;
import org.jooq.impl.DSL;

import java.time.LocalDateTime;
import java.util.List;

public class TradeRepository implements AutoCloseable {
    private static final TBLTrade TRD = Tables.TRADE;

    private static final byte IS_OPEN = (byte)1;

    private static final byte IS_CLOSE = (byte)0;

    private final CloseableDSLContext ctx;

    //    @Inject()
    public TradeRepository(String connectionString, String username, String password) {
        ctx = DSL.using(connectionString, username, password);
    }

    public Trade create(Trade trade) {
        var insert = ctx.insertInto(TRD)
                .columns(TRD.TRADE_ID,
                        TRD.INSTRUMENT,
                        TRD.OPEN_PRICE,
                        TRD.OPEN_TIME,
                        TRD.CLOSE_TIME,
                        TRD.CLOSE_PRICE,
                        TRD.IS_OPEN,
                        TRD.ACCOUNT_ID)
                .values(trade.tradeId(),
                        trade.instrument(),
                        trade.openPrice(),
                        LocalDateTime.now(),
                        null,
                        -1.0,
                        (byte)1,
                        trade.accountId());
        var inserted = insert.execute();
        if(inserted != 1) {
            System.out.println("Failed to insert trade");
        }
        return trade;
    }

    public List<Trade> readAll() {
        var select = ctx
                .select()
                .from(TRD);
        var rs = select.fetch();
        return TradeExtension.fromRecords(rs);
    }

    public List<Trade> getFor(String accountId) {
        var select = ctx
                .select()
                .from(TRD)
                .where(TRD.ACCOUNT_ID.eq(accountId));
        var rs = select.fetch();
        return TradeExtension.fromRecords(rs);
    }

    public List<Trade> getOpenTradesFor(String accountId) {
        var select = ctx
                .select()
                .from(TRD)
                .where(TRD.ACCOUNT_ID.eq(accountId))
                .and(TRD.IS_OPEN.eq(IS_OPEN));
        var rs = select.fetch();
        return TradeExtension.fromRecords(rs);
    }

    public List<Trade> getClosedTradesFor(String accountId) {
        var select = ctx
                .select()
                .from(TRD)
                .where(TRD.ACCOUNT_ID.eq(accountId))
                .and(TRD.IS_OPEN.eq(IS_CLOSE));
        var rs = select.fetch();
        return TradeExtension.fromRecords(rs);
    }

    @Override
//    @PreDestroy()
    public void close() throws Exception {
        ctx.close();
    }
}
