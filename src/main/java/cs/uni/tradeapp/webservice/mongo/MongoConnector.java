package cs.uni.tradeapp.webservice.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Notechus on 05/25/2016.
 */
public class MongoConnector
{
	private final Logger log = LoggerFactory.getLogger(getClass());
	private MongoClient mongoClient;
	private MongoDatabase db;

	public MongoConnector(String connectionString, String dbname)
	{
		MongoClientURI con = new MongoClientURI(connectionString);
		mongoClient = new MongoClient(con);
		db = mongoClient.getDatabase(dbname);
	}

	public MongoDatabase getDb()
	{
		return this.db;
	}


	public void close()
	{
		mongoClient.close();
	}

}