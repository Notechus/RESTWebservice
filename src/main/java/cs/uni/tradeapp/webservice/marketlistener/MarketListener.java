package cs.uni.tradeapp.webservice.marketlistener;

import com.google.gson.reflect.TypeToken;
import cs.uni.tradeapp.utils.data.Option;
import cs.uni.tradeapp.utils.data.RiskCalculationTaskObject;
import cs.uni.tradeapp.utils.data.RiskCalculationTaskResult;
import cs.uni.tradeapp.utils.data.StockMessage;
import cs.uni.tradeapp.utils.json.JsonBuilder;
import cs.uni.tradeapp.utils.zookeeper.MyDistributedQueue;
import cs.uni.tradeapp.webservice.mongo.DBController.DBOptionController;
import cs.uni.tradeapp.webservice.mongo.TradeStore;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

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
	private static final String RESULT_PATH = "/trade-application/results/worker1";

	private JsonBuilder jsonBuilder = new JsonBuilder();
	private MessageSendingOperations<String> messagingTemplate;

	@Autowired
	private CuratorFramework curator;

	@Autowired
	private TreeCache priceCache;

	@Autowired
	@Qualifier("taskQueue")
	private MyDistributedQueue taskQueue;

	@Autowired
	@Qualifier("resultQueue")
	MyDistributedQueue resultQueue;

	@Autowired
	private TradeStore tradeStore;

	@Autowired
	public MarketListener(MessageSendingOperations<String> messagingTemplate)
	{
		this.messagingTemplate = messagingTemplate;
		this.taskQueue = new MyDistributedQueue(curator, QUEUE_PATH);
		this.resultQueue = new MyDistributedQueue(curator, RESULT_PATH);
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
				String name = treeCacheEvent.getData().getPath().substring(PATH.length() + 1);
				double newPrice = Double.parseDouble(new String(treeCacheEvent.getData().getData()));
				log.info("Changed data {} on {}", newPrice, name);
				log.info("Sending updated prices");
				sendPrices(prices);
				log.info("Fetching hedger");
				price(name, newPrice);

			}
		});
		return t;
	}

	@Scheduled(fixedRate = 1000)
	public void execute() throws ParseException
	{
		tradeStore.executeTrades(getLatestPrices());
	}

	public void price(String ticker, double newPrice) throws Exception
	{
		Type type = new TypeToken<Hashtable<RiskCalculationTaskObject, RiskCalculationTaskResult>>()
		{
		}.getType();
		Hashtable<RiskCalculationTaskObject, RiskCalculationTaskResult> task = new Hashtable<>();
		DBOptionController optionController = (DBOptionController) tradeStore.getController(TradeStore.Context.OPTION);
		Option[] options = optionController.getOptionsForTicker(ticker);
		if (options.length > 0)
		{
			for (Option o : options)
			{
				log.info("Pricing {}", o.getId());
				RiskCalculationTaskObject taskObject = new RiskCalculationTaskObject();
				taskObject.setNewPrice(newPrice);
				taskObject.setOption(o);
				task.put(taskObject, new RiskCalculationTaskResult());
			}
			taskQueue.put(jsonBuilder.serialize(task, type).getBytes());
		}
	}

	@Scheduled(fixedRate = 200)
	public void fetchResult() throws Exception
	{
		Type type = new TypeToken<Hashtable<RiskCalculationTaskObject, RiskCalculationTaskResult>>()
		{
		}.getType();
		try
		{
			Hashtable<RiskCalculationTaskObject, RiskCalculationTaskResult> result =
					(Hashtable<RiskCalculationTaskObject, RiskCalculationTaskResult>)
							jsonBuilder.deserialize(new String(resultQueue.remove()), type);
			log.info("Fetched results");
		} catch (Exception e)
		{

		}

	}

	public void hedge(Hashtable<String, Double> prices)
	{
		ArrayList<RiskCalculationTaskObject> options = new ArrayList<>();

	}

	public Hashtable<String, Double> getLatestPrices()
	{
		Hashtable<String, Double> latestPrices = new Hashtable<>();
		Map<String, ChildData> children = priceCache.getCurrentChildren(PATH);
		for (String key : children.keySet())
		{
			//log.info("key: {} has {}", key, new String(children.get(key).getData()));
			latestPrices.put(key, Double.parseDouble(new String(children.get(key).getData())));
		}
		return latestPrices;
	}

	public void sendPrices(Hashtable<String, Double> prices)
	{
		log.info("Sending prices");
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
