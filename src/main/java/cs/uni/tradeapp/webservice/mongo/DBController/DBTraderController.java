package cs.uni.tradeapp.webservice.mongo.DBController;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import cs.uni.tradeapp.utils.data.Trader;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Notechus on 06/09/2016.
 */
public class DBTraderController extends DBController
{
	private final Logger log = LoggerFactory.getLogger(getClass());

	public DBTraderController(MongoDatabase db)
	{
		this.db = db;
		this.documentName = "Traders";
	}

	public void addTrader(Trader t)
	{
		Document d = new Document("Name", t.getName())
				.append("LastName", t.getLastName())
				.append("username", t.getUsername())
				.append("PV", t.getPV())
				.append("Delta", t.getDelta());
		db.getCollection(this.documentName).insertOne(d);
		log.info("Created user: {}", t);
	}

	public void removeTrader(String id)
	{
		Document d = new Document("_id", new ObjectId(id));
		db.getCollection(this.documentName).deleteOne(d);
	}

	public Trader getTraderDetails(String username)
	{
		BasicDBObject query = new BasicDBObject();
		query.put("username", username);
		MongoCollection<Document> users = db.getCollection(this.documentName);
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

	public String getTraderID(String username)
	{
		BasicDBObject query = new BasicDBObject();
		query.put("username", username);
		MongoCollection<Document> users = db.getCollection(this.documentName);
		Document d = users.find(query).first();
		return d.getObjectId("_id").toString();
	}

	public void updateTraderPV(String username, double value)
	{
		Trader t = getTraderDetails(username);
		t.setPV(t.getPV() + value);
		Document d = new Document("username", username);
		Document set = new Document("$set", new Document("PV", t.getPV()));
		db.getCollection(this.documentName).updateOne(d, set);
		log.info("Updated " + username + " PV:{}", t.getPV());
	}

	public void updateTraderDelta(String username, double value)
	{
		Trader t = getTraderDetails(username);
		t.setDelta(t.getDelta() + value);
		Document d = new Document("username", username);
		Document set = new Document("$set", new Document("Delta", t.getDelta()));
		db.getCollection(this.documentName).updateOne(d, set);
		log.info("Updated " + username + " Delta:{}", t.getPV());
	}
}
