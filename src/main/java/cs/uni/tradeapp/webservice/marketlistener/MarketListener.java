package cs.uni.tradeapp.webservice.marketlistener;

import cs.uni.tradeapp.utils.data.OptionTrade;
import cs.uni.tradeapp.utils.data.RiskCalculationTaskObject;
import cs.uni.tradeapp.utils.data.StockMessage;
import cs.uni.tradeapp.utils.zookeeper.MyDistributedQueue;
import cs.uni.tradeapp.webservice.mongo.MongoConnector;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Notechus on 06/08/2016.
 */
@Service
public class MarketListener
{
	private final Logger log = LoggerFactory.getLogger(getClass());
	private static final String URL = "/api/market/price.stock.";
	private static final String PATH = "/trade-application/prices";
	private static final String QUEUE_PATH = "/trade-application/tasks/worker1";
	private static final String USER = "SebastianPaulus";

	private MessageSendingOperations<String> messagingTemplate;
	private AtomicBoolean brokerAvailable = new AtomicBoolean();

	@Autowired
	private CuratorFramework curator;

	@Autowired
	private TreeCache priceCache;

	@Autowired
	private MyDistributedQueue taskQueue;

	@Autowired
	private MongoConnector mongoConnector;

	@Autowired
	public MarketListener(MessageSendingOperations<String> messagingTemplate)
	{
		this.messagingTemplate = messagingTemplate;
	}


	@Bean(initMethod = "createQueue")
	public MyDistributedQueue taskQueue()
	{
		return new MyDistributedQueue(curator, QUEUE_PATH);
	}

	@Bean(initMethod = "start", destroyMethod = "close")
	public TreeCache createCache() throws Exception
	{
		TreeCache t = new TreeCache(curator, PATH);
		t.getListenable().addListener((curatorFramework, treeCacheEvent) -> {
			log.info("Fired {}", treeCacheEvent.getType());
			TreeCacheEvent.Type type = treeCacheEvent.getType();
			if (type == TreeCacheEvent.Type.NODE_UPDATED)
			{
				Hashtable<String, Double> prices = getLatestPrices();
				log.info("Sending updated prices");
				sendPrices(prices);
				log.info("Fetching hedger");
				//taskQueue.put("nothing".getBytes());

			/*
			 * 1. get trades from mongo
			 * 2. assign task for pricer
			 * 3. fetch result and update trader data
			 */
			}
		});
		return t;
	}

	public void fetchTask(Hashtable<String, Double> prices)
	{
		ArrayList<RiskCalculationTaskObject> options = new ArrayList<>();
		OptionTrade[] trades = mongoConnector.getOptionTrades(USER);
	}

	public Hashtable<String, Double> getLatestPrices()
	{
		Hashtable<String, Double> latestPrices = new Hashtable<>();
		Map<String, ChildData> children = priceCache.getCurrentChildren(PATH);
		for (String key : children.keySet())
		{
			log.info("key: {} has {}", key, new String(children.get(key).getData()));
			latestPrices.put(key, Double.parseDouble(new String(children.get(key).getData())));
		}
		return latestPrices;
	}

	public void sendPrices(Hashtable<String, Double> prices)
	{
		log.info("sending prices");
		for (String key : prices.keySet())
		{
			StockMessage msg = new StockMessage();
			msg.setUnderlying(key);
			msg.setPrice(prices.get(key));
			msg.setTimestamp(LocalDateTime.now());
			this.messagingTemplate.convertAndSend(URL + msg.getUnderlying(), msg);
		}
	}
}
