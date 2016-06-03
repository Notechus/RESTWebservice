package cs.uni.tradeapp.webservice.trades;

import cs.uni.tradeapp.utils.data.Option;
import cs.uni.tradeapp.utils.data.OptionTrade;
import cs.uni.tradeapp.utils.data.StockTrade;
import cs.uni.tradeapp.utils.data.Trade;
import cs.uni.tradeapp.utils.spring.RestServiceDetails;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;

/**
 * Created by Notechus on 29/05/2016.
 */
@RestController
public class TradeController
{
	private final Logger log = LoggerFactory.getLogger(getClass());
	private static final String OPTION_PATH = "/api/trade/option";
	private static final String STOCK_PATH = "/api/trade/stock";

	@Autowired
	private ServiceDiscovery<RestServiceDetails> serviceDiscovery;

	@Autowired
	private RestTemplate restTemplate;

	@CrossOrigin
	@RequestMapping(path = OPTION_PATH, method = RequestMethod.GET)
	public OptionTrade[] getOption(@RequestParam(value = "trader") String trader) throws Exception
	{
		Collection<ServiceInstance<RestServiceDetails>> instances = serviceDiscovery.queryForInstances("trade-store");
		String url = instances.iterator().next().buildUriSpec();
		log.info("Reaching to " + url + OPTION_PATH + "?trader=" + trader);
		OptionTrade[] options = restTemplate.getForObject(url + OPTION_PATH + "?trader=" + trader, OptionTrade[].class);
		log.info("GET " + options);
		return options;
	}

	@CrossOrigin
	@RequestMapping(path = OPTION_PATH, method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void postOption(@RequestBody OptionTrade trade) throws Exception
	{
		Collection<ServiceInstance<RestServiceDetails>> instances = serviceDiscovery.queryForInstances("trade-store");
		String url = instances.iterator().next().buildUriSpec();
		log.info("POST " + trade.getUnderlying() + " , " + trade.getId() + " , " + trade.getQuantity() + " , " + trade.getTradeType() + " , " + trade.getTrader());
		restTemplate.postForObject(url + OPTION_PATH, trade, OptionTrade.class);
	}

	@CrossOrigin
	@RequestMapping(path = STOCK_PATH, method = RequestMethod.GET)
	public StockTrade[] getStock(@RequestParam(value = "trader") String trader) throws Exception
	{
		Collection<ServiceInstance<RestServiceDetails>> instances = serviceDiscovery.queryForInstances("trade-store");
		String url = instances.iterator().next().buildUriSpec();
		log.info("Reaching to " + url + STOCK_PATH + "?trader=" + trader);
		StockTrade[] stocks = restTemplate.getForObject(url + STOCK_PATH + "?trader=" + trader, StockTrade[].class);
		log.info("GET " + stocks);
		return stocks;

	}

	@CrossOrigin
	@RequestMapping(path = STOCK_PATH, method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void postStock(@RequestBody StockTrade trade) throws Exception
	{
		Collection<ServiceInstance<RestServiceDetails>> instances = serviceDiscovery.queryForInstances("trade-store");
		String url = instances.iterator().next().buildUriSpec();
		log.info("POST " + trade.getUnderlying() + " , " + trade.getQuantity() + " , " + trade.getTradeType() + " , " + trade.getTrader());
		restTemplate.postForObject(url + STOCK_PATH, trade, StockTrade.class);

	}
}
