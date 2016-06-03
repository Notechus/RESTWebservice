package cs.uni.tradeapp.webservice.trades;

import cs.uni.tradeapp.utils.data.Option;
import cs.uni.tradeapp.utils.spring.RestServiceDetails;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;

/**
 * Created by Notechus on 05/29/2016.
 */
@RestController
public class OptionController
{
	private final Logger log = LoggerFactory.getLogger(getClass());
	private static final String PATH = "/api/option";

	@Autowired
	private ServiceDiscovery<RestServiceDetails> serviceDiscovery;

	@Autowired
	private RestTemplate restTemplate;

	@Bean()
	public RestTemplate createRest()
	{
		return new RestTemplate();
	}

	@CrossOrigin
	@RequestMapping(path = PATH, method = RequestMethod.GET)
	public Option[] get(@RequestParam(value = "trader") String trader) throws Exception
	{
		Collection<ServiceInstance<RestServiceDetails>> instances = serviceDiscovery.queryForInstances("trade-store");
		String url = instances.iterator().next().buildUriSpec();
		log.info("Reaching to " + url + PATH + "?trader=" + trader);
		Option[] options = restTemplate.getForObject(url + PATH + "?trader=" + trader, Option[].class);
		log.info("GET " + options);
		return options;
	}

	@CrossOrigin
	@RequestMapping(path = PATH, method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void post(@RequestBody Option option) throws Exception
	{
		Collection<ServiceInstance<RestServiceDetails>> instances = serviceDiscovery.queryForInstances("trade-store");
		String url = instances.iterator().next().buildUriSpec();
		log.info("POST " + option.getUnderlying() + " , " + option.getNotional() + " , " + option.getMaturity() + " , " + option.getDirection() + " , " + option.getStrike() + " , " + option.getTrader());
		restTemplate.postForObject(url + PATH, option, Option.class);
	}
}
