package transaction;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import model.TradePosition;
import org.apache.log4j.Logger;
import util.TransactionsUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * Created by Prasad on 19-09-2018.
 */
public class TransactionsManager {

    static Logger logger=Logger.getLogger(TransactionsManager.class.getName());


    /**
     * Lazy read each transaction from  the input  transaction file with JSON Jackson streaming API.
     * For each transaction calculate and update the the start of the day positions.
     * Return End of the day positions in the same order .
     * @param map
     * @return
     * @throws IOException
     */

    public static Map<String, List<TradePosition>> processTransactions(Map<String, List<TradePosition>> map, String inputPath) throws IOException {

        JsonFactory jfactory = new JsonFactory();

        try (JsonParser jParser = jfactory.createParser(new FileInputStream(inputPath))) {

            while (jParser.nextToken() != JsonToken.END_ARRAY) {

              Transaction t= parseInputTransaction(jParser);
              updatePositionBasedOnTransaction(map, t);
            }
        }
        catch (JsonParseException e) {
            logger.error("Problem parsing the JSON transaction input File",e);
            throw e;
        }
        catch (IOException e)
        {  logger.error("Problem with File Input output ",e);
            throw e;
        }
        return map;
    }

    /**
     * Reads One JSON Object corresponding to a single transaction from the JSON file and
     * returns a POJO
     * @param jParser
     * @return Transaction
     * @throws IOException
     */
    private static Transaction parseInputTransaction(JsonParser jParser) throws IOException {
        String instrument=null;
        String transactionType=null;
        long transactionQuantity = 0;
        int transactionId = 0;
        while (jParser.nextToken() != JsonToken.END_OBJECT) {

            if (jParser.currentToken() == JsonToken.START_OBJECT)
                continue;

            String fieldname = jParser.getCurrentName();

            switch (fieldname) {
                case "TransactionId":
                    transactionId = jParser.nextIntValue(0);
                    break;

                case "Instrument":
                    instrument = jParser.nextTextValue();
                    break;

                case "TransactionType":
                    transactionType = jParser.nextTextValue();
                    break;
                case "TransactionQuantity":
                    transactionQuantity = jParser.nextLongValue(0);
                    break;
            }
        }

        Transaction t=new Transaction(transactionId,instrument,transactionType,transactionQuantity);
        return  t;
    }

    private static Map<String, List<TradePosition>> updatePositionBasedOnTransaction(Map<String, List<TradePosition>> map, Transaction t) {

        List<TradePosition> newTradePositions = map.get(t.getInstrument())
                .stream()
                .map(tradePosition -> TransactionsUtil.applyTransaction(tradePosition,t))
                .collect(toList());

        map.put(t.getInstrument(), newTradePositions);//override the old trade poisitions with new trade position arrived at  after transaction.

        return map;
    }


}
