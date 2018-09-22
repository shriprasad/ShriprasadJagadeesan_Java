package main;

import helpers.PositionsCalculatorHelper;
import model.TradePosition;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import parsers.InputParser;
import parsers.OutputWriter;
import parsers.TransactionParser;
import transaction.TransactionsManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Prasad on 20-09-2018.
 */
public class PositionCalculatorTest

{
    static Logger logger=Logger.getLogger(PositionCalculatorTest.class.getName());

    PositionsCalculatorHelper helper ;
    String startofDayPosFileLoc;
    String startofDayPosWithError;
    String transactionFilePath;
    String outputPath;
    String expectedOutputPath;

    @Before
 public void setup()
 {
     helper =new PositionsCalculatorHelper();
     transactionFilePath=this.getClass().getResource("/Input_Transactions.txt").getPath();
     startofDayPosFileLoc =this.getClass().getResource("/startofDayPositionsTestInput").getPath();
     startofDayPosWithError =this.getClass().getResource("/ErroneousInput").getPath();
     outputPath=this.getClass().getResource("/testOutput.txt").getPath();
     expectedOutputPath=this.getClass().getResource("/Expected_EndOfDay_Positions").getPath();
     System.out.println(startofDayPosFileLoc);
 }

@Test
public void test_StartOfTheDayPosition_With_Expectation() throws IOException {

    Map<String,List<TradePosition>> resultmap=  getInput();

    Map<String,List<TradePosition>> expectedmap=getExpectedStartOfDayPositionMap();

    assert (checkLinkedHashMapEquality(resultmap,expectedmap));

}
@Test(expected=IllegalArgumentException.class)
public void test_Exception_StartOfTheDayPosition_With_Expectation() throws IOException
{

        Map<String,List<TradePosition>> resultmap=  ((InputParser< Map<String, List<TradePosition>>,IOException>)helper::getStartofDayPositions).parse(startofDayPosWithError);
        Map<String,List<TradePosition>> expectedmap=getExpectedStartOfDayPositionMap();
        assert (checkLinkedHashMapEquality(resultmap,expectedmap));

}


@Test
 public void test_TransactionParser_With_ExpectedOutput() throws IOException
{
        Map<String,List<TradePosition>> input=  getInput();
        Map<String,List<TradePosition>> output=processTransactions(input);
        Map<String,List<TradePosition>> expectedOuptut=getExpectedEndofDayPositions(expectedOutputPath);

    assert (checkLinkedHashMapEquality(output,expectedOuptut));

 }


    public  Map<String, List<TradePosition>> getExpectedEndofDayPositions(String path) throws IOException
    {
        Map<String, List<TradePosition>> map = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {

            map = reader.lines()
                    .skip(1)// skip the header of the csv
                    .map(mapInputLineToTradePosition())
                    .collect( //key-value->Instrument - list of Tradepositions.
                            Collectors.groupingBy(TradePosition::getInstrument, LinkedHashMap::new, Collectors.toList())
                    );
        }


        return map;
    }
    private   Function<? super String, TradePosition> mapInputLineToTradePosition() {

        return (String input) -> {
            String[] tokens = input.split(",");
            long delta=Long.valueOf(tokens[4]);
            return  new TradePosition(tokens[0], tokens[1], tokens[2], tokens[3],delta);
        };


    }


    private Map<String, List<TradePosition>> processTransactions(Map<String, List<TradePosition>> startOfTheDayPositions) throws IOException {
        return ((TransactionParser<Map<String, List<TradePosition>>, IOException>) TransactionsManager::parse).parse(startOfTheDayPositions,transactionFilePath);
    }

    private boolean writeOutputToFile(Map<String, List<TradePosition>> updatedEndOfDayPositions) throws IOException {
        return ((OutputWriter<Map<String, List<TradePosition>>, IOException>) helper::writeEndOfDayPositionsToFile).write(updatedEndOfDayPositions,outputPath);
    }


    private Map<String, List<TradePosition>>  getInput() throws IOException {
        return ((InputParser< Map<String, List<TradePosition>>,IOException>)helper::getStartofDayPositions).parse(startofDayPosFileLoc);

    }




    private boolean checkIfEndOfDayPositionsOutputInFile(Map<String, List<TradePosition>> map) {
return false;

    }

    private boolean checkIfEndOfDayPositionsAreCorrect(Map<String, List<TradePosition>> map) {
        return false;
    }





    public LinkedHashMap<String, List<TradePosition>> getExpectedStartOfDayPositionMap() {
        ArrayList<TradePosition> list=new ArrayList<>();
        list.add(new TradePosition("IBM","1001","E","10000"));
        list.add(new TradePosition("IBM","2001","I","-10000"));
        list.add(new TradePosition("MSFT","1001","E","500000"));
        list.add(new TradePosition("MSFT","2001","I","-500000"));
        list.add(new TradePosition("APPL","1001","E","1000"));
        list.add(new TradePosition("APPL","2001","I","-1000"));
        list.add(new TradePosition("AMZN","1001","E","-1000"));
        list.add(new TradePosition("AMZN","2001","I","1000"));
        list.add(new TradePosition("NFLX","1001","E","10000000"));
        list.add(new TradePosition("NFLX","2001","I","-10000000"));
     return   list.stream().collect(Collectors.groupingBy(TradePosition::getInstrument,LinkedHashMap::new,Collectors.toList()
        ));
   }

//checks equality of two linkedhashmaps

    public static <K, V> boolean checkLinkedHashMapEquality(Map<K, V> left, Map<K, V> right) {


        Iterator<Map.Entry<K, V>> leftItr = left.entrySet().iterator();
        Iterator<Map.Entry<K, V>> rightItr = right.entrySet().iterator();

        while ( leftItr.hasNext() && rightItr.hasNext()) {
            Map.Entry<K, V> leftEntry = leftItr.next();
            Map.Entry<K, V> rightEntry = rightItr.next();

            // for maps we can assume you never get null entries
            if (! leftEntry.equals(rightEntry))
                return false;
        }
        return !(leftItr.hasNext() || rightItr.hasNext());
    }




}
