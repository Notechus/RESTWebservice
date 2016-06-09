package cs.uni.tradeapp.webservice.mongo.DBController;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import cs.uni.tradeapp.utils.data.StockPosition;
import cs.uni.tradeapp.utils.data.TradePosition;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Created by Notechus on 06/09/2016.
 */
public class DBPositionController extends DBController
{
	private final Logger log = LoggerFactory.getLogger(getClass());
	private String stockName;

	public DBPositionController(MongoDatabase db)
	{
		this.db = db;
		this.documentName = "TradePositions";
		this.stockName = "StockPositions";
	}

	public TradePosition[] getTradePositions(String trader)
	{
		BasicDBObject query = new BasicDBObject();
		query.put("TraderID", trader);
		MongoCollection<Document> positions = db.getCollection(this.documentName);
		MongoCursor<Document> cursor = positions.find(query).iterator();
		ArrayList<TradePosition> tmp = new ArrayList<>();
		while (cursor.hasNext())
		{
			Document d = cursor.next();
			TradePosition t = new TradePosition();
			t.setId(d.getObjectId("_id").toString());
			t.setOptionId(d.getString("OptionID"));
			t.setUnderlying(d.getString("Underlying"));
			t.setNotional(d.getDouble("Notional"));
			t.setQuantity(d.getDouble("Quantity"));
			t.setDelta(d.getDouble("Delta"));
			tmp.add(t);
		}
		TradePosition[] pos = new TradePosition[tmp.size()];
		return tmp.toArray(pos);
	}

	public void addOrUpdateTradePosition(TradePosition t, String trader)
	{
		BasicDBObject query = new BasicDBObject();
		query.put("TraderID", trader);
		query.put("OptionID", t.getOptionId());
		MongoCollection<Document> positions = db.getCollection(this.documentName);
		MongoCursor<Document> cursor = positions.find(query).iterator();
		if (cursor.hasNext())
		{
			Document tmp = cursor.next();
			updateQuantity(t.getOptionId(), tmp.getDouble("Quantity") + t.getQuantity());
		} else
		{
			Document doc = new Document("OptionID", t.getOptionId())
					.append("Underlying", t.getUnderlying())
					.append("Notional", t.getNotional())
					.append("Quantity", t.getQuantity())
					.append("Delta", t.getDelta())
					.append("TraderID", trader);

			positions.insertOne(doc);
		}
	}

	public void updateQuantity(String optionID, double newQuantity)
	{
		Document d = new Document("OptionID", optionID);
		double q = getQuantity(optionID);
		Document set = new Document("$set", new Document("Quantity", q + newQuantity));
		db.getCollection(this.documentName).updateOne(d, set);
	}

	public double getQuantity(String optionID)
	{
		Document d = new Document("OptionID", optionID);
		return db.getCollection(this.documentName).find(d).iterator().next().getDouble("Quantity");
	}

	public StockPosition[] getStockPositions(String trader)
	{
		BasicDBObject query = new BasicDBObject();
		query.put("TraderID", trader);
		MongoCollection<Document> positions = db.getCollection(this.stockName);
		MongoCursor<Document> cursor = positions.find(query).iterator();
		ArrayList<StockPosition> tmp = new ArrayList<>();
		while (cursor.hasNext())
		{
			Document d = cursor.next();
			StockPosition s = new StockPosition();
			s.setId(d.getObjectId("_id").toString());
			s.setUnderlying(d.getString("Underlying"));
			s.setAmount(d.getDouble("Amount"));
			tmp.add(s);
		}
		StockPosition[] pos = new StockPosition[tmp.size()];
		return tmp.toArray(pos);
	}

	public void addOrUpdateTradePosition(StockPosition s, String trader)
	{
		BasicDBObject query = new BasicDBObject();
		query.put("TraderID", trader);
		query.put("Underlying", s.getUnderlying());
		MongoCollection<Document> positions = db.getCollection(this.stockName);
		MongoCursor<Document> cursor = positions.find(query).iterator();
		if (cursor.hasNext())
		{
			Document tmp = cursor.next();
			Document d = new Document("Underlying", s.getUnderlying());
			Document set = new Document("$set", new Document("Amount", tmp.getDouble("Amount") + s.getAmount()));
			positions.updateOne(d, set);
		} else
		{
			Document doc = new Document("Underlying", s.getUnderlying())
					.append("Amount", s.getAmount())
					.append("TraderID", trader);
		}
	}
}
