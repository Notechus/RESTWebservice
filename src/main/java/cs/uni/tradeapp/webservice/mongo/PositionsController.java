package cs.uni.tradeapp.webservice.mongo;

import cs.uni.tradeapp.utils.data.StockPosition;
import cs.uni.tradeapp.utils.data.TradePosition;
import cs.uni.tradeapp.webservice.mongo.DBController.DBPositionController;
import cs.uni.tradeapp.webservice.mongo.DBController.DBTraderController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Notechus on 06/09/2016.
 */
@RestController
public class PositionsController
{
	private final Logger log = LoggerFactory.getLogger(getClass());
	private static final String OPTION_PATH = "/api/position/trade";
	private static final String STOCK_PATH = "/api/position/stock";

	@Autowired
	private TradeStore tradeStore;

	@CrossOrigin
	@RequestMapping(path = OPTION_PATH, method = RequestMethod.GET)
	public TradePosition[] getPositions(@RequestParam(value = "trader") String trader)
	{
		DBTraderController traderController = (DBTraderController) tradeStore.getController(TradeStore.Context.TRADER);
		DBPositionController positionController = (DBPositionController) tradeStore.getController(TradeStore.Context.POSITION);
		String traderID = traderController.getTraderID(trader);
		TradePosition[] positions = positionController.getTradePositions(traderID);
		log.info("returning {}", positions);

		return positions;
	}

	@CrossOrigin
	@RequestMapping(path = STOCK_PATH, method = RequestMethod.GET)
	public StockPosition[] getStockPositions(@RequestParam(value = "trader") String trader)
	{
		DBTraderController traderController = (DBTraderController) tradeStore.getController(TradeStore.Context.TRADER);
		DBPositionController positionController = (DBPositionController) tradeStore.getController(TradeStore.Context.POSITION);
		String traderID = traderController.getTraderID(trader);
		StockPosition[] positions = positionController.getStockPositions(traderID);
		log.info("returning {}", positions);

		return positions;
	}
}
