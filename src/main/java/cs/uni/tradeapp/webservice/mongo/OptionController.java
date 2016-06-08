package cs.uni.tradeapp.webservice.mongo;

import cs.uni.tradeapp.utils.data.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

/**
 * Created by Notechus on 06/02/2016.
 */
@RestController
public class OptionController
{
	private final Logger log = LoggerFactory.getLogger(getClass());
	private static final String PATH = "/api/option";


	@Autowired
	private MongoConnector mongo;



	@CrossOrigin
	@RequestMapping(path = PATH, method = RequestMethod.GET)
	public Option[] get(@RequestParam(value = "trader") String trader) throws Exception
	{
		Option[] res = mongo.getOptions(trader);
		log.info("returning " + res);
		return res;
	}

	@CrossOrigin
	@RequestMapping(path = PATH, method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void post(@RequestBody Option option) throws ParseException
	{
		log.info("POST " + option.getUnderlying() + " , " + option.getNotional() + " , " + option.getMaturity() + " , " + option.getDirection() + " , " + option.getStrike() + " , " + option.getTrader());
		mongo.addOption(option);
	}
}