package cs.uni.tradeapp.webservice.appconfig;

import cs.uni.tradeapp.utils.spring.RestServiceDetails;
import cs.uni.tradeapp.webservice.mongo.MongoConnector;
import cs.uni.tradeapp.webservice.mongo.TradeStore;
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
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Notechus on 28/05/2016.
 */


@Configuration
@EnableScheduling
public class AppConfiguration
{
	private static final String REST_VERSION = "1.0";
	public static final String SERVICE_NAME = "webservice";
	public static final String SERVICE_PATH = "/trade-application/services";
	private static final String connectionString = "mongodb://localhost:27017,localhost:37017,localhost:47017";
	private static final String database = "TradeStore";

	@Value("${server.port:9000}")
	private Integer port;

	@Value("${server.host:localhost}")
	private String host;

	@Value("${server.threadpool:4}")
	private int threadPool;

	@Autowired
	private CuratorFramework curatorFramework;

	@Autowired
	private ServiceDiscovery<RestServiceDetails> serviceDiscovery;

	@Autowired
	private ServiceInstance<RestServiceDetails> serviceInstance;

	@Bean(destroyMethod = "close")
	public TradeStore createMongoConnector()
	{
		return new TradeStore(connectionString, database);
	}

	@Bean()
	public Executor taskScheduler()
	{
		return Executors.newScheduledThreadPool(threadPool);
	}

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
