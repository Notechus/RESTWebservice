package cs.uni.tradeapp.webservice.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

/**
 * Created by Notechus on 06/02/2016.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WSConfig extends AbstractWebSocketMessageBrokerConfigurer
{

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config)
	{
		config.enableSimpleBroker("/api/market");
		config.setApplicationDestinationPrefixes("/app");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry)
	{
		stompEndpointRegistry.addEndpoint("api/websocket");
	}
}
