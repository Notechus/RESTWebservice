package cs.uni.tradeapp.webservice.websocket;

import cs.uni.tradeapp.utils.spring.RestServiceDetails;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;

/**
 * Created by Notechus on 06/02/2016.
 */
@Controller
public class ChatController
{
	private final Logger log = LoggerFactory.getLogger(getClass());
	private int i = 0;

	@Autowired
	private SimpMessagingTemplate template;


	@Autowired
	private ServiceDiscovery<RestServiceDetails> serviceDiscovery;

	/*@MessageMapping("/api/websocket")
	@SendTo("api/market")
	public String greeting()
	{
		return "no";
	}*/
	@RequestMapping(path = "/api/websocket")
	public void greet(String greeting) throws Exception
	{
		Collection<ServiceInstance<RestServiceDetails>> instances = serviceDiscovery.queryForInstances("trade-store");
		log.info(instances.toString());
	}
}
