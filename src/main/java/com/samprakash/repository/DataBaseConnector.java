package com.samprakash.repository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Updates;
import com.samprakash.basemodel.TrainBookingDatabase;
import com.samprakash.basemodel.UserCollection;
import com.samprakash.basemodel.Users;
import com.samprakash.baseviewmodel.Hashing;
import com.samprakash.paymentmodel.Passenger;
import com.samprakash.ticketbookmodel.SeatMetaData;
import com.samprakash.ticketbookmodel.Ticket;

public class DataBaseConnector {

	private static final DataBaseConnector DATA_BASE_CONNECTOR;

	private final Properties DB_PROPERTIES;

	private final String MONGO_DB_CONNECTION_URL, TRAIN_BOOKING_DB_NAME;
	private final String TRAIN_ID, DATE, TRAIN_NAME;

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

		try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("mongodb.properties")) {

			DB_PROPERTIES.load(inputStream);

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

	public JSONObject getSeatAvailabilityForTrain(JSONArray matchedTrainList, String journeyDate) {

		JSONObject availabilityJson = new JSONObject();

		try (MongoClient mongoClient = MongoClients.create(DB_PROPERTIES.getProperty(MONGO_DB_CONNECTION_URL))) {

			MongoDatabase db = mongoClient.getDatabase(DB_PROPERTIES.getProperty(TRAIN_BOOKING_DB_NAME));

			MongoCollection<Document> seatLayoutCol = db.getCollection(TrainBookingDatabase.SEAT_LAYOUT.name());
			MongoCollection<Document> seatAvailCol = db.getCollection(TrainBookingDatabase.SEAT_AVAILABILITY.name());

			for (int i = 0; i < matchedTrainList.length(); i++) {

				String trainID = matchedTrainList.getJSONObject(i).getString(TRAIN_ID);

				Document layoutDoc = seatLayoutCol.find(Filters.eq(TRAIN_ID, trainID)).first();
				if (layoutDoc == null)
					continue;
				Document classInfo = (Document) layoutDoc.get("class_info");

				Document seatAvail = seatAvailCol
						.find(Filters.and(Filters.eq(TRAIN_ID, trainID), Filters.eq(DATE, journeyDate))).first();

				Set<String> booked = new HashSet<>();
				if (seatAvail != null && seatAvail.containsKey("booked")) {
					booked.addAll(seatAvail.getList("booked", String.class));
				}

				JSONObject trainJson = new JSONObject();

				// NEW: coaches is a document, not an array
				Document coachesDoc = (Document) layoutDoc.get("coaches");

				for (String coachClass : coachesDoc.keySet()) {

					Document classLimits = (Document) classInfo.get(coachClass);
					int racLimit = classLimits.getInteger("rac_limit");
					int wlLimit = classLimits.getInteger("wl_limit");

					// Read RAC and WL already booked (if exist)
					List<String> bookedRAC = new ArrayList<>();
					List<String> bookedWL = new ArrayList<>();
					if (seatAvail != null) {
						if (seatAvail.containsKey("rac")) {
							bookedRAC = seatAvail.getList("rac", String.class);
						}
						if (seatAvail.containsKey("wl")) {
							bookedWL = seatAvail.getList("wl", String.class);
						}
					}

					int usedRAC = bookedRAC.size();
					int usedWL = bookedWL.size();

					int availableRAC = Math.max(0, racLimit - usedRAC);
					int availableWL = Math.max(0, wlLimit - usedWL);

					JSONArray coachArray = new JSONArray();
					List<Document> coachList = (List<Document>) coachesDoc.get(coachClass);

					for (Document coach : coachList) {

						int available = 0;
						String coachNo = coach.getString("coach_no");
						List<Document> seats = coach.getList("seats", Document.class);

						for (Document seat : seats) {
							String seatID = coachNo + "-" + seat.getString("seat_no");

							if (!booked.contains(seatID)) {
								available++;
							}
						}

						JSONObject coachJson = new JSONObject();

						if (available > 0) {
							coachJson.put("coach_no", coachNo);
							coachJson.put("available_seats", available);
							coachArray.put(coachJson);
						}

					}

					if (coachArray.isEmpty()) {
						if (availableRAC > 0) {
							JSONObject racJsonObj = new JSONObject();
							racJsonObj.put("status", "RAC");
							racJsonObj.put("available_seats", availableRAC);
							trainJson.put(coachClass, racJsonObj);
						} else if (availableWL > 0) {
							JSONObject wlJsonObj = new JSONObject();
							wlJsonObj.put("status", "WL");
							wlJsonObj.put("available_seats", availableWL);
							trainJson.put(coachClass, wlJsonObj);
						} else {
							JSONObject noTicketsJsonObj = new JSONObject();
							noTicketsJsonObj.put("status", "NOT_AVAILABLE");
							trainJson.put(coachClass, noTicketsJsonObj);
						}
					} else {
						trainJson.put(coachClass, coachArray);
					}

				}

				availabilityJson.put(trainID, trainJson);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return availabilityJson;
	}

	public Map<String, String> getTrainNameByID(JSONArray matchedTrainList) {

		Map<String, String> trainNameIDMap = new TreeMap<>();

		if (matchedTrainList == null) {

			System.out.println("Provided matchedTrainList Json Array is Null");
			return trainNameIDMap;
		}

		try (MongoClient mongoClient = MongoClients.create(DB_PROPERTIES.getProperty(MONGO_DB_CONNECTION_URL, DATE))) {

			MongoDatabase trainBookingDatabase = mongoClient
					.getDatabase(DB_PROPERTIES.getProperty(TRAIN_BOOKING_DB_NAME, ""));

			MongoCollection<Document> trainCollection = trainBookingDatabase
					.getCollection(TrainBookingDatabase.TRAINS.name());

			int size = matchedTrainList.length();

			for (int i = 0; i < size; i++) {

				String trainID = ((JSONObject) matchedTrainList.get(i)).getString(TRAIN_ID);

				Document trainDocument = trainCollection.find(Filters.eq(TRAIN_ID, trainID)).first();

				String trainName = trainDocument.getString(TRAIN_NAME);

				trainNameIDMap.put(trainID, trainName);
			}
		}

		return trainNameIDMap;
	}

	public Ticket getConfirmedTicektForAllPassenger(Set<Passenger> passengerDetails, String trainId, String trainName,
			String source, String destination, String classType, String date) {

		try (MongoClient mongoClient = MongoClients.create(DB_PROPERTIES.getProperty(MONGO_DB_CONNECTION_URL))) {

			MongoDatabase db = mongoClient.getDatabase(DB_PROPERTIES.getProperty(TRAIN_BOOKING_DB_NAME));

			MongoCollection<Document> layoutCollection = db.getCollection(TrainBookingDatabase.SEAT_LAYOUT.name());
			MongoCollection<Document> availabilityCollection = db.getCollection(TrainBookingDatabase.SEAT_AVAILABILITY.name());

			// 1. Load layout
			Document layout = layoutCollection.find(Filters.eq(TRAIN_ID, trainId)).first();
			if (layout == null)
				throw new RuntimeException("Layout Missing");

			List<Document> coaches = (List<Document>) ((Document) layout.get("coaches")).get(classType);

			// 2. Load availability
			Document availability = availabilityCollection
					.find(Filters.and(Filters.eq(TRAIN_ID, trainId), Filters.eq(DATE, date))).first();

			if (availability == null) {
				availability = new Document(TRAIN_ID, trainId).append(DATE, date).append("booked",
						new ArrayList<>());
				availabilityCollection.insertOne(availability);
			}

			List<String> bookedSeats = (List<String>) availability.get("booked");

			// 3. Flatten seats
			List<Document> allSeats = new ArrayList<>();
			for (Document coach : coaches) {
				String coachNo = coach.getString("coach_no");
				List<Document> seats = (List<Document>) coach.get("seats");

				for (Document seat : seats) {
					seat.append("coach_no", coachNo);
					allSeats.add(seat);
				}
			}

			// **GROUP BOOKING OPTIMIZATION**
			// Sort by coach → seat_no so grouped passengers get nearby seats
			allSeats.sort((a, b) -> {
				int cmp = a.getString("coach_no").compareTo(b.getString("coach_no"));
				if (cmp != 0)
					return cmp;
				return Integer.compare(Integer.parseInt(a.getString("seat_no")),
						Integer.parseInt(b.getString("seat_no")));
			});

			// 4. Allocate seat for each passenger
			for (Passenger p : passengerDetails) {

				String pref = p.getPreference(); // LB/UB/MB/SL/SU

				// LIST 1 → Preferred seats
				List<Document> preferredSeats = allSeats.stream()
						.filter(s -> s.getString("berth").equalsIgnoreCase(pref)
								&& !bookedSeats.contains(s.getString("coach_no") + "-" + s.getString("seat_no")))
						.toList();

				// LIST 2 → Fallback seats (everything else)
				List<Document> fallbackSeats = allSeats.stream()
						.filter(s -> !bookedSeats.contains(s.getString("coach_no") + "-" + s.getString("seat_no")))
						.toList();

				boolean allocated = tryAllocate(p, preferredSeats, bookedSeats, availabilityCollection, trainId, date);

				if (!allocated) {
					allocated = tryAllocate(p, fallbackSeats, bookedSeats, availabilityCollection, trainId, date);
				}

				if (!allocated)
					throw new RuntimeException("No seats available for " + p.getName());
			}

			// 5. Create ticket
			String pnr = "PNR" + System.currentTimeMillis();
			return new Ticket(trainId, trainName, classType, source, destination, pnr, "TXN-" + System.nanoTime(),
					passengerDetails);

		}
	}

	private boolean tryAllocate(Passenger p, List<Document> seatList, List<String> bookedSeats,
			MongoCollection<Document> availabilityCollection, String trainId, String date) {

		for (Document seat : seatList) {

			String coachNo = seat.getString("coach_no");
			String seatNo = seat.getString("seat_no");
			String seatCode = coachNo + "-" + seatNo;

			Document updated = availabilityCollection.findOneAndUpdate(
					Filters.and(Filters.eq(TRAIN_ID, trainId), Filters.eq(DATE, date),
							Filters.not(Filters.in("booked", seatCode))),
					Updates.push("booked", seatCode),
					new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));

			if (updated != null) {
				p.setSeatMetaData(new SeatMetaData(coachNo, Byte.parseByte(seatNo)));
				bookedSeats.add(seatCode);
				return true;
			}
		}
		return false;
	}

}
