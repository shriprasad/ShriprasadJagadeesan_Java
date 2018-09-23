package helpers;

import model.TradePosition;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * Created by Prasad on 21-09-2018.
 */
public class PositionsCalculatorHelper {

    private static final java.lang.String COMMA = ",";
    static Logger logger=Logger.getLogger(PositionsCalculatorHelper.class.getName());

    /**
     *
     * @param consumer
     * @param <T>
     * @param <E>
     * @return
     */
    private  <T, E extends Exception> Consumer<T> consumerWrapper(Consumer<T> consumer) {

        return i -> {
            try {
                consumer.accept(i);
            } catch (Exception ex) {

                logger.error("Consumer wrapper caught ouptut exception",ex);
                throw ex;

            }
        };
    }

    /**
     * Write End of day positions to the output file.
     * @param endOfDayPositions
     * @param outputPath
     * @return
     * @throws IOException
     */

    public   boolean writeEndOfDayPositionsToFile(Map<String, List<TradePosition>> endOfDayPositions,String outputPath) throws IOException {

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath, false)))
        {
            writeHeader(writer);
            endOfDayPositions.entrySet()
                    .stream()
                    .flatMap(entry->entry.getValue().stream())
                    .forEach(consumerWrapper(writeOutputToFile(writer)));
        }
        catch (IOException e)
        {
            logger.error("Exception writing end of day positions output to file",e);
            throw e;

        }
        return true;

    }

    private void writeHeader(BufferedWriter writer) throws IOException {

        writer.write("Instrument,Account,AccountType,Quantity,Delta");
    }

    private static Consumer<TradePosition> writeOutputToFile(BufferedWriter writer) throws IOException {
        return (TradePosition tradePosition) -> {
            try {
                writer.newLine();
                writer.write(tradePosition.toString());

            } catch (IOException e) {
                logger.error( "problem writing to file", e);
            }

        };


    }

    /**
     * Reads from the input file and returns a map with Start of the day positions.
     *
     * @param inputPath
     * @return
     * @throws IOException
     */
    public  Map<String, List<TradePosition>> getStartofDayPositions(String inputPath) throws IOException
    {
        Map<String, List<TradePosition>> map = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(inputPath))) {
                map = reader.lines()
                        .skip(1)// skip the header of the csv
                        .map(mapInputLineToTradePosition())
                        .collect( //key-value->Instrument - list of Tradepositions.
                            Collectors.groupingBy(TradePosition::getInstrument, LinkedHashMap::new, toList())
                         );
        } catch (IOException e) {
            logger.error( "Exception occured whie reading  start of day positions from file", e);
            throw e;
        }


        return map;
    }
    private   Function<? super String, TradePosition> mapInputLineToTradePosition() {

        return (String input) -> {
            String[] tokens = input.split(COMMA);
            return new TradePosition(tokens[0], tokens[1], tokens[2], tokens[3]);
        };


    }


    public List<String> printMaxVolumeTransactionInstruments(Map<String, List<TradePosition>> map) {
        logger.info("Printing Instruments with maximum transaction volume for the day ::->");
        long maxDelta = map.entrySet()
                .stream()
                .flatMap(entry -> entry.getValue().stream())
                .max(Comparator.comparing(netTransactionVolume())).get().getDelta();

       List<String> list=map.entrySet()
                .stream()
                .flatMap(entry -> entry.getValue().stream())
                .filter(t -> Math.abs(t.getDelta()) == Math.abs(maxDelta))
                .map(TradePosition::getInstrument).distinct()
                .collect(toList());

       logger.info(list);
       return list;


    }

    private Function<TradePosition, Long> netTransactionVolume() {
        return t -> Math.abs(t.getDelta());
    }


    public List<String> getMinVolumeTransactionInstrument(Map<String, List<TradePosition>> map) {

        logger.info("Printing Instruments with minimum transaction volume for the day ::->");
        long minDelta = map.entrySet()
                .stream()
                .flatMap(entry -> entry.getValue()
                        .stream())
                .min(Comparator.comparing(netTransactionVolume())).get()
                .getDelta();

     List<String> list=   map.entrySet()
                .stream()
                .flatMap(entry -> entry.getValue().stream())
                .filter(t -> Math.abs(t.getDelta()) == Math.abs(minDelta))
                .map(TradePosition::getInstrument)
                .distinct()
                .collect(toList());

        logger.info(list);
         return list;
    }
}
