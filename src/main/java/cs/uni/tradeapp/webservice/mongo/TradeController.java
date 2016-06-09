package cs.uni.tradeapp.webservice.mongo;

import cs.uni.tradeapp.utils.data.OptionTrade;
import cs.uni.tradeapp.utils.data.StockTrade;
import cs.uni.tradeapp.utils.data.TradePosition;
import cs.uni.tradeapp.webservice.mongo.DBController.DBOptionController;
import cs.uni.tradeapp.webservice.mongo.DBController.DBPositionController;
import cs.uni.tradeapp.webservice.mongo.DBController.DBTradeController;
import cs.uni.tradeapp.webservice.mongo.DBController.DBTraderController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Notechus on 06/03/2016.
 */
@RestController
public class TradeController
{
	private final Logger log = LoggerFactory.getLogger(getClass());
	private static final String OPTION_PATH = "/api/trade/option";
	private static final String STOCK_PATH = "/api/trade/stock";

	@Autowired
	private TradeStore tradeStore;

	@CrossOrigin
	@RequestMapping(path = OPTION_PATH, method = RequestMethod.GET)
	public OptionTrade[] getOption(@RequestParam(value = "trader") String trader) throws Exception
	{
		DBTraderController traderController = (DBTraderController) tradeStore.getController(TradeStore.Context.TRADER);
		DBTradeController tradeController = (DBTradeController) tradeStore.getController(TradeStore.Context.TRADE);
		String traderID = traderController.getTraderID(trader);
		OptionTrade[] options = tradeController.getOptionTrades(traderID);
		log.info("returning " + options);
		return options;
	}

	@CrossOrigin
	@RequestMapping(path = OPTION_PATH, method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void postOption(@RequestBody OptionTrade trade)
	{
		DBTraderController traderController = (DBTraderController) tradeStore.getController(TradeStore.Context.TRADER);
		DBTradeController tradeController = (DBTradeController) tradeStore.getController(TradeStore.Context.TRADE);
		DBPositionController positionController = (DBPositionController) tradeStore.getController(TradeStore.Context.POSITION);
		String traderID = traderController.getTraderID(trade.getTrader());
		log.info("POST " + trade.getUnderlying() + " , " + trade.getId() + " , " + trade.getQuantity() + " , " + trade.getTradeType() + " , " + trade.getTrader());
		String tradeID = tradeController.addOptionTrade(trade, traderID);
		positionController.addOrUpdateTradePosition(new TradePosition(trade.getId(), trade.getOptionId(), trade
				.getUnderlying(), trade.getQuantity(), 0.0), traderID);
	}

	@CrossOrigin
	@RequestMapping(path = STOCK_PATH, method = RequestMethod.GET)
	public StockTrade[] getStock(@RequestParam(value = "trader") String trader)
	{
		DBTraderController traderController = (DBTraderController) tradeStore.getController(TradeStore.Context.TRADER);
		DBTradeController tradeController = (DBTradeController) tradeStore.getController(TradeStore.Context.TRADE);
		String traderID = traderController.getTraderID(trader);
		StockTrade[] stocks = tradeController.getStockTrades(traderID);
		log.info("returning " + stocks);
		return stocks;
	}

	@CrossOrigin
	@RequestMapping(path = STOCK_PATH, method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void postStock(@RequestBody StockTrade trade)
	{
		DBTraderController traderController = (DBTraderController) tradeStore.getController(TradeStore.Context.TRADER);
		DBTradeController tradeController = (DBTradeController) tradeStore.getController(TradeStore.Context.TRADE);
		String traderID = traderController.getTraderID(trade.getTrader());
		log.info("POST " + trade.getUnderlying() + " , " + trade.getQuantity() + " , " + trade.getTradeType() + " , " + trade.getTrader());
		tradeController.addStockTrade(trade, traderID);
	}
}