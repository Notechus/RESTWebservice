package cs.uni.tradeapp.webservice.mongo.DBController;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import cs.uni.tradeapp.utils.data.Option;
import cs.uni.tradeapp.utils.data.OptionTrade;
import cs.uni.tradeapp.utils.data.StockTrade;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;

/**
 * Created by Notechus on 06/09/2016.
 */
public class DBTradeController extends DBController
{
	private String stockName;

	public DBTradeController(MongoDatabase db)
	{
		this.db = db;
		this.documentName = "OptionTrades";
		this.stockName = "StockTrades";
	}

	public OptionTrade[] getOptionTrades(String trader)
	{
		BasicDBObject query = new BasicDBObject();
		query.put("TraderID", trader);
		MongoCollection<Document> options = db.getCollection(this.documentName);
		MongoCursor<Document> cursor = options.find(query).iterator();
		ArrayList<OptionTrade> tmp = new ArrayList<>();
		while (cursor.hasNext())
		{
			Document d = cursor.next();
			OptionTrade o = new OptionTrade();
			o.setId(d.getObjectId("_id").toString());
			o.setOptionId(d.getString("OptionID"));
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

	public OptionTrade[] getOptionTradesForIDs(Option[] options)
	{
		ArrayList<OptionTrade> tmp = new ArrayList<>();
		for (Option op : options)
		{
			BasicDBObject query = new BasicDBObject();
			query.put("OptionID", op.getId());
			MongoCollection<Document> trades = db.getCollection(this.documentName);
			MongoCursor<Document> cursor = trades.find(query).iterator();
			while (cursor.hasNext())
			{
				Document d = cursor.next();
				OptionTrade o = new OptionTrade();
				o.setId(d.getObjectId("_id").toString());
				o.setOptionId(d.getString("OptionID"));
				o.setQuantity(d.getInteger("Quantity"));
				o.setUnderlying(d.getString("Underlying"));
				o.setTrader(d.getString("Trader"));
				tmp.add(o);
			}
		}
		OptionTrade[] res = new OptionTrade[tmp.size()];
		return tmp.toArray(res);
	}

	public void addOptionTrade(OptionTrade o, String trader)
	{
		Document doc = new Document("Underlying", o.getUnderlying())
				.append("OptionID", o.getOptionId())
				.append("Quantity", o.getQuantity())
				.append("TraderID", trader);

		db.getCollection(this.documentName).insertOne(doc);
		//return db.getCollection(this.documentName).find(doc).iterator().next().getObjectId("_id").toString();
	}

	public void deleteOptionTrade(ObjectId id)
	{
		Document d = new Document("_id", id);
		db.getCollection(this.documentName).deleteOne(d);
	}

	public StockTrade[] getStockTrades(String trader)
	{
		BasicDBObject query = new BasicDBObject();
		query.put("TraderID", trader);
		MongoCollection<Document> stocks = db.getCollection(this.stockName);
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

	public void addStockTrade(StockTrade s, String trader)
	{
		Document doc = new Document("Underlying", s.getUnderlying())
				.append("Quantity", s.getQuantity())
				.append("TraderID", trader);

		db.getCollection(this.stockName).insertOne(doc);
	}

}
