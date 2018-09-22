package transaction;

/**
 * Created by Prasad on 19-09-2018.
 */
public enum TransactionType {

    BUY,
    SELL;


private TransactionType() {
    }



 public   static TransactionType transactiontType(String type)
    {
        if(!"B".equals(type)&&!"S".equals(type))
            throw new IllegalArgumentException("illegal transaction type");

          return "B".equals(type)?BUY:SELL;

    }
}
