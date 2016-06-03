package cs.uni.tradeapp.webservice.websocket;

import cs.uni.tradeapp.utils.spring.RestServiceDetails;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;

/**
 * Created by Notechus on 06/02/2016.
 */
@Controller
public class ServiceController
{
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private CuratorFramework curator;

	@Autowired
	private ServiceDiscovery<RestServiceDetails> serviceDiscovery;

	@RequestMapping("api/services")
	public String event() throws Exception
	{
		Collection<ServiceInstance<RestServiceDetails>> instances = serviceDiscovery.queryForInstances("trade-store");
		log.info(instances.toString());
		return instances.toString();
	}
}
