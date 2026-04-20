package com.autotesting.extension;

import com.autotesting.db.Tables;
import com.autotesting.db.tables.TBLTrade;
import com.autotesting.model.Trade;
import org.jooq.Result;

import java.util.LinkedList;
import java.util.List;

public class TradeExtension {
    private static final TBLTrade TRD = Tables.TRADE;

    public static List<Trade> fromRecords(Result<?> rs) {
        var res = new LinkedList<Trade>();
        for(var r: rs) {
            res.add(new Trade(
                    r.get(TRD.TRADE_ID),
                    r.get(TRD.INSTRUMENT).trim(),
                    r.get(TRD.OPEN_PRICE),
                    r.get(TRD.OPEN_TIME),
                    r.get(TRD.CLOSE_TIME),
                    r.get(TRD.CLOSE_PRICE),
                    r.get(TRD.IS_OPEN) == 1,
                    r.get(TRD.ACCOUNT_ID)
            ));
        }
        return res;
    }
}
