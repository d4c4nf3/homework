package cuk.vojislav;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class AccountServiceTest {

    private final AccountService accountService = new AccountService(new AccountRepository());

    @Test
    void shouldCreateAccountsWithNameAndBalance() {

        Account account1 = accountService.createAccount("account1", new BigDecimal(100));
        assertThat(account1.getName()).isEqualTo("account1");
        assertThat(account1.getBalance()).isEqualTo(new BigDecimal(100));
    }

    /**
     * If we want to run tests concurrently, this test could fail because other test might create new account while this
     * test is being executed. For that, we need to synchronize account creation.
     */
    @Test
    void shouldCreateAccountsWithAutoIncrementedId() {
        Account account1 = accountService.createAccount("account1", new BigDecimal(100));

        Account account2 = accountService.createAccount("account2", new BigDecimal(200));
        assertThat(account2.getId()).isEqualTo(account1.getId() + 1);
    }

    @Test
    void shouldMakeDeposit() {
        BigDecimal startingAmount = new BigDecimal(100);
        BigDecimal deposit = new BigDecimal(50);

        Account account = accountService.createAccount("account1", startingAmount);
        accountService.makeDeposit(account.getId(), deposit);
        assertThat(account.getBalance()).isEqualTo(startingAmount.add(deposit));
    }

    @Test
    void whenDepositAmountIsNegativeShouldThrowException() {
        BigDecimal startingAmount = new BigDecimal(100);
        BigDecimal deposit = new BigDecimal(-50);

        Account account = accountService.createAccount("account1", startingAmount);
        assertThatThrownBy(() -> accountService.makeDeposit(account.getId(), deposit))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot make negative deposit");
    }

    @Test
    void shouldMakeWithdrawal() {
        BigDecimal startingAmount = new BigDecimal(100);
        BigDecimal withdrawal = new BigDecimal(50);

        Account account = accountService.createAccount("account1", startingAmount);
        accountService.makeWithdrawal(account.getId(), withdrawal);
        assertThat(account.getBalance()).isEqualTo(startingAmount.subtract(withdrawal));
    }

    @Test
    void overdraftShouldNotBePossible() {
        BigDecimal startingAmount = new BigDecimal(100);
        BigDecimal withdrawal = new BigDecimal(200);

        Account account = accountService.createAccount("account1", startingAmount);
        assertThatThrownBy(() -> accountService.makeWithdrawal(account.getId(), withdrawal))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Insufficient funds");
    }

    @Test
    void whenWithdrawalAmountIsNegativeShouldThrowException() {
        BigDecimal startingAmount = new BigDecimal(100);
        BigDecimal withdrawal = new BigDecimal(-50);

        Account account = accountService.createAccount("account1", startingAmount);
        assertThatThrownBy(() -> accountService.makeWithdrawal(account.getId(), withdrawal))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot make negative withdrawal");
    }

    @Test
    void shouldTransfer() {
        BigDecimal startingAmount1 = new BigDecimal(100);
        BigDecimal startingAmount2 = new BigDecimal(200);
        BigDecimal amount = new BigDecimal(50);

        Account accountFrom = accountService.createAccount("account1", startingAmount1);
        Account accountTo = accountService.createAccount("account2", startingAmount2);
        accountService.transfer(accountFrom.getId(), accountTo.getId(), amount);
        assertThat(accountFrom.getBalance()).isEqualTo(startingAmount1.subtract(amount));
        assertThat(accountTo.getBalance()).isEqualTo(startingAmount2.add(amount));
    }

    @Test
    void transferShouldFailOnOverdraft() {
        BigDecimal startingAmount1 = new BigDecimal(100);
        BigDecimal amount = new BigDecimal(200);

        Account accountFrom = accountService.createAccount("account1", startingAmount1);
        Account accountTo = accountService.createAccount("account2", new BigDecimal(0));
        assertThatThrownBy(() -> accountService.transfer(accountFrom.getId(), accountTo.getId(), amount))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Insufficient funds");
    }

    @Test
    void whenTransferAmountIsNegativeShouldThrowException() {
        BigDecimal startingAmount1 = new BigDecimal(100);
        BigDecimal amount = new BigDecimal(-50);

        Account accountFrom = accountService.createAccount("account1", startingAmount1);
        Account accountTo = accountService.createAccount("account2", new BigDecimal(0));
        assertThatThrownBy(() -> accountService.transfer(accountFrom.getId(), accountTo.getId(), amount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot transfer negative amount");
    }
}