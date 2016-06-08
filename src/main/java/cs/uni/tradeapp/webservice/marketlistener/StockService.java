package cs.uni.tradeapp.webservice.marketlistener;

import cs.uni.tradeapp.utils.data.StockMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.messaging.simp.broker.BrokerAvailabilityEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Notechus on 06/05/2016.
 */
@Service
public class StockService implements ApplicationListener<BrokerAvailabilityEvent>
{
	private Logger log = LoggerFactory.getLogger(getClass());
	private static final String URL = "/api/market/price.stock.";
	private final MessageSendingOperations<String> messagingTemplate;
	private AtomicBoolean brokerAvailable = new AtomicBoolean();

	@Autowired
	public StockService(MessageSendingOperations<String> messagingTemplate)
	{
		this.messagingTemplate = messagingTemplate;
	}

	@Override
	public void onApplicationEvent(BrokerAvailabilityEvent event)
	{
		this.brokerAvailable.set(event.isBrokerAvailable());
	}

	@Scheduled(fixedDelay = 1000)
	public void sendPrices()
	{
		StockMessage msg = new StockMessage("GOOG", 6.0, 5.0, LocalDateTime.now());
		this.messagingTemplate.convertAndSend(URL + "GOOG", msg);
	}
}
