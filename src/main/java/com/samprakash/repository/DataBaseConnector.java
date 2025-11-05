package com.samprakash.repository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.samprakash.basemodel.TrainBookingDatabase;
import com.samprakash.basemodel.UserCollection;
import com.samprakash.basemodel.Users;
import com.samprakash.baseviewmodel.Hashing;

public class DataBaseConnector {

	private static final DataBaseConnector DATA_BASE_CONNECTOR;
	
	private final Properties DB_PROPERTIES;

	private final String MONGO_DB_CONNECTION_URL, TRAIN_BOOKING_DB_NAME;
	private final String TRAIN_ID, DATE,TRAIN_NAME,AVAILABLE_DAY;

	static {

		DATA_BASE_CONNECTOR = new DataBaseConnector();
	}

	public static synchronized DataBaseConnector getInstance() {

		return DATA_BASE_CONNECTOR;
	}

	private DataBaseConnector() {

		DB_PROPERTIES = new Properties();

		MONGO_DB_CONNECTION_URL = "Db.Url";
		TRAIN_BOOKING_DB_NAME = "Db.TrainBooking.name";
		TRAIN_ID = "train_id";
		DATE = "date";
		TRAIN_NAME = "train_name";
		AVAILABLE_DAY = "available_days";

		try (InputStream is = getClass().getClassLoader().getResourceAsStream("mongodb.properties")) {

			DB_PROPERTIES.load(is);

		} catch (FileNotFoundException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	public boolean addUser(Users newUser) {

		if (newUser == null) {
			System.out.println("Provided User Object is Null");
			return false;
		}

		if (isUserAlreadyExist(newUser.userName())) {
			System.out.println("User Already Exist");
			return false;
		}

		try (MongoClient mongoClient = MongoClients.create(DB_PROPERTIES.getProperty(MONGO_DB_CONNECTION_URL, ""))) {

			MongoDatabase trainBookingDataBase = mongoClient
					.getDatabase(DB_PROPERTIES.getProperty(TRAIN_BOOKING_DB_NAME, ""));

			MongoCollection<Document> allUserDocument = trainBookingDataBase
					.getCollection(TrainBookingDatabase.USERS.name());
			Document newUserDocument = new Document(UserCollection.FULL_NAME.name(), newUser.fullName())
					.append(UserCollection.EMAIL.name(), newUser.email())
					.append(UserCollection.USER_NAME.name(), newUser.userName())
					.append(UserCollection.HASHED_PASSWORD.name(), newUser.hashedPassword());

			allUserDocument.insertOne(newUserDocument);
		}

		return true;

	}

	public boolean isUserAlreadyExist(String userName) {

		try (MongoClient mongoClient = MongoClients.create(DB_PROPERTIES.getProperty(MONGO_DB_CONNECTION_URL, ""))) {

			MongoDatabase trainBookingDataBase = mongoClient
					.getDatabase(DB_PROPERTIES.getProperty(TRAIN_BOOKING_DB_NAME, ""));

			MongoCollection<Document> allUserDocument = trainBookingDataBase
					.getCollection(TrainBookingDatabase.USERS.name());
			Document userDocument = new Document(UserCollection.USER_NAME.name(), userName);

			return allUserDocument.find(userDocument).iterator().hasNext();
		}

	}

	public boolean isUserCredentialIsCorrect(String userName, String password) {

		boolean isCredentialsIsCorrect = false;
		if (userName == null || password == null) {
			System.out.println("Username or Password is null");
			return isCredentialsIsCorrect;
		}
		try (MongoClient mongoClient = MongoClients.create(DB_PROPERTIES.getProperty(MONGO_DB_CONNECTION_URL, ""))) {

			MongoDatabase trainBookingDatabase = mongoClient
					.getDatabase(DB_PROPERTIES.getProperty(TRAIN_BOOKING_DB_NAME, ""));

			MongoCollection<Document> allUserDocument = trainBookingDatabase
					.getCollection(TrainBookingDatabase.USERS.name());

			Document userDocument = allUserDocument.find(Filters.eq(UserCollection.USER_NAME.name(), userName)).first();

			if (userDocument != null) {

				String hashedPassword = userDocument.getString(UserCollection.HASHED_PASSWORD.name());

				return Hashing.isPlainPasswordMatchedWithHashedPassword(password, hashedPassword);

			}

			return false;

		}

	}

	public JSONArray getMatchedTrain(String fromStation, String toStation, String travelDate) {

		JSONArray matchedTrainList = new JSONArray();

		if (fromStation == null || toStation == null || travelDate == null) {

			System.out.println("Given fromStation ,toStation or travelDate is Null");
			return matchedTrainList;
		}

		try (MongoClient mongoClient = MongoClients.create(DB_PROPERTIES.getProperty(MONGO_DB_CONNECTION_URL, ""))) {

			MongoDatabase trainBookingDatabase = mongoClient
					.getDatabase(DB_PROPERTIES.getProperty(TRAIN_BOOKING_DB_NAME, ""));

			MongoCollection<Document> allTrainDocument = trainBookingDatabase
					.getCollection(TrainBookingDatabase.TRAIN_SCHEDULE.name());

			Document query = new Document()
				    .append("routes.station", new Document("$all", Arrays.asList(fromStation, toStation)))
				    .append("available_days", travelDate);
			
			       FindIterable<Document> trainsOnDate = allTrainDocument.find(query);

			for (Document train : trainsOnDate) {
				List<Document> routes = (List<Document>) train.get("routes");

				int fromIndex = -1, toIndex = -1, routeSize = routes.size();

				// 3. Check if both stations exist in routes and order is valid
				for (int i = 0; i < routeSize; i++) {
					String stationName = routes.get(i).getString("station");
					if (stationName.equalsIgnoreCase(fromStation)) {
						fromIndex = i;
					}
					if (stationName.equalsIgnoreCase(toStation)) {
						toIndex = i;
					}
				}

				if (fromIndex != -1 && toIndex != -1 && fromIndex < toIndex) {
					matchedTrainList.put(new JSONObject(train.toJson()));
				}
			}

			return matchedTrainList;

		}

	}

	public JSONObject getSeatAvailabilityForTrain(JSONArray matchedTrainList) {

	    JSONObject availabilityJson = new JSONObject();

	    if (matchedTrainList == null) {
	        System.out.println("Given JSON Array is null");
	        return availabilityJson;
	    }

	    try (MongoClient mongoClient = MongoClients.create(DB_PROPERTIES.getProperty(MONGO_DB_CONNECTION_URL, ""))) {

	        MongoDatabase trainBookingDatabase = mongoClient.getDatabase(DB_PROPERTIES.getProperty(TRAIN_BOOKING_DB_NAME, ""));
	        MongoCollection<Document> availabilityCollection = trainBookingDatabase
	                .getCollection(TrainBookingDatabase.SEAT_AVAILABILITY.name());

	        for (int i = 0; i < matchedTrainList.length(); i++) {
	            JSONObject trainObj = matchedTrainList.getJSONObject(i);
	            String trainID = trainObj.optString(TRAIN_ID, "");
	            String travelDate = trainObj.optString(DATE, "");
	            
	          //  System.out.println("Travel Date : "+travelDate);

	            AggregateIterable<Document> result = availabilityCollection.aggregate(Arrays.asList(
	                    new Document("$match", new Document(TRAIN_ID, trainID)),
	                    new Document("$unwind", "$coaches"),
	                    new Document("$unwind", "$coaches.seats"),
	                    new Document("$match", new Document("coaches.seats.status", "available")),
	                    new Document("$group", new Document("_id", new Document("coach_no", "$coaches.coach_no")
	                                                       .append("class", "$coaches.class"))
	                                           .append("available_seats", new Document("$sum", 1)))
	            ));

	            JSONObject trainJson = new JSONObject();

	            for (Document doc : result) {
	                Document idDoc = doc.get("_id", Document.class);
	                String coachNo = idDoc.getString("coach_no");
	                String coachClass = idDoc.getString("class");
	                int availableSeats = doc.getInteger("available_seats", 0);

	                JSONObject coachJson = new JSONObject();
	                coachJson.put("class", coachClass);
	                coachJson.put("available_seats", availableSeats);

	                trainJson.put(coachNo, coachJson);
	            }

	            availabilityJson.put(trainID, trainJson);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return availabilityJson;
	}

	public Map<String, String> getTrainNameByID(JSONArray matchedTrainList) {
		
		Map<String,String> trainNameIDMap = new TreeMap<>();
		
		
		if(matchedTrainList == null) {
			
			System.out.println("Provided matchedTrainList Json Array is Null");
			return trainNameIDMap;
		}
		
		
		try(MongoClient mongoClient = MongoClients.create(DB_PROPERTIES.getProperty(MONGO_DB_CONNECTION_URL, DATE))) {
			
			MongoDatabase trainBookingDatabase = mongoClient.getDatabase(DB_PROPERTIES.getProperty(TRAIN_BOOKING_DB_NAME,""));
			
			MongoCollection<Document> trainCollection = trainBookingDatabase.getCollection(TrainBookingDatabase.TRAINS.name());
			
			
				int size = matchedTrainList.length();
				
				
				for(int i = 0 ; i < size ; i++) {
					
					String trainID = ((JSONObject)matchedTrainList.get(i)).getString(TRAIN_ID);
					
					Document trainDocument = trainCollection.find(Filters.eq(TRAIN_ID, trainID)).first();
					
					String trainName = trainDocument.getString(TRAIN_NAME);
					
					trainNameIDMap.put(trainID, trainName);
				}
		}
		
		return trainNameIDMap;
	}


}
