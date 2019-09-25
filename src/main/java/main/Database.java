package main;

import com.google.gson.Gson;
import com.mongodb.DBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import main.Domain.ParkingSession;
import main.ImageProcessor.ImageTypeUtils;
import org.bson.Document;

public class Database {
	private final static String USERNAME = "ntxtung";
	private final static String PASSWORD = "xuantung98";
	private final static String HOST = "206.189.40.187";
	private final static int    PORT = 27017;
	private final static String URI_CONNECTION = String.format("mongodb://%s:%s@%s:%d/admin?retryWrites=true", USERNAME, PASSWORD, HOST, PORT);
	private final static String DATABASE_NAME = "SmartParking";
	private final static String ACTIVE_PARKING_SESSION = "ParkingSession";
	private final static String HISTORY_PARKING_SESSION = "HistorySession";

	private String RFIDNUMBER = "rfidNumber";
	private String PLATENUMBER = "plateNumber";
	private String TIMEIN = "timeIn";
	private String TIMEOUT = "timeOut";
	private String EMOTIONIN = "emotionIn";
	private String EMOTIONOUT = "emotionOut";
	private String STATUS = "status";
	private String FRONTIMG_BASE64 = "frontImgBase64";
	private String BACKIMG_BASE64 = "backImgBase64";
	private String PLATEIMG_BASE64 = "plateImgBase64";

	private static Database instance;

	private MongoClient mongoClient;
	private MongoDatabase smartParkingDB;
	private MongoCollection<Document> activeParkingCollection;
	private MongoCollection<Document> historyParkingCollection;

	private Database() {
		mongoClient = MongoClients.create(URI_CONNECTION);
		smartParkingDB = mongoClient.getDatabase(DATABASE_NAME);
		activeParkingCollection = smartParkingDB.getCollection(ACTIVE_PARKING_SESSION);
		historyParkingCollection = smartParkingDB.getCollection(HISTORY_PARKING_SESSION);
	}

	public static Database getInstance(){
		if (instance == null) {
			synchronized(Database.class) {
				if (null == instance) {
					instance  = new Database();
				}
			}
		}
		return instance;
	}

	public void insert2ActiveSessions(ParkingSession parkingSession) {
		this.activeParkingCollection.insertOne(this.createDocumentFromVehicle(parkingSession));
	}

	public Document createDocumentFromVehicle(ParkingSession parkingSession) {

//		Document document = new Document();
//		Gson gson = new Gson();
//		String json = gson.toJson(parkingSession);
//		System.out.println(json);
//		return null;
//		this.activeParkingCollection.createIndex()
		return new Document(RFIDNUMBER, parkingSession.getRfidNumber()).
				append(PLATENUMBER, parkingSession.getPlateNumber()).
				append(TIMEIN, parkingSession.getTimeIn()).
				append(TIMEOUT, parkingSession.getTimeOut()).
				append(EMOTIONIN, parkingSession.getEmotionIn().toString()).
				append(EMOTIONOUT, parkingSession.getEmotionOut().toString()).
				append(STATUS, parkingSession.getStatus()).
                append(FRONTIMG_BASE64, ImageTypeUtils.fxImage2Base64(parkingSession.getFrontImg())).
                append(BACKIMG_BASE64, ImageTypeUtils.fxImage2Base64(parkingSession.getBackImg())).
                append(PLATEIMG_BASE64, ImageTypeUtils.fxImage2Base64(parkingSession.getPlateImg()));
	}

	public ParkingSession findActiveSessionByRfid(String rfidNumber) {
		Document resDoc = this.activeParkingCollection.find(Filters.eq("rfidNumber", rfidNumber)).first();
//		if (resDoc != null)
//			System.out.println(resDoc.toJson());
		return null;
	}


	public void closeConnection(){
		mongoClient.close();
	}


}
