package main;

import helpers.PositionsCalculatorHelper;
import model.TradePosition;
import org.apache.log4j.Logger;
import transaction.TransactionsManager;
import util.FileUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by Prasad on 20-09-2018.
 */
public class PositionsCalculatorMain {


    static Logger logger = Logger.getLogger(PositionsCalculatorMain.class.getName());
    static PositionsCalculatorHelper helper = new PositionsCalculatorHelper();


    public static void main(String[] args) {
        try {
            Map<String, List<TradePosition>> map = new PositionsCalculatorMain().run();
        } catch (IOException e) {
            logger.error("Could not finish transaction.Process terminated abruptly.", e);
        }
    }


    public Map<String, List<TradePosition>> run() throws IOException {
        Map<String, List<TradePosition>> EndOfDayPositions = null;
        try {
                 EndOfDayPositions = processTransactions(getInputPositions());
                 writeOutputToFile(EndOfDayPositions);
                 printQueryResults(EndOfDayPositions);
        }
        catch (IOException e) {
            logger.error("probelm with parsing input/output from file", e);
            throw e;
        }
        return EndOfDayPositions;
    }

    private Map<String, List<TradePosition>> getInputPositions() throws IOException {
        return helper.getStartofDayPositions(FileUtil.getInputPath());
    }

    private void printQueryResults(Map<String, List<TradePosition>> updatedEndOfDayPositions) {
        logger.info(helper.getMaxVolumeTransactionInstrument(updatedEndOfDayPositions));
        logger.info(helper.getMinVolumeTransactionInstrument(updatedEndOfDayPositions));
    }

    private Map<String, List<TradePosition>> processTransactions(Map<String, List<TradePosition>> startOfTheDayPositions) throws IOException {
        return TransactionsManager.parse(startOfTheDayPositions, FileUtil.transactionsFile());
    }

    private boolean writeOutputToFile(Map<String, List<TradePosition>> updatedEndOfDayPositions) throws IOException {
        return helper.writeEndOfDayPositionsToFile(updatedEndOfDayPositions, FileUtil.outputFile());
    }


}
