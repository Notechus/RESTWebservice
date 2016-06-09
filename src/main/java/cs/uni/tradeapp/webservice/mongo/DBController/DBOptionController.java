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
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Created by Notechus on 06/09/2016.
 */
public class DBOptionController extends DBController
{
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final String ISO_DATE_FORMAT = "YYYY-MM-DD'T'HH:mm:ssZ";
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

	public Option[] getOptionsForMaturity(LocalDateTime maturity) throws ParseException
	{
		BasicDBObject query = new BasicDBObject();
		query.put("Maturity", simpleDateFormat.parse(maturity.toString()));
		return query(query);
	}

	public void addOption(Option o, String trader) throws ParseException, DuplicateKeyException
	{
		Document doc = new Document("Underlying", o.getUnderlying())
				.append("Direction", o.getDirection())
				.append("Maturity", simpleDateFormat.parse(o.getMaturity()))
				.append("Strike", o.getStrike())
				.append("TraderID", trader)
				.append("Notional", o.getNotional());

		db.getCollection(this.documentName).insertOne(doc);
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
			o.setTrader(d.getString("Trader"));
			o.setNotional(d.getDouble("Notional"));
			tmp.add(o);
		}
		Option[] res = new Option[tmp.size()];
		tmp.toArray(res);
		cursor.close();
		return res;
	}

}
