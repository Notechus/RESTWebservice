package cs.uni.tradeapp.webservice.mongo.DBController;

import com.mongodb.client.MongoDatabase;

/**
 * Created by Notechus on 06/09/2016.
 */
public abstract class DBController
{
	protected MongoDatabase db;
	protected String documentName;

}
