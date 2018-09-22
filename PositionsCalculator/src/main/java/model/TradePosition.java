package model;

import java.util.Objects;

/**
 * Created by Prasad on 19-09-2018.
 */
final public class TradePosition {

    private final String instrument;
    private   final int account;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TradePosition that = (TradePosition) o;
        return account == that.account &&
                quantity == that.quantity &&
                delta == that.delta &&
                Objects.equals(instrument, that.instrument) &&
                accountType == that.accountType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(instrument, account, accountType, quantity, delta);
    }

    public AccountType getAccountType() {
        return accountType;
    }

    private  final AccountType accountType;

    public long getQuantity() {
        return quantity;
    }

    private final long quantity;

    public long getDelta() {
        return delta;
    }

    final long delta;

    public TradePosition(String instrument, String account, String accountType, String quantity) {

        this(instrument,account,accountType,quantity,0);
    }


    public TradePosition(String instrument, String account, String accountType, String quantity,long delta) {
        this.instrument = instrument;
        this.account = Integer.valueOf(account.trim());
        this.accountType = AccountType.accountType(accountType.trim());
        this.quantity = Integer.valueOf(quantity.trim());
        this.delta=delta;
    }


//copy constructor
    public TradePosition(TradePosition t,final long quantity,final long delta) {
        this.instrument = t.getInstrument();
        this.account =t.account;
        this.accountType = t.accountType;
        this.quantity = quantity;
        this.delta=delta;
    }


    public String getInstrument() {
        return instrument;
    }


    @Override
    public String toString() {
        return instrument + "," +account +"," + accountType +"," + quantity +","+ delta ;
    }


}
