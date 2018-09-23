package main;

import com.fasterxml.jackson.core.JsonParseException;
import helpers.PositionsCalculatorHelper;
import model.TradePosition;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import parser.InputParser;
import transaction.TransactionsManager;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static main.TestHelper.checkLinkedHashMapEquality;
import static main.TestHelper.getExpectedStartOfDayPositionMap;
import static main.TestHelper.getTestInput;

/**
 * Created by Prasad on 23-09-2018.
 */
public class PositionCalcultorExceptionTest {



    PositionsCalculatorHelper helper ;
    String startofDayPosWithError;
    String badInputFile;
    @Before
    public void setup()
    {
        helper =new PositionsCalculatorHelper();
        startofDayPosWithError =this.getClass().getResource("/ErroneousInput").getPath();
        badInputFile=this.getClass().getResource("/Bad_Input_Format_Error").getPath();
    }


    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void test_Bad_Input_Incorrect_AccountType() throws IOException
    {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("incorrect accounttype");
        Map<String,List<TradePosition>> resultmap=  ((InputParser< Map<String, List<TradePosition>>,IOException>)helper::getStartofDayPositions).parse(startofDayPosWithError);
        Map<String,List<TradePosition>> expectedmap=getExpectedStartOfDayPositionMap();
        assert (checkLinkedHashMapEquality(resultmap,expectedmap));

    }

    @Test
    public void test_Bad_Input_Incorrect_Format() throws IOException
    {
        expectedEx.expect(ArrayIndexOutOfBoundsException.class);
        Map<String,List<TradePosition>> resultmap=  ((InputParser< Map<String, List<TradePosition>>,IOException>)helper::getStartofDayPositions).parse(badInputFile);
        Map<String,List<TradePosition>> expectedmap=getExpectedStartOfDayPositionMap();
        assert (checkLinkedHashMapEquality(resultmap,expectedmap));

    }


    @Test
    public void test_TransactionParserJSONException() throws IOException {

        expectedEx.expect(JsonParseException.class);
        Map<String,List<TradePosition>> input=  getTestInput();
        String transactionFilePath=this.getClass().getResource("/input_transaction_with_errors").getPath();
        TransactionsManager.processTransactions(input,transactionFilePath);



    }




    @Test
    public void test_TransactionParser_Illegal_Argument_JSONException() throws IOException {

        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("illegal transaction type");
        Map<String,List<TradePosition>> input=  getTestInput();
        String transactionFilePath=this.getClass().getResource("/input_transaction_with_Illegal_Argument").getPath();
        TransactionsManager.processTransactions(input,transactionFilePath);



    }
}
