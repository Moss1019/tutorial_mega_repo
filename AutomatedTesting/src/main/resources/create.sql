create table account
(
    account_id  char(36) primary key,
    username    char(16),
    balance     double precision,
    profit_loss double precision,
    last_update timestamp
);

create table trade
(
    trade_id    char(36) primary key,
    instrument  char(8),
    open_price  double precision,
    open_time   timestamp,
    close_time  timestamp,
    close_price double precision,
    is_open     boolean,
    account_id  char(36) references account (account_id)
);