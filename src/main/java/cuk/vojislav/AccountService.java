package cuk.vojislav;

import java.math.BigDecimal;

public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account createAccount(String name, BigDecimal balance) {
        return accountRepository.create(name, balance);
    }

    /**
     * Using synchronized to simplify the solution. For better concurrency, lock for each account could be used.
     * But neither of these solutions work in a distributed system. For that, we would have to use some sort of
     * distributed transactions, saga pattern or distributed locking.
     */
    public synchronized Account makeDeposit(long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId).orElseThrow();
        account.setBalance(account.getBalance().add(amount));
        return accountRepository.save(account);
    }

    public synchronized Account makeWithdrawal(long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId).orElseThrow();
        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }
        account.setBalance(account.getBalance().subtract(amount));
        return accountRepository.save(account);
    }

    public synchronized void transfer(long accountIdFrom, long accountIdTo, BigDecimal amount) {
        Account accountFrom = accountRepository.findById(accountIdFrom).orElseThrow();
        Account accountTo = accountRepository.findById(accountIdTo).orElseThrow();
        if (accountFrom.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }
        accountFrom.setBalance(accountFrom.getBalance().subtract(amount));
        accountTo.setBalance(accountTo.getBalance().add(amount));
    }

    public synchronized BigDecimal balance(long accountId) {
        return accountRepository.findById(accountId).orElseThrow().getBalance();
    }
}
