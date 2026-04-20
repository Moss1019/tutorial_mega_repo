package com.jpatutorial.model;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

@Entity(name = "account_ent")
@Table(name = "account", indexes = { @Index(name = "idx_account_username", columnList = "username") })
public class Account {
    @Id()
    @Column(name = "account_id")
    public String accountId;

    @Column(name = "username", unique = true)
    public String username;

    @Column(name = "balance")
    @ColumnDefault("0.0")
    public double balance;

    public Account() {
    }

    public Account(String accountId, String username, double balance) {
        this.accountId = accountId;
        this.username = username;
        this.balance = balance;
    }
}
