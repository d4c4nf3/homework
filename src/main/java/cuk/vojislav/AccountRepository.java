package cuk.vojislav;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AccountRepository {

    private final Map<Long, Account> accounts = new HashMap<>();

    private long sequence = 0;

    public Optional<Account> findById(Long id) {
        return Optional.ofNullable(accounts.get(id));
    }

    public Account create(String name, BigDecimal balance) {
        long id = nextId();
        accounts.put(id, new Account(id, name, balance));
        return accounts.get(id);
    }

    public Account save(Account account) {
        accounts.put(account.getId(), account);
        return account;
    }

    private long nextId() {
        synchronized (this) {
            sequence++;
            return sequence;
        }
    }
}
