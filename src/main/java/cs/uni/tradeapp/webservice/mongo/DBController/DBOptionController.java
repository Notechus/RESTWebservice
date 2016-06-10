package cs.uni.tradeapp.webservice.mongo.DBController;

import com.mongodb.BasicDBObject;
import com.mongodb.DuplicateKeyException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import cs.uni.tradeapp.utils.data.Option;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Notechus on 06/09/2016.
 */
public class DBOptionController extends DBController
{
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final String ISO_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssX";
	private SimpleDateFormat simpleDateFormat;

	public DBOptionController(MongoDatabase db)
	{
		this.db = db;
		this.documentName = "Options";
		this.simpleDateFormat = new SimpleDateFormat(ISO_DATE_FORMAT);
	}

	public Option[] getOptions(String trader)
	{
		BasicDBObject query = new BasicDBObject();
		query.put("TraderID", trader);
		return query(query);
	}

	public Option[] getOptionsForTicker(String ticker)
	{
		BasicDBObject query = new BasicDBObject();
		query.put("Underlying", ticker);
		return query(query);
	}

	public Option[] getOptions()
	{
		MongoCollection<Document> options = db.getCollection(this.documentName);
		MongoCursor<Document> cursor = options.find().iterator();
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

	public Option[] getOptionsForMaturity(Date maturity) throws ParseException
	{
		BasicDBObject query = new BasicDBObject();
		query.put("Maturity", maturity);
		return query(query);
	}

	public double getOptionValue(String id)
	{
		BasicDBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(id));
		return db.getCollection(this.documentName).find(query).iterator().next().getDouble("Value");
	}

	public void addOption(Option o, String trader) throws ParseException, DuplicateKeyException
	{
		Document doc = new Document("Underlying", o.getUnderlying())
				.append("Direction", o.getDirection())
				.append("Maturity", simpleDateFormat.parse(o.getMaturity()))
				.append("Strike", o.getStrike())
				.append("Value", o.getValue())
				.append("TraderID", trader)
				.append("Notional", o.getNotional());

		db.getCollection(this.documentName).insertOne(doc);
	}

	public void updateOptionValue(String id, double value)
	{
		double val = getOptionValue(id);
		Document d = new Document("_id", new ObjectId(id));
		Document set = new Document("$set", new Document("Value", val + value));
		db.getCollection(this.documentName).updateOne(d, set);
	}

	public Option getOption(String id)
	{
		BasicDBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(id));
		Document d = db.getCollection(this.documentName).find(query).first();
		Option o = new Option();
		o.setId(d.getObjectId("_id").toString());
		o.setUnderlying(d.getString("Underlying"));
		o.setDirection(d.getString("Direction"));
		o.setStrike(d.getDouble("Strike"));
		o.setMaturity(simpleDateFormat.format(d.getDate("Maturity")));

		return o;
	}

	public Option[] query(BasicDBObject query)
	{
		MongoCollection<Document> options = db.getCollection(this.documentName);
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
			o.setValue(d.getDouble("Value"));
			o.setNotional(d.getDouble("Notional"));
			tmp.add(o);
		}
		Option[] res = new Option[tmp.size()];
		tmp.toArray(res);
		cursor.close();
		return res;
	}

}
