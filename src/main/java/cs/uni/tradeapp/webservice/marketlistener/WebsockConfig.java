package cs.uni.tradeapp.webservice.marketlistener;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

/**
 * Created by Notechus on 06/05/2016.
 */
@Configuration
@EnableScheduling
@EnableWebSocketMessageBroker
public class WebsockConfig extends AbstractWebSocketMessageBrokerConfigurer
{
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry)
	{
		registry.addEndpoint("/api/market").setAllowedOrigins("*").withSockJS();
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry)
	{
		registry.enableSimpleBroker("/api/market");
		registry.setApplicationDestinationPrefixes("/app");
	}
}
