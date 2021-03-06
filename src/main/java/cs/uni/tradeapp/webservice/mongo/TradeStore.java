package cs.uni.tradeapp.webservice.mongo;

import cs.uni.tradeapp.utils.data.Option;
import cs.uni.tradeapp.utils.data.OptionTrade;
import cs.uni.tradeapp.utils.data.RiskCalculationTaskResult;
import cs.uni.tradeapp.webservice.mongo.DBController.*;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Date;
import java.util.Hashtable;

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

	public Hashtable<Option, RiskCalculationTaskResult> hedgedOptions;

	public TradeStore(String connectionString, String database)
	{
		this.hedgedOptions = new Hashtable<>();
		this.mongoConnector = new MongoConnector(connectionString, database);
		this.traderController = new DBTraderController(mongoConnector.getDb());
		this.positionController = new DBPositionController(mongoConnector.getDb());
		this.optionController = new DBOptionController(mongoConnector.getDb());
		this.tradeController = new DBTradeController(mongoConnector.getDb());
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

	public void executeTrades(Hashtable<String, Double> prices) throws ParseException
	{
		Option[] options = optionController.getOptionsForMaturity(new Date());
		log.info("Found {} trades to execute.", options.length);
		if (options.length == 0) return;
		log.info("Executing trades");
		OptionTrade[] trades = tradeController.getOptionTradesForIDs(options);
		for (OptionTrade t : trades)
		{
			String type = "";
			double strike = 0.0;
			double marketprice = prices.get(t.getUnderlying());
			for (Option op : options)
			{
				if (op.getId().equals(t.getOptionId()))
				{
					type = op.getTradeType();
					strike = op.getStrike();
					break;
				}
			}
			if ((type.equals("PUT") && strike > marketprice) || (type.equals("CALL") && strike < marketprice))
			{
				double value = (Math.abs(strike - marketprice) * t.getQuantity());
				traderController.updateTraderPV(t.getTrader(), value);
			}
			tradeController.deleteOptionTrade(new ObjectId(t.getId()));
			positionController.updateQuantity(t.getOptionId(), -t.getQuantity());
		}
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
