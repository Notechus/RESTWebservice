package cs.uni.tradeapp.webservice.mongo;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import cs.uni.tradeapp.utils.data.*;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Created by Notechus on 05/25/2016.
 */
public class MongoConnector
{
	private final Logger log = LoggerFactory.getLogger(getClass());
	private MongoClient mongoClient;
	private MongoDatabase db;
	private final String ISO_DATE_FORMAT = "YYYY-MM-DD'T'HH:mm:ssZ";
	private SimpleDateFormat simpleDateFormat;

	public MongoConnector(String connectionString, String dbname)
	{
		MongoClientURI con = new MongoClientURI(connectionString);
		mongoClient = new MongoClient(con);
		simpleDateFormat = new SimpleDateFormat(ISO_DATE_FORMAT);
		db = mongoClient.getDatabase(dbname);
	}

	/* options part */
	public Option[] getOptions(String trader)
	{
		BasicDBObject query = new BasicDBObject();
		query.put("Trader", trader);
		MongoCollection<Document> options = db.getCollection("Options");
		MongoCursor<Document> cursor = options.find(query).iterator();
		ArrayList<Option> tmp = new ArrayList<>();
		while (cursor.hasNext())
		{
			Document d = cursor.next();
			Option o = new Option();
			o.setId(d.getObjectId("_id").toString());
			o.setMaturity(simpleDateFormat.format(d.getDate("Maturity")));
			o.setUnderlying(d.getString("Underlying"));
			o.setDirection(d.getString("Direction"));
			o.setStrike(d.getDouble("Strike"));
			o.setTrader(d.getString("Trader"));
			o.setNotional(d.getDouble("Notional"));
			tmp.add(o);
		}
		Option[] res = new Option[tmp.size()];
		tmp.toArray(res);
		cursor.close();
		return res;
	}

	public void addOption(Option o) throws ParseException, DuplicateKeyException
	{
		Document doc = new Document("Underlying", o.getUnderlying())
				.append("Direction", o.getDirection())
				.append("Maturity", simpleDateFormat.parse(o.getMaturity()))
				.append("Strike", o.getStrike())
				.append("Trader", o.getTrader())
				.append("Notional", o.getNotional());

		db.getCollection("Options").insertOne(doc);
	}

	public OptionTrade[] getOptionTrades(String trader)
	{
		BasicDBObject query = new BasicDBObject();
		query.put("Trader", trader);
		MongoCollection<Document> options = db.getCollection("OptionTrade");
		MongoCursor<Document> cursor = options.find(query).iterator();
		ArrayList<OptionTrade> tmp = new ArrayList<>();
		while (cursor.hasNext())
		{
			Document d = cursor.next();
			OptionTrade o = new OptionTrade();
			o.setId(d.getObjectId("_id").toString());
			o.setQuantity(d.getInteger("Quantity"));
			o.setUnderlying(d.getString("Underlying"));
			o.setTrader(d.getString("Trader"));
			tmp.add(o);
		}
		OptionTrade[] res = new OptionTrade[tmp.size()];
		tmp.toArray(res);
		cursor.close();
		return res;
	}

	public void addOptionTrade(OptionTrade o)
	{
		Document doc = new Document("Underlying", o.getUnderlying())
				.append("Id", o.getId())
				.append("Quantity", o.getQuantity())
				.append("Trader", o.getTrader());

		db.getCollection("OptionTrade").insertOne(doc);
	}

	public void deleteOptionTrade(ObjectId id)
	{
		Document d = new Document("_id", id);
		db.getCollection("OptionTrade").deleteOne(d);
	}

	public StockTrade[] getStockTrades(String trader)
	{
		BasicDBObject query = new BasicDBObject();
		query.put("Trader", trader);
		MongoCollection<Document> stocks = db.getCollection("StockTrade");
		MongoCursor<Document> cursor = stocks.find(query).iterator();
		ArrayList<StockTrade> tmp = new ArrayList<>();
		while (cursor.hasNext())
		{
			Document d = cursor.next();
			StockTrade s = new StockTrade();
			s.setQuantity(d.getInteger("Quantity"));
			s.setUnderlying(d.getString("Underlying"));
			s.setTrader(d.getString("Trader"));
			tmp.add(s);
		}
		StockTrade[] res = new StockTrade[tmp.size()];
		tmp.toArray(res);
		cursor.close();
		return res;
	}

	public void addStockTrade(StockTrade s)
	{
		Document doc = new Document("Underlying", s.getUnderlying())
				.append("Quantity", s.getQuantity())
				.append("Trader", s.getTrader());

		db.getCollection("StockTrade").insertOne(doc);
		// now we update PV
		double marketvalue = 15.8; // temp
		updateTraderPV(s.getTrader(), marketvalue * s.getQuantity());
	}

	@Scheduled(fixedRate = 1000)
	public void execute()
	{
		log.info("Executing trades");
		BasicDBObject query = new BasicDBObject();
		query.put("Maturity", LocalDateTime.now());
		MongoCollection<Document> options = db.getCollection("OptionTrade");
		long count = options.count();
		log.info("Found {} trades to execute.", count);
		if (count == 0) return;

		MongoCursor<Document> cursor = options.find(query).iterator();
		while (cursor.hasNext())
		{
			Document d = cursor.next();
			String type = d.getString("Direction");
			double strike = d.getDouble("Strike");
			double marketprice = 0.0;
			String trader = d.getString("TraderID");
			if ((type.equals("PUT") && strike > marketprice) || (type.equals("CALL") && strike < marketprice))
			{
				double value = (Math.abs(strike - marketprice));
				updateTraderPV(trader, value);
			}
			deleteOptionTrade(d.getObjectId("_id"));
		}
	}

	public void addTrader(Trader t)
	{
		Document d = new Document("Name", t.getName())
				.append("LastName", t.getLastName())
				.append("username", t.getUsername())
				.append("PV", t.getPV())
				.append("Delta", t.getDelta());
		db.getCollection("Traders").insertOne(d);
		log.info("Created user: {}", t);
	}

	public void removeTrader(String id)
	{
		Document d = new Document("_id", new ObjectId(id));
		db.getCollection("Traders").deleteOne(d);
	}

	public Trader getTraderDetails(String username)
	{
		BasicDBObject query = new BasicDBObject();
		query.put("username", username);
		MongoCollection<Document> users = db.getCollection("Traders");
		Document d = users.find(query).first();

		Trader t = new Trader();
		t.setId(d.getObjectId("_id").toString());
		t.setName(d.getString("Name"));
		t.setLastName(d.getString("LastName"));
		t.setUsername(d.getString("username"));
		t.setPV(d.getDouble("PV"));
		t.setDelta(d.getDouble("Delta"));

		return t;
	}

	public void updateTraderPV(String username, double value)
	{
		Trader t = getTraderDetails(username);
		t.setPV(t.getPV() + value);
		Document d = new Document("username", username);
		Document set = new Document("$set", new Document("PV", t.getPV()));
		db.getCollection("Traders").updateOne(d, set);
		log.info("Updated " + username + " PV:{}", t.getPV());
	}

	public void updateTraderDelta(String username, double value)
	{
		Trader t = getTraderDetails(username);
		t.setDelta(t.getDelta() + value);
		Document d = new Document("username", username);
		Document set = new Document("$set", new Document("Delta", t.getDelta()));
		db.getCollection("Traders").updateOne(d, set);
		log.info("Updated " + username + " Delta:{}", t.getPV());
	}

	public void close()
	{
		mongoClient.close();
	}

}