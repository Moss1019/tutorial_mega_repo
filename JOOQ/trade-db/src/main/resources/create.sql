create table account
(
    account_id  char(36) primary key,
    user_name   char(16),
    balance     double precision,
    profit_loss double precision
);

create index tdx_account_user_name on account (user_name);

create table trade
(
    trade_id   char(36) primary key,
    instrument char(16),
    open_price double precision,
    open_time  timestamp,
    is_open    bool,
    account_id char(36) references account (account_id) on delete cascade
);

insert into trade(trade_id, instrument, open_price, open_time, is_open, account_id)
values ('trade-125',
        'EUR/USD',
        1.09,
        now(),
        true,
        (select account_id from account limit 1)
    );

create
or replace procedure close_trade (
	trade_to_close_id char(36),
	closeing_price double precision
)
language plpgsql
as
$$
declare
owning_account_id char(36);
	final_profit_loss
double precision;

begin
	if
exists (select from trade where trade_id = trade_to_close_id and is_open = true) then

		owning_account_id := (select account_id from trade where trade_id = trade_to_close_id);
		final_profit_loss := closeing_price - (select open_price from trade where trade_id = trade_to_close_id);

    update account
    set profit_loss = profit_loss + final_profit_loss
    where account_id = owning_account_id;

    update trade
    set is_open = false
    where trade_id = trade_to_close_id;
end if;
end;
$$;
