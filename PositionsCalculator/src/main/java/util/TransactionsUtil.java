package util;

import model.AccountType;
import model.TradePosition;
import transaction.Transaction;
import transaction.TransactionType;

/**
 * Created by Prasad on 20-09-2018.
 */
public class TransactionsUtil {



    /**
     * Takes an old TradePosition  to return new TradePosition
     *  after the transaction.
     * @param tradePosition
     * @param transaction
     * @return
     */
    public static TradePosition applyTransaction(TradePosition tradePosition, Transaction transaction) {

        long tempQuantity = tradePosition.getQuantity();
        long delta = tradePosition.getDelta();
        if (tradePosition.getAccountType()==(AccountType.E)) {
            tempQuantity += TransactionType.BUY==transaction.getType() ? transaction.getTransactionQuantity() : (-transaction.getTransactionQuantity());
        } else if (tradePosition.getAccountType()==(AccountType.I)) {
            tempQuantity += TransactionType.SELL==transaction.getType() ? transaction.getTransactionQuantity() : (-transaction.getTransactionQuantity());
        }

        long newQuantity = tempQuantity;
        delta += newQuantity - tradePosition.getQuantity();

        return new TradePosition(tradePosition, newQuantity, delta);
    }



}
