package main;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class Database {
	private String USERNAME = "test";
	private String PASSWORD = "test";
	private String HOST = "xt-cluster-zbwyc.gcp.mongodb.net";
	private String URIString = String.format("mongodb+srv://%s:%s@%s/test?retryWrites=true", USERNAME, PASSWORD, HOST);
	private String DATABASE_NAME = "SmartParking";
	private String PARKING_COLLECTION = "ParkingManager";

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
	private MongoCollection<Document> parkingManager;

	private Database() {
		mongoClient = MongoClients.create(URIString);
		smartParkingDB = mongoClient.getDatabase(DATABASE_NAME);
		parkingManager = smartParkingDB.getCollection(PARKING_COLLECTION);
	}

	public static Database getInstance(){
		if(instance == null) {
			synchronized(Database.class) {
				if(null == instance) {
					instance  = new Database();
				}
			}
		}
		return instance;
	}

	public Document getDocumentFromVehicle(Vehicle vehicle){
		return new Document(RFIDNUMBER, vehicle.getRfidNumber()).
				append(PLATENUMBER, vehicle.getPlateNumber()).
				append(TIMEIN, vehicle.getTimeIn()).
				append(TIMEOUT, vehicle.getTimeOut()).
				append(EMOTIONIN, vehicle.getEmotionIn()).
				append(EMOTIONOUT, vehicle.getEmotionOut()).
				append(STATUS, vehicle.getStatus());
//                append(FRONTIMG_BASE64, vehicle.getFrontImg()).
//                append(BACKIMG_BASE64, vehicle.getBackImg()).
//                append(PLATEIMG_BASE64, vehicle.getPlateImg());
	}

	public void closeConnection(){
		mongoClient.close();
	}


}
