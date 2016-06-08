package cs.uni.tradeapp.webservice.appconfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by Notechus on 29/05/16.
 */
@SpringBootApplication
@ComponentScan("cs.uni.tradeapp.webservice")
public class ServerApplication
{
	public static void main(String[] args)
	{
		SpringApplication.run(ServerApplication.class, args);
	}
}
