package cs.uni.tradeapp.webservice.marketlistener;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Notechus on 06/08/2016.
 */
public class PriceCacheListener implements TreeCacheListener
{
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception
	{
		log.info("Fired {}", treeCacheEvent.getType());
		TreeCacheEvent.Type type = treeCacheEvent.getType();
		if (type == TreeCacheEvent.Type.NODE_UPDATED)
		{
			/*
			* 1. get trades from mongo
			* 2. assign task for pricer
			* 3. fetch result and update trader data
			*/

		}
	}
}
