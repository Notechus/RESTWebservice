package cs.uni.tradeapp.webservice.marketlistener;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Hashtable;
import java.util.Map;

/**
 * Created by Notechus on 06/08/2016.
 */
@Component
public class MarketListener
{
	private final Logger log = LoggerFactory.getLogger(getClass());
	private static final String PATH = "/trade-application/prices";

	@Autowired
	private CuratorFramework curator;

	@Autowired
	private TreeCache priceCache;

	@Bean(initMethod = "start", destroyMethod = "close")
	public TreeCache createCache() throws Exception
	{
		TreeCache t = new TreeCache(curator, PATH);
		t.getListenable().addListener(new PriceCacheListener());
		return t;
	}

	public Hashtable<String, Double> getLatestPrices()
	{
		Hashtable<String, Double> latestPrices = new Hashtable<>();
		Map<String, ChildData> children = priceCache.getCurrentChildren(PATH);
		for (String key : children.keySet())
		{
			log.info("key: {} has {}", key, new String(children.get(key).getData()));
		}
		return latestPrices;
	}
}
