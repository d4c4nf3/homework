package cuk.vojislav;

import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {
        AccountService accountService = new AccountService(new AccountRepository());

        Account account1 = accountService.createAccount("account1", new BigDecimal(100));
        Account account2 = accountService.createAccount("account2", new BigDecimal(200));

        accountService.makeDeposit(account1.getId(), new BigDecimal(50));
        accountService.transfer(account1.getId(), account2.getId(), new BigDecimal(25));
        accountService.makeWithdrawal(account2.getId(), new BigDecimal(50));
        System.out.println(accountService.balance(account2.getId()));
    }
}