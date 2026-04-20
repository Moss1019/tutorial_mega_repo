package com.autotesting.service;

import com.autotesting.model.Account;
import com.autotesting.repository.AccountRepository;
import com.autotesting.repository.TradeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class AccountServiceTest {
    @Test()
    public void testReadAll() {
        AccountRepository accountRepo = Mockito.mock(AccountRepository.class);

        List<Account> testAccounts = new LinkedList<>();
        testAccounts.add(createTestAccount("hennerJ"));
        testAccounts.add(createTestAccount("hjaarD"));

        Mockito.when(accountRepo.readAll())
                .thenReturn(testAccounts);

        AccountService sut = new AccountService(accountRepo, null);

        List<Account> actual = sut.readAll();

        Assertions.assertEquals(2, actual.size());
        Assertions.assertTrue(actual.stream().anyMatch(a -> a.username().equals("hennerJ")));
        Assertions.assertTrue(actual.stream().anyMatch(a -> a.username().equals("hjaarD")));
        Assertions.assertFalse(actual.stream().anyMatch(a -> a.username().equals("hjaarD_01")));
    }

    private Account createTestAccount(String username) {
        return new Account(UUID.randomUUID().toString(),
                username,
                0.0,
                0.0,
                LocalDateTime.now());
    }
}
