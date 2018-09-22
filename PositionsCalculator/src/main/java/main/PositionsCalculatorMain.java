package main;

import helpers.PositionsCalculatorHelper;
import model.TradePosition;
import org.apache.log4j.Logger;
import parsers.InputParser;
import parsers.OutputWriter;
import parsers.TransactionParser;
import transaction.TransactionsManager;
import util.FileUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by Prasad on 20-09-2018.
 */
public class PositionsCalculatorMain {


    static Logger logger=Logger.getLogger(PositionsCalculatorMain.class.getName());
    static  PositionsCalculatorHelper helper =new PositionsCalculatorHelper();


    public static void main(String[] args) {
        try {

            Map<String, List<TradePosition>> map =   new PositionsCalculatorMain().run();
            logger.info(String.valueOf(helper.getMaxVolumeTransactionInstrument(map).get()));
            logger.info(String.valueOf( helper.getMinVolumeTransactionInstrument(map).get()));

        } catch (IOException e) {
            logger.error("Could not finish transaction.Process terminated abruptly.",e);
        }
    }



    public   Map<String, List<TradePosition>> run() throws IOException {

       Map<String, List<TradePosition>> updatedEndOfDayPositions=null;
        try
        {
            Map<String, List<TradePosition>> startOfTheDayPositions= getInput();
            updatedEndOfDayPositions= processTransactions(startOfTheDayPositions);
            writeOutputToFile(updatedEndOfDayPositions);

         } catch (IOException e) {
            logger.error("probelm with parsing input/output from file",e);
            throw e;
        }

        return updatedEndOfDayPositions;
    }

    private Map<String, List<TradePosition>> processTransactions(Map<String, List<TradePosition>> startOfTheDayPositions) throws IOException {
        return ((TransactionParser<Map<String, List<TradePosition>>, IOException>)TransactionsManager::parse).parse(startOfTheDayPositions,FileUtil.transactionsFile());
    }

    private boolean writeOutputToFile(Map<String, List<TradePosition>> updatedEndOfDayPositions) throws IOException {
        return ((OutputWriter<Map<String, List<TradePosition>>, IOException> ) helper::writeEndOfDayPositionsToFile).write(updatedEndOfDayPositions, FileUtil.outputFile());
    }


    private Map<String, List<TradePosition>>  getInput() throws IOException {
        return ((InputParser< Map<String, List<TradePosition>>,IOException>)helper::getStartofDayPositions).parse(FileUtil.getInputPath());

    }

}
