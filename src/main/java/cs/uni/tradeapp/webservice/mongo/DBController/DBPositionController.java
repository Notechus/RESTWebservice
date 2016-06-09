package cs.uni.tradeapp.webservice.mongo.DBController;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
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
			TradePosition t = new TradePosition();
		}

		return null;
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

		}

	}

}
