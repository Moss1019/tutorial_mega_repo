package com.autotesting.extension;

import com.autotesting.db.Tables;
import com.autotesting.db.tables.TBLAccount;
import com.autotesting.model.Account;
import org.jooq.Result;

import java.util.LinkedList;
import java.util.List;

public class AccountExtension {
    private static final TBLAccount ACC = Tables.ACCOUNT;

    public static List<Account> fromRecords(Result<?> rs) {
        var res = new LinkedList<Account>();
        for(var r: rs) {
            res.add(new Account(
                    r.get(ACC.ACCOUNT_ID),
                    r.get(ACC.USERNAME).trim(),
                    r.get(ACC.BALANCE),
                    r.get(ACC.PROFIT_LOSS),
                    r.get(ACC.LAST_UPDATE)
            ));
        }
        return res;
    }
}
