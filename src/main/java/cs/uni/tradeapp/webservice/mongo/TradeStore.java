package cs.uni.tradeapp.webservice.mongo;

import cs.uni.tradeapp.utils.data.Trader;
import cs.uni.tradeapp.webservice.mongo.DBController.*;
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

	public TradeStore(String connectionString, String database)
	{
		this.mongoConnector = new MongoConnector(connectionString, database);
		traderController = new DBTraderController(mongoConnector.getDb());
		positionController = new DBPositionController(mongoConnector.getDb());
		optionController = new DBOptionController(mongoConnector.getDb());
		tradeController = new DBTradeController(mongoConnector.getDb());
	}

	public DBController getController(Context c)
	{
		switch (c)
		{
			case TRADER:
				return this.traderController;
			case POSITION:
				return this.positionController;
			case OPTION:
				return this.optionController;
			case TRADE:
				return this.tradeController;
		}
		return null;
	}

	public void close()
	{
		mongoConnector.close();
	}

	public enum Context
	{
		TRADER,
		POSITION,
		OPTION,
		TRADE,
	}

}
