package main;

import com.google.gson.Gson;
import com.mongodb.Block;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import org.bson.Document;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
	
	private static final String hostName	= "localhost";
	private static final String database	= "smart_parking";
	private static final int	port 		= 3306;
	private static final String user		= "smart_parking";
	private static final String password	= "smart_parking";

	private static Database instance = null;
	
	private static Connection conn = null;
	
	private static boolean connect2DB() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://" + hostName + "/" + database +"?useSSL=false", user, password);
		} catch (ClassNotFoundException | SQLException e) {
			System.out.println("[ERROR]" + e.getMessage());
			return false;
			
		}
		System.out.println("Connected to " + hostName + "/" + database);
		return true;
	}

	private static boolean connect2DB2() {
		String uri = "mongodb+srv://smart_parking:smart_parking@smartparking-bbfly.mongodb.net/test?retryWrites=true";
		MongoClient client = MongoClients.create(uri);
		MongoDatabase db = client.getDatabase("test");
		MongoCollection collection = db.getCollection("msg");
		System.out.println(collection.countDocuments());
		return client != null;
	}
	
	public static Database getInstance() {
		if (instance == null) {
			instance = new Database();
			if (instance.connect2DB2() == false) {
				System.exit(101);
			}
		}
		return instance;
	}
	
}

class KeyFile {
	private String uri = "MongoDB_URI";
	private String database = "MongoDB_Database";
	private String collection = "MongoDB_Collection";
	private SecretKey key = null;

	public String getUri() { return uri; }
	public void setUri(String uri) { this.uri = uri; }

	public String getDatabase() { return database; }
	public void setDatabase(String database) { this.database = database; }

	public String getCollection() { return collection; }
	public void setCollection(String collection) { this.collection = collection; }

	public SecretKey getKey() { return key; }
	public void setKey(SecretKey key) { this.key = key; }

	public KeyFile() {};

	public KeyFile(String keyFileName) {

	}

	public static void genTemplateKeyFile(String keyFileName) {
		KeyFile templateKey = new KeyFile();
		//new Gson().toJson(this);
	}
}
