package main;

import helpers.PositionsCalculatorHelper;
import model.TradePosition;
import parser.InputParser;
import parser.TransactionParser;
import transaction.TransactionsManager;
import util.FileUtility;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Prasad on 23-09-2018.
 */
public class TestHelper {


   static PositionsCalculatorHelper helper ;

   static String transactionFilePath;


   static
    {
        helper =new PositionsCalculatorHelper();
        transactionFilePath="TestHelper".getClass().getResource("/Input_Transactions.txt").getPath();
    }



    public static Map<String, List<TradePosition>> getExpectedEndofDayPositions(String path) throws IOException
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
    private static Function<? super String, TradePosition> mapInputLineToTradePosition() {

        return (String input) -> {
            String[] tokens = input.split(",");
            long delta=Long.valueOf(tokens[4]);
            return  new TradePosition(tokens[0], tokens[1], tokens[2], tokens[3],delta);
        };


    }


    static Map<String, List<TradePosition>> processTransactions(Map<String, List<TradePosition>> startOfTheDayPositions) throws IOException {
        return ((TransactionParser<Map<String, List<TradePosition>>, IOException>) TransactionsManager::processTransactions).parse(startOfTheDayPositions,transactionFilePath);
    }




    static Map<String, List<TradePosition>> getTestInput() throws IOException {
        return ((InputParser< Map<String, List<TradePosition>>,IOException>) helper::getStartofDayPositions).parse(FileUtility.getInputPath());

    }



    public static LinkedHashMap<String, List<TradePosition>> getExpectedStartOfDayPositionMap() {
        ArrayList<TradePosition> list=new ArrayList<>();
        list.add(new TradePosition("IBM","101","E","100000"));
        list.add(new TradePosition("IBM","201","I","-100000"));
        list.add(new TradePosition("MSFT","101","E","5000000"));
        list.add(new TradePosition("MSFT","201","I","-5000000"));
        list.add(new TradePosition("APPL","101","E","10000"));
        list.add(new TradePosition("APPL","201","I","-10000"));
        list.add(new TradePosition("AMZN","101","E","-10000"));
        list.add(new TradePosition("AMZN","201","I","10000"));
        list.add(new TradePosition("NFLX","101","E","100000000"));
        list.add(new TradePosition("NFLX","201","I","-100000000"));
        return   list.stream().collect(Collectors.groupingBy(TradePosition::getInstrument,LinkedHashMap::new,Collectors.toList()
        ));
    }

//checks equality of two linkedhashmaps

    public  static <K, V> boolean checkLinkedHashMapEquality(Map<K, V> left, Map<K, V> right) {


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
