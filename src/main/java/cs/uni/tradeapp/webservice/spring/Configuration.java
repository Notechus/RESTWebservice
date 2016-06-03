package cs.uni.tradeapp.webservice.spring;

import cs.uni.tradeapp.utils.spring.RestServiceDetails;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/**
 * Created by Notechus on 28/05/2016.
 */


@org.springframework.context.annotation.Configuration
public class Configuration
{
	private static final String REST_VERSION = "1.0";
	public static final String SERVICE_NAME = "webservice";
	public static final String SERVICE_PATH = "/trade-application/services";

	@Value("${server.port:8080}")
	private Integer port;

	@Value("${server.host:localhost}")
	private String host;

	@Autowired
	private CuratorFramework curatorFramework;

	@Autowired
	private ServiceDiscovery<RestServiceDetails> serviceDiscovery;

	@Autowired
	private ServiceInstance<RestServiceDetails> serviceInstance;

	@Bean(initMethod = "start", destroyMethod = "close")
	public CuratorFramework createCuratorFramework()
	{
		return CuratorFrameworkFactory.newClient("localhost:2181", new ExponentialBackoffRetry(1000, 3));
	}

	@Bean()
	public ServiceInstance initServiceInstance() throws Exception
	{
		return ServiceInstance.builder()
				.uriSpec(new UriSpec("{scheme}://{address}:{port}/{name}"))
				.address(host)
				.port(port)
				.name(SERVICE_NAME)
				.payload(new RestServiceDetails(REST_VERSION))
				.build();
	}

	@Bean(initMethod = "start", destroyMethod = "close")
	public ServiceDiscovery<RestServiceDetails> serviceDiscovery() throws Exception
	{
		return ServiceDiscoveryBuilder.builder(RestServiceDetails.class)
				.basePath(SERVICE_PATH)
				.client(curatorFramework)
				.thisInstance(serviceInstance).build();
	}
}
