package main;

import helpers.PositionsCalculatorHelper;
import model.TradePosition;
import org.apache.log4j.Logger;
import transaction.TransactionsManager;
import util.FileUtility;

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
            new PositionsCalculatorMain().run();
        } catch (IOException e) {
            logger.error("Could not finish transaction.Process terminated abruptly.", e);
        }
    }


    public Map<String, List<TradePosition>> run() throws IOException {
        Map<String, List<TradePosition>> endOfDayPositions = null;
        try {
            endOfDayPositions = processTransactions(getInputPositions());
            writeOutputToFile(endOfDayPositions);
            printQueryResults(endOfDayPositions);
        }
        catch (IOException e) {
            logger.error("probelm with parsing input/output from file", e);
            throw e;
        }
        return endOfDayPositions;
    }

    private Map<String, List<TradePosition>> getInputPositions() throws IOException {
        return helper.getStartofDayPositions(FileUtility.getInputPath());
    }

    private void printQueryResults(Map<String, List<TradePosition>> updatedEndOfDayPositions) {
        helper.printMaxVolumeTransactionInstruments(updatedEndOfDayPositions);
        helper.getMinVolumeTransactionInstrument(updatedEndOfDayPositions);
    }

    private Map<String, List<TradePosition>> processTransactions(Map<String, List<TradePosition>> startOfTheDayPositions) throws IOException {
        return TransactionsManager.processTransactions(startOfTheDayPositions, FileUtility.transactionsFile());
    }

    private boolean writeOutputToFile(Map<String, List<TradePosition>> updatedEndOfDayPositions) throws IOException {
        return helper.writeEndOfDayPositionsToFile(updatedEndOfDayPositions, FileUtility.outputFile());
    }


}
