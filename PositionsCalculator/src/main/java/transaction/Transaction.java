package transaction;

/**
 * Created by Prasad on 20-09-2018.
 */
public class Transaction {



  final int transactionId;
  final  String instrument;
  final TransactionType type;
  final long transactionQuantity;


    public Transaction(int transactionId, String instrument,String type, long transactionQuantity) {
        this.transactionId = transactionId;
        this.instrument = instrument;
        this.type = TransactionType.transactiontType(type);
        this.transactionQuantity = transactionQuantity;
    }



    public String getInstrument() {
        return instrument;
    }

    public TransactionType getType() {
        return type;
    }

    public long getTransactionQuantity() {
        return transactionQuantity;
    }
}
