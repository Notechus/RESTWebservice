package cs.uni.tradeapp.webservice.mongo;

import cs.uni.tradeapp.utils.data.OptionTrade;
import cs.uni.tradeapp.utils.data.StockTrade;
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
	private MongoConnector mongo;

	@CrossOrigin
	@RequestMapping(path = OPTION_PATH, method = RequestMethod.GET)
	public OptionTrade[] getOption(@RequestParam(value = "trader") String trader) throws Exception
	{
		OptionTrade[] options = mongo.getOptionTrades(trader);
		log.info("returning " + options);
		return options;
	}

	@CrossOrigin
	@RequestMapping(path = OPTION_PATH, method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void postOption(@RequestBody OptionTrade trade)
	{
		log.info("POST " + trade.getUnderlying() + " , " + trade.getId() + " , " + trade.getQuantity() + " , " + trade.getTradeType() + " , " + trade.getTrader());
		mongo.addOptionTrade(trade);
	}

	@CrossOrigin
	@RequestMapping(path = STOCK_PATH, method = RequestMethod.GET)
	public StockTrade[] getStock(@RequestParam(value = "trader") String trader)
	{
		StockTrade[] stocks = mongo.getStockTrades(trader);
		for (StockTrade i : stocks)
		{
			log.info(i.getUnderlying());
		}
		log.info("returning " + stocks);
		return stocks;
	}

	@CrossOrigin
	@RequestMapping(path = STOCK_PATH, method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void postStock(@RequestBody StockTrade trade)
	{
		log.info("POST " + trade.getUnderlying() + " , " + trade.getQuantity() + " , " + trade.getTradeType() + " , " + trade.getTrader());
		mongo.addStockTrade(trade);
	}
}