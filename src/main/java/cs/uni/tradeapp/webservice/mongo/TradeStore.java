package cs.uni.tradeapp.webservice.mongo;

import cs.uni.tradeapp.webservice.mongo.DBController.DBOptionController;
import cs.uni.tradeapp.webservice.mongo.DBController.DBPositionController;
import cs.uni.tradeapp.webservice.mongo.DBController.DBTradeController;
import cs.uni.tradeapp.webservice.mongo.DBController.DBTraderController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Notechus on 06/09/2016.
 */
public class TradeStore
{
	private final Logger log = LoggerFactory.getLogger(getClass());
	private MongoConnector mongoConnector;
	private DBTraderController traderController;
	private DBPositionController positionController;
	private DBOptionController optionController;
	private DBTradeController tradeController;

	
}
