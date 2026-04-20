package com.autotesting.repository;

import com.autotesting.db.Tables;
import com.autotesting.model.Account;
import org.jooq.CloseableDSLContext;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class AccountRepositoryTest {
    private static GenericContainer<?> mariaDb;

    private static  String connectionString;

    @BeforeAll()
    public static void init() {
        mariaDb = new GenericContainer<>("mariadb:12.3.1-ubi10-rc")
                .withEnv("MARIADB_ROOT_PASSWORD", "asd")
                .withEnv("MARIADB_DATABASE", "test_db")
                .withExposedPorts(3306);
        mariaDb.start();

        connectionString = String.format("jdbc:mariadb://%s:%d/test_db",
                mariaDb.getHost(),
                mariaDb.getMappedPort(3306));

        try (CloseableDSLContext ctx = DSL.using(connectionString, "root", "asd")) {

            ctx.createTable(Tables.ACCOUNT)
                    .columns(Tables.ACCOUNT.fields())
                    .execute();

        }
    }

    @AfterAll()
    public static void cleanup() {
        mariaDb.stop();
    }

    @Test()
    public void testCreateAndReadAll() {
        try(AccountRepository sut = new AccountRepository(connectionString, "root", "asd")) {
            sut.create(new Account(UUID.randomUUID().toString(),
                    "hennerJ",
                    0.0,
                    0.0,
                    LocalDateTime.now()));

            List<Account> actual = sut.readAll();

            Assertions.assertTrue(actual.stream().anyMatch(a -> a.username().equals("hennerJ")));
        }
    }
}
