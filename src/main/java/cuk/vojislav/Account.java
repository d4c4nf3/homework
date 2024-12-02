package cuk.vojislav;

import java.math.BigDecimal;

/**
 * For simplicity's sake, creating account with balance. In production system, account, balance and transactions should
 * be stored as a ledger.
 */
public class Account {

    private long id;
    private String name;
    /**
     * Assuming all accounts are using the same currency in order to simplify solution. Otherwise, balance should also
     * have currency and exchange rates should be considered.
     */
    private BigDecimal balance;

    public Account(long id, String name, BigDecimal balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

}
