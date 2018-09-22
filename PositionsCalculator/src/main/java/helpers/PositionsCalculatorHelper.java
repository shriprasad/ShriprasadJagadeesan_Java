package helpers;

import model.TradePosition;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Prasad on 21-09-2018.
 */
public class PositionsCalculatorHelper {

    private static final java.lang.String COMMA = ",";
    static Logger logger=Logger.getLogger(PositionsCalculatorHelper.class.getName());

    public   boolean writeEndOfDayPositionsToFile(Map<String, List<TradePosition>> endOfDayPositions,String outputPath) throws IOException {

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath, false)))
        {
            endOfDayPositions.entrySet()
                    .stream()
                    .flatMap(entry->entry.getValue().stream())
                    .forEach(writeOutputToFile(writer));
        }
        return true;

    }

    private static Consumer<TradePosition> writeOutputToFile(BufferedWriter writer) throws IOException {
        return (TradePosition tradePosition) -> {

            try {
                writer.write(tradePosition.toString());
            } catch (IOException e) {

                logger.error( "problem reading from input file", e);
            }

        };


    }

    public  Map<String, List<TradePosition>> getStartofDayPositions(String inputPath) throws IOException
    {
        logger.info("parsing startof day positions from "+inputPath);
        Map<String, List<TradePosition>> map = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(inputPath))) {
                map = reader.lines()
                        .skip(1)// skip the header of the csv
                        .map(mapInputLineToTradePosition())
                        .collect( //key-value->Instrument - list of Tradepositions.
                            Collectors.groupingBy(TradePosition::getInstrument, LinkedHashMap::new, Collectors.toList())
                         );
        } catch (IOException e) {
            logger.error( "problem reading from input file", e);
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


    public Optional<TradePosition> getMaxVolumeTransactionInstrument(Map<String, List<TradePosition>> map) {
    return   map.entrySet()
                .stream()
                .flatMap(entry->entry.getValue().stream())
                 .max(Comparator.comparing(netTransactionVolume()));
    }

    private Function<TradePosition, Long> netTransactionVolume() {
        return t->Math.abs(t.getDelta());
    }


    public Optional<TradePosition> getMinVolumeTransactionInstrument(Map<String, List<TradePosition>> map) {
        return   map.entrySet().stream().flatMap(entry->entry.getValue().stream()).min(Comparator.comparing(netTransactionVolume()));

    }
}
