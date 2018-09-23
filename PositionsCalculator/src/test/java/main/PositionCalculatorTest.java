package main;

import model.TradePosition;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import transaction.Transaction;
import util.FileUtility;
import util.TransactionsUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static main.TestHelper.*;

/**
 * Created by Prasad on 20-09-2018.
 */
public class PositionCalculatorTest

{

    String expectedOutputPath;

    @Before
 public void setup()
 {
      expectedOutputPath=this.getClass().getResource("/Expected_end_of_day").getPath();

 }

@Test
public void test_StartOfTheDayPosition_With_Expectation() throws IOException {

    Map<String,List<TradePosition>> resultmap=  getTestInput();

    Map<String,List<TradePosition>> expectedmap=getExpectedStartOfDayPositionMap();

    assert (checkLinkedHashMapEquality(resultmap,expectedmap));

}




    @Test
    public void test_TransactionParser_With_ExpectedOutput() throws IOException
    {
        Map<String,List<TradePosition>> input=  getTestInput();
        Map<String,List<TradePosition>> output=processTransactions(input);
        Map<String,List<TradePosition>> expectedOuptut=getExpectedEndofDayPositions(expectedOutputPath);

        assert (checkLinkedHashMapEquality(output,expectedOuptut));

    }

     @Test
    public void test_PositionCalculatorMain_Process_Matches_Expectations() throws IOException {

         PositionsCalculatorMain main=new PositionsCalculatorMain();
         main.run();
         File actualOutput=new File(FileUtility.outputFile());
         File expectedOutput=new File(expectedOutputPath);
         assert ( FileUtils.contentEquals(actualOutput,expectedOutput));
   }

   @Test
    public void test_ApplyBuyTransaction()
   {
       TradePosition startPosition=new TradePosition("IBM","101","E","100000");
       Transaction buy=new Transaction(1, "IBM", "B",1000);
       TradePosition endPosition= TransactionsUtil.applyTransaction(startPosition,buy);
       assertImmutableTradepositionFields(startPosition, endPosition);
       assert(startPosition.getQuantity()+buy.getTransactionQuantity()==endPosition.getQuantity());//External Buy
       assert (startPosition.getQuantity()+endPosition.getDelta()==endPosition.getQuantity());
   }

    private void assertImmutableTradepositionFields(TradePosition startPosition, TradePosition endPosition) {
        assert (startPosition.getDelta()==0);
        assert(startPosition.getAccount()==endPosition.getAccount());
        assert (startPosition.getAccountType()==endPosition.getAccountType());
        assert(startPosition.getInstrument().equals(endPosition.getInstrument()));
    }

    @Test
    public void test_ApplySellTransaction()
    {

        TradePosition startPosition=new TradePosition("IBM","101","I","100000");
        Transaction sell=new Transaction(1, "IBM", "S",1000);
        TradePosition endPosition= TransactionsUtil.applyTransaction(startPosition,sell);

        assertImmutableTradepositionFields(startPosition,endPosition);
        assert(startPosition.getQuantity()+sell.getTransactionQuantity()==endPosition.getQuantity());//Internal account,Sell
        assert (startPosition.getQuantity()+endPosition.getDelta()==endPosition.getQuantity());

        Transaction buy=new Transaction(1, "IBM", "B",-1000);
        //TradePosition is an immutable object Startposition remains the same even after transaction has been applied to it.
        TradePosition newEndPosition= TransactionsUtil.applyTransaction(startPosition,sell);
        assertImmutableTradepositionFields(startPosition,newEndPosition);
        assert(startPosition.getQuantity()-buy.getTransactionQuantity()==newEndPosition.getQuantity());//Internal account,Buy
        assert (startPosition.getQuantity()+newEndPosition.getDelta()==newEndPosition.getQuantity());




    }


}
