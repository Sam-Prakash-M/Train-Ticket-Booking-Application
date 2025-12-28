package com.samprakash.repository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mongodb.client.ClientSession;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Updates;
import com.samprakash.basemodel.Status;
import com.samprakash.basemodel.TrainBookingDatabase;
import com.samprakash.basemodel.UserCollection;
import com.samprakash.basemodel.Users;
import com.samprakash.baseviewmodel.Hashing;
import com.samprakash.exception.SeatNotAvailableException;
import com.samprakash.paymentmodel.Passenger;
import com.samprakash.ticketbookmodel.BookingData;
import com.samprakash.ticketbookmodel.BookingState;
import com.samprakash.ticketbookmodel.PassengerCollection;
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

	// -----------------------
	// Public booking method
	// -----------------------
	public Ticket getConfirmedTicketForAllPassenger(Set<Passenger> passengerDetails, String trainId, String trainName,
			String source, String destination, String classType, String journeyDate, double totalAmount)
			throws SeatNotAvailableException, Exception {

		if (passengerDetails == null || passengerDetails.isEmpty()) {
			throw new IllegalArgumentException("No passengers provided");
		}

		// convert to list to keep ordering for group proximity
		List<Passenger> passengerList = new ArrayList<>(passengerDetails);

		try (MongoClient mongoClient = MongoClients.create(DB_PROPERTIES.getProperty(MONGO_DB_CONNECTION_URL))) {

			MongoDatabase db = mongoClient.getDatabase(DB_PROPERTIES.getProperty(TRAIN_BOOKING_DB_NAME));
			MongoCollection<Document> layoutCol = db.getCollection(TrainBookingDatabase.SEAT_LAYOUT.name());
			MongoCollection<Document> availCol = db.getCollection(TrainBookingDatabase.SEAT_AVAILABILITY.name());

			// Load layout (static)
			Document layoutDoc = layoutCol.find(Filters.eq(TRAIN_ID, trainId)).first();
			if (layoutDoc == null)
				throw new RuntimeException("Train layout not found for " + trainId);

			// Load or create availability doc for the date
			Document availFilter = new Document(TRAIN_ID, trainId).append(DATE, journeyDate);
			Document availDoc = availCol.find(availFilter).first();
			if (availDoc == null) {
				availDoc = new Document(TRAIN_ID, trainId).append(DATE, journeyDate)
						.append("booked", new ArrayList<String>()).append("rac", new ArrayList<String>())
						.append("wl", new ArrayList<String>());
				availCol.insertOne(availDoc);
			} else {
				// FIX EXISTING DOCUMENTS
				if (!availDoc.containsKey("rac") || availDoc.get("rac") == null)
					availCol.updateOne(availFilter, Updates.set("rac", new ArrayList<String>()));

				if (!availDoc.containsKey("wl") || availDoc.get("wl") == null)
					availCol.updateOne(availFilter, Updates.set("wl", new ArrayList<String>()));

			}
			availDoc = availCol.find(availFilter).first();

			// Read class_info for limits
			Document classInfoDoc = (Document) layoutDoc.get("class_info");
			if (classInfoDoc == null || !classInfoDoc.containsKey(classType))
				throw new RuntimeException("class_info missing or class not found: " + classType);

			Document classLimits = (Document) classInfoDoc.get(classType);
			int racLimit = classLimits.getInteger("rac_limit", 0);
			int wlLimit = classLimits.getInteger("wl_limit", 0);

			// Build flattened seat list for the class (coach order, seat_no order)
			List<Document> allSeatsInClass = flattenSeatsFromLayout(layoutDoc, classType);

			// pre-calculate counts (from DB)
			List<String> bookedList = availDoc.getList("booked", String.class, new ArrayList<>());
			List<String> racList = availDoc.getList("rac", String.class, new ArrayList<>());
			List<String> wlList = availDoc.getList("wl", String.class, new ArrayList<>());

			int freeConfirmed = countFreeConfirmedSeats(allSeatsInClass, bookedList);
			int freeRac = Math.max(0, racLimit - racList.size());
			int freeWl = Math.max(0, wlLimit - wlList.size());

			int required = passengerList.size();
			if (freeConfirmed + freeRac + freeWl < required) {
				throw new SeatNotAvailableException(
						"Not enough seats (Confirmed + RAC + WL) for " + required + " passengers");
			}

			// We'll track allocations to rollback if something fails
			List<String> confirmedAllocated = new ArrayList<>();
			List<String> racAllocated = new ArrayList<>();
			List<String> wlAllocated = new ArrayList<>();

			// -------- PHASE A: allocate confirmed seats honoring preference and grouping
			// --------
			try {
				List<Passenger> stillUnallocated = allocateConfirmedGroup(passengerList, allSeatsInClass, bookedList,
						availCol, trainId, journeyDate, confirmedAllocated, classType);

				// -------- PHASE B: allocate RAC for remaining --------
				if (!stillUnallocated.isEmpty() && freeRac > 0) {
					stillUnallocated = allocateRac(stillUnallocated, availCol, trainId, journeyDate, racLimit,
							racAllocated, classType);
				}

				// -------- PHASE C: allocate WL for remaining --------
				if (!stillUnallocated.isEmpty() && freeWl > 0) {
					stillUnallocated = allocateWl(stillUnallocated, availCol, trainId, journeyDate, wlLimit,
							wlAllocated, classType);
				}

				// If anything still unallocated -> rollback everything and throw
				if (!stillUnallocated.isEmpty()) {
					rollback(availCol, availFilter, confirmedAllocated, racAllocated, wlAllocated);
					throw new RuntimeException("Unable to allocate seats for all passengers. Booking rolled back.");
				}

				// Success: build ticket
				String pnr = generatePnr(trainId);
				String txnId = "TXN-" + System.nanoTime();

				// Create Ticket (using your record)
				return new Ticket(journeyDate, trainId, trainName, classType, source, destination, pnr, txnId,
						new HashSet<>(passengerList), totalAmount);

			}

			catch (Exception inner) {
				// Rollback and propagate
				rollback(availCol, availFilter, confirmedAllocated, racAllocated, wlAllocated);
				throw inner;
			}

		}
	}

	// -------------------------
	// Helper methods
	// -------------------------

	private String generatePnr(String trainId) {
		return "PNR-" + trainId + "-" + System.currentTimeMillis();
	}

	/**
	 * Flatten seats for the given class in order: coach order and seat_no numeric
	 * order. Each seat doc we return has fields: "coach_no", "seat_no", "berth".
	 */
	private List<Document> flattenSeatsFromLayout(Document layoutDoc, String classType) {
		Document coachesDoc = (Document) layoutDoc.get("coaches");
		if (coachesDoc == null || !coachesDoc.containsKey(classType))
			return Collections.emptyList();

		List<Document> coachList = (List<Document>) coachesDoc.get(classType);
		List<Document> allSeats = new ArrayList<>();

		if (coachList == null)
			return allSeats;

		for (Document coach : coachList) {
			String coachNo = coach.getString("coach_no");
			List<Document> seats = (List<Document>) coach.getList("seats", Document.class, Collections.emptyList());
			for (Document seat : seats) {
				Document seatCopy = new Document(seat); // copy to avoid mutating original
				seatCopy.put("coach_no", coachNo);
				allSeats.add(seatCopy);
			}
		}

		// sort by coach_no (lexicographically) then seat_no (numerically)
		allSeats.sort((a, b) -> {
			int cmp = a.getString("coach_no").compareTo(b.getString("coach_no"));
			if (cmp != 0)
				return cmp;
			return Integer.compare(Integer.parseInt(a.getString("seat_no")), Integer.parseInt(b.getString("seat_no")));
		});

		return allSeats;
	}

	private int countFreeConfirmedSeats(List<Document> allSeatsInClass, List<String> booked) {
		// count seats in layout that are not in booked array
		int count = 0;
		Set<String> bookedSet = new HashSet<>(booked);
		for (Document s : allSeatsInClass) {
			String code = seatCode(s.getString("coach_no"), s.getString("seat_no"));
			if (!bookedSet.contains(code))
				count++;
		}
		return count;
	}

	private String seatCode(String coachNo, String seatNo) {
		return coachNo + "-" + seatNo;
	}

	/**
	 * Allocate confirmed seats for the whole group trying preferences first and
	 * clustering by coach/seat order. - passengerList: ordered list (group
	 * ordering) - allSeatsInClass: flattened & sorted seats - bookedList: current
	 * booked seats (snapshot) - used to skip known booked seats quickly - availCol:
	 * collection to perform atomic updates - trainId/date filter used inside atomic
	 * updates - confirmedAllocated: out parameter list to collect seat codes
	 * allocated successfully (for rollback)
	 *
	 * Returns list of passengers still unallocated after trying confirmed seats.
	 */
	private List<Passenger> allocateConfirmedGroup(List<Passenger> passengerList, List<Document> allSeatsInClass,
			List<String> bookedList, MongoCollection<Document> availCol, String trainId, String journeyDate,
			List<String> confirmedAllocated, String classType) {

		List<Passenger> stillUnallocated = new ArrayList<>(passengerList);

		// We'll attempt to allocate each passenger in order. For each passenger:
		// 1) Build preferred seat candidates (berth == pref) in the order of
		// allSeatsInClass
		// 2) Try to allocate the first candidate atomically via findOneAndUpdate(push
		// booked) with condition that seat not in booked
		// 3) If not allocated from preferred, try fallback seat list
		// We also maintain a local bookedSet combining DB snapshot + our
		// confirmedAllocated to minimize retries.
		Set<String> bookedSet = new HashSet<>(bookedList);
		bookedSet.addAll(confirmedAllocated);

		Document availFilter = new Document(TRAIN_ID, trainId).append(DATE, journeyDate);
		FindOneAndUpdateOptions options = new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER);

		List<Passenger> nextRound = new ArrayList<>();

		for (Passenger p : stillUnallocated) {
			String pref = p.getPreference() != null ? p.getPreference().trim().toUpperCase() : "";

			// preferred candidates: berth equals preference and not already in bookedSet
			List<Document> preferred = allSeatsInClass.stream()
					.filter(d -> pref.length() > 0 && pref.equalsIgnoreCase(d.getString("berth")))
					.filter(d -> !bookedSet.contains(seatCode(d.getString("coach_no"), d.getString("seat_no"))))
					.collect(Collectors.toList());

			boolean allocated = tryAllocateFromList(p, preferred, bookedSet, availCol, availFilter, options,
					confirmedAllocated, classType);

			if (!allocated) {
				// fallback: all available seats in order (not booked)
				List<Document> fallback = allSeatsInClass.stream()
						.filter(d -> !bookedSet.contains(seatCode(d.getString("coach_no"), d.getString("seat_no"))))
						.collect(Collectors.toList());

				allocated = tryAllocateFromList(p, fallback, bookedSet, availCol, availFilter, options,
						confirmedAllocated, classType);
			}

			if (!allocated) {
				// could not allocate confirmed seat — push to nextRound for RAC/WL allocation
				nextRound.add(p);
			}
		}

		return nextRound;
	}

	/**
	 * Try allocating a single passenger from the ordered seatCandidates list.
	 * Returns true if allocated (and updates passenger object and
	 * confirmedAllocated list).
	 */
	private boolean tryAllocateFromList(Passenger p, List<Document> seatCandidates, Set<String> bookedSet,
			MongoCollection<Document> availCol, Document availFilter, FindOneAndUpdateOptions options,
			List<String> confirmedAllocated, String classType) {

		for (Document seatDoc : seatCandidates) {
			String coachNo = seatDoc.getString("coach_no");
			String seatNo = seatDoc.getString("seat_no");
			String code = seatCode(coachNo, seatNo);

			// Atomic push only if seat not already present in booked array
			Document updated = availCol.findOneAndUpdate(
					Filters.and(Filters.eq(TRAIN_ID, availFilter.getString(TRAIN_ID)),
							Filters.eq(DATE, availFilter.getString(DATE)), Filters.not(Filters.in("booked", code))),
					Updates.push("booked", code), options);

			if (updated != null) {
				// success: set passenger seat and mark it in our local bookedSet and
				// confirmedAllocated list
				p.setSeatMetaData(new SeatMetaData(classType, coachNo, (byte) Integer.parseInt(seatNo)));
				bookedSet.add(code);
				confirmedAllocated.add(code);
				return true;
			}
			// if updated == null, some other concurrent process grabbed it — try next
			// candidate
		}
		return false;
	}

	/**
	 * Allocate RAC entries for the remaining passengers (ordered list). Uses atomic
	 * push to 'rac' only if rac length < racLimit. Returns list of passengers still
	 * unallocated after RAC allocation.
	 */
	private List<Passenger> allocateRac(List<Passenger> remaining, MongoCollection<Document> availCol, String trainId,
			String journeyDate, int racLimit, List<String> racAllocated, String classType) {

		List<Passenger> stillUnallocated = new ArrayList<>();

		FindOneAndUpdateOptions options = new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER);

		for (Passenger p : remaining) {
			// Attempt atomic push ONLY IF current rac length < racLimit using Filters.where
			// We'll compute the next RAC index based on returned document's rac size after
			// push, but to get index after push,
			// we can read rac list before push (not strictly necessary) — instead we push
			// and then read doc to compute index.
			Document updated = availCol
					.findOneAndUpdate(
							Filters.and(Filters.eq(TRAIN_ID, trainId), Filters.eq(DATE, journeyDate),
									Filters.expr(new Document("$lt",
											Arrays.asList(new Document("$size", "$rac"), racLimit)))),
							Updates.push("rac", "RAC"), // push a placeholder; we'll determine its index by reading
														// updated doc
							options);

			if (updated != null) {
				// compute index of the newly added RAC by reading updated.rac size (it contains
				// the newly added placeholder)
				List<String> racListNow = updated.getList("rac", String.class, new ArrayList<>());
				int idx = racListNow.size(); // 1-based
				String racCode = "RAC-" + idx;

				// replace last pushed placeholder value with the actual code RAC-idx
				// We'll use findOneAndUpdate to replace the last element
				// Remove last element then push actual code in a safe atomic manner:
				// Simpler approach: set rac[idx-1] to racCode — using positional update $set
				// with index
				// Build update: Updates.set("rac."+(idx-1), racCode);
				Document confirm = availCol.findOneAndUpdate(
						Filters.and(Filters.eq(TRAIN_ID, trainId), Filters.eq(DATE, journeyDate),
								Filters.eq("rac." + (idx - 1), "RAC")),
						Updates.set("rac." + (idx - 1), racCode), options);

				// If confirm == null, it's unexpected but continue — the rac code might already
				// be replaced by another process
				// Set passenger metadata to indicate RAC
				p.setSeatMetaData(new SeatMetaData(classType, "RAC", (byte) idx));
				p.setTicketStatus("RAC");
				racAllocated.add(racCode);
			} else {
				// no RAC slot available
				stillUnallocated.add(p);
			}
		}
		return stillUnallocated;
	}

	/**
	 * Allocate WL entries similarly to RAC.
	 */
	private List<Passenger> allocateWl(List<Passenger> remaining, MongoCollection<Document> availCol, String trainId,
			String journeyDate, int wlLimit, List<String> wlAllocated, String classType) {

		List<Passenger> stillUnallocated = new ArrayList<>();

		Document availFilter = new Document(TRAIN_ID, trainId).append(DATE, journeyDate);
		FindOneAndUpdateOptions options = new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER);

		for (Passenger p : remaining) {

			Document updated = availCol
					.findOneAndUpdate(
							Filters.and(Filters.eq(TRAIN_ID, trainId), Filters.eq(DATE, journeyDate),
									Filters.expr(
											new Document("$lt", Arrays.asList(new Document("$size", "$wl"), wlLimit)))),
							Updates.push("wl", "WL"), options);

			if (updated != null) {
				List<String> wlNow = updated.getList("wl", String.class, new ArrayList<>());
				int idx = wlNow.size();
				String wlCode = "WL-" + idx;

				// set actual code at the last position atomically (similar approach as RAC)
				Document confirm = availCol
						.findOneAndUpdate(
								Filters.and(Filters.eq(TRAIN_ID, trainId), Filters.eq(DATE, journeyDate),
										Filters.eq("wl." + (idx - 1), "WL")),
								Updates.set("wl." + (idx - 1), wlCode), options);

				p.setSeatMetaData(new SeatMetaData(classType, "WL", (byte) idx));
				p.setTicketStatus("WL");
				wlAllocated.add(wlCode);
			} else {
				stillUnallocated.add(p);
			}
		}

		return stillUnallocated;
	}

	/**
	 * Rollback allocations by removing the allocated seat codes from booked/rac/wl
	 * arrays.
	 */
	private void rollback(MongoCollection<Document> availCol, Document availFilter, List<String> confirmedAllocated,
			List<String> racAllocated, List<String> wlAllocated) {

		if ((confirmedAllocated != null && !confirmedAllocated.isEmpty())) {
			availCol.updateOne(
					Filters.and(Filters.eq(TRAIN_ID, availFilter.getString(TRAIN_ID)),
							Filters.eq(DATE, availFilter.getString(DATE))),
					Updates.pullAll("booked", confirmedAllocated));
		}

		if ((racAllocated != null && !racAllocated.isEmpty())) {
			availCol.updateOne(Filters.and(Filters.eq(TRAIN_ID, availFilter.getString(TRAIN_ID)),
					Filters.eq(DATE, availFilter.getString(DATE))), Updates.pullAll("rac", racAllocated));
		}

		if ((wlAllocated != null && !wlAllocated.isEmpty())) {
			availCol.updateOne(Filters.and(Filters.eq(TRAIN_ID, availFilter.getString(TRAIN_ID)),
					Filters.eq(DATE, availFilter.getString(DATE))), Updates.pullAll("wl", wlAllocated));
		}
	}

	public void storeBookingStateInDB(BookingData bookingData, Set<Passenger> associatedPassengers, String mobile,
			String email) {

		try (MongoClient mongoClient = MongoClients.create(DB_PROPERTIES.getProperty(MONGO_DB_CONNECTION_URL, ""))) {

			MongoDatabase mongoDatabase = mongoClient.getDatabase(DB_PROPERTIES.getProperty(TRAIN_BOOKING_DB_NAME, ""));

			MongoCollection<Document> bookingStateCollection = mongoDatabase
					.getCollection(TrainBookingDatabase.BOOKING_STATE.name());

			List<Document> passengerList = new ArrayList<>();
			if (associatedPassengers != null) {

				for (Passenger passenger : associatedPassengers) {

					StringBuilder ticketBookingStatus = new StringBuilder();

					ticketBookingStatus
							.append(passenger.getTicketStatus() + "/" + passenger.getSeatMetaData().getSeatNumber());

					Document passengerDocument = new Document(PassengerCollection.NAME.name(), passenger.getName())
							.append(PassengerCollection.AGE.name(), passenger.getAge())
							.append(PassengerCollection.GENDER.name(), passenger.getGender())
							.append(PassengerCollection.CLASS_TYPE.name(), bookingData.getClassType())
							.append(PassengerCollection.COACH_NO.name(), passenger.getSeatMetaData().getCoachNo())
							.append(PassengerCollection.CURRENT_STATUS.name(), ticketBookingStatus.toString())
							.append(PassengerCollection.OPTED_AUTO_UPGRADE.name(), passenger.isAutoUpgrade());

					passengerList.add(passengerDocument);
				}

			}

			bookingStateCollection.insertOne(new Document(BookingState.USER_NAME.name(), bookingData.getUserName())
					.append(BookingState.BOOKING_MOBILE_NO.name(), mobile)
					.append(BookingState.BOOKING_EMAIL_ID.name(), email)
					.append(BookingState.TRAVEL_DATE.name(), bookingData.getTravelDate())
					.append(BookingState.TRAIN_ID.name(), bookingData.getTrainId())
					.append(BookingState.TRAIN_NAME.name(), bookingData.getTraiName())
					.append(BookingState.SOURCE.name(), bookingData.getSource())
					.append(BookingState.DESTINATION.name(), bookingData.getDestination())
					.append(BookingState.CLASS_TYPE.name(), bookingData.getClassType())
					.append(BookingState.TOTAL_TICKET_FARE.name(), bookingData.getTotalFare())
					.append(BookingState.PNR_NUMBER.name(), bookingData.getPnrNo())
					.append(BookingState.TRANSACTION_ID.name(), bookingData.getTransactionId())
					.append(BookingState.BOOKING_STATUS.name(), bookingData.getBookingStatus())
					.append(BookingState.TRANSACTION_STATUS.name(), bookingData.getTransactionStatus())
					.append(BookingState.ASSOCIATED_PASSENGER.name(), passengerList));

		}

	}

	public Ticket getTicketByPNR(String pnrNumber) {

		Ticket matchedTicket = null;

		try (MongoClient mongoClient = MongoClients.create(DB_PROPERTIES.getProperty(MONGO_DB_CONNECTION_URL, ""))) {

			MongoDatabase mongoDatabase = mongoClient.getDatabase(DB_PROPERTIES.getProperty(TRAIN_BOOKING_DB_NAME, ""));

			MongoCollection<Document> bookingStateCollection = mongoDatabase
					.getCollection(TrainBookingDatabase.BOOKING_STATE.name());

			Document matchedTicketDocument = bookingStateCollection
					.find(Filters.eq(BookingState.PNR_NUMBER.name(), pnrNumber)).first();

			if (matchedTicketDocument != null) {

				// ✅ 1. Read Passenger Array
				Set<Passenger> associatedPassenger = new HashSet<>();

				List<Document> passengerDocs = (List<Document>) matchedTicketDocument
						.get(BookingState.ASSOCIATED_PASSENGER.name());

				if (passengerDocs != null) {
					for (Document pDoc : passengerDocs) {

						String name = pDoc.getString(PassengerCollection.NAME.name());
						byte age = ((Number) pDoc.get(PassengerCollection.AGE.name())).byteValue();
						char gender = pDoc.getString(PassengerCollection.GENDER.name()).charAt(0);
						String classType = pDoc.getString(PassengerCollection.CLASS_TYPE.name());
						String coachNo = pDoc.getString(PassengerCollection.COACH_NO.name());
						String currentStatus = pDoc.getString(PassengerCollection.CURRENT_STATUS.name());
						boolean autoUpgrade = pDoc.getBoolean(PassengerCollection.OPTED_AUTO_UPGRADE.name(), false);

						// ✅ Create Passenger
						Passenger passenger = new Passenger(name, classType, age, gender, "", // nationality not stored
																								// in DB
								autoUpgrade);

						passenger.setTicketStatus(currentStatus);

						SeatMetaData seat = new SeatMetaData(classType, coachNo,
								Byte.parseByte(currentStatus.split("/")[1]));
						passenger.setSeatMetaData(seat);

						associatedPassenger.add(passenger);
					}
				}

				// ✅ 2. Create Ticket Object
				matchedTicket = new Ticket(matchedTicketDocument.getString(BookingState.TRAVEL_DATE.name()), // bookingDate
						matchedTicketDocument.getString(BookingState.TRAIN_ID.name()),
						matchedTicketDocument.getString(BookingState.TRAIN_NAME.name()),
						matchedTicketDocument.getString(BookingState.CLASS_TYPE.name()),
						matchedTicketDocument.getString(BookingState.SOURCE.name()),
						matchedTicketDocument.getString(BookingState.DESTINATION.name()),
						matchedTicketDocument.getString(BookingState.PNR_NUMBER.name()),
						matchedTicketDocument.getString(BookingState.TRANSACTION_ID), associatedPassenger,
						matchedTicketDocument.getDouble(BookingState.TOTAL_TICKET_FARE.name()));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return matchedTicket;
	}

	public List<BookingData> getCurrentUserBooking(String userName) {

		List<BookingData> allBooking = new ArrayList<>();
		try (MongoClient mongoClient = MongoClients.create(DB_PROPERTIES.getProperty(MONGO_DB_CONNECTION_URL, ""))) {

			MongoDatabase mongoDatabase = mongoClient.getDatabase(DB_PROPERTIES.getProperty(TRAIN_BOOKING_DB_NAME, ""));

			MongoCollection<Document> bookingStateCollection = mongoDatabase
					.getCollection(TrainBookingDatabase.BOOKING_STATE.name());

			FindIterable<Document> allBookingDocument = bookingStateCollection
					.find(Filters.and(Filters.eq(BookingState.USER_NAME.name(), userName),
							Filters.eq(BookingState.BOOKING_STATUS.name(), Status.SUCCESS.name())));

			for (Document eachBooking : allBookingDocument) {

				BookingData bookingData = new BookingData();
				bookingData.setUserName(userName);
				bookingData.setTrainId(eachBooking.getString(BookingState.TRAIN_ID.name()));
				bookingData.setTraiName(eachBooking.getString(BookingState.TRAIN_NAME.name()));
				bookingData.setClassType(eachBooking.getString(BookingState.CLASS_TYPE.name()));
				bookingData.setBookingStatus(eachBooking.getString(BookingState.BOOKING_STATUS.name()));
				bookingData.setSource(eachBooking.getString(BookingState.SOURCE.name()));
				bookingData.setDestination(eachBooking.getString(BookingState.DESTINATION.name()));
				bookingData.setPnrNo(eachBooking.getString(BookingState.PNR_NUMBER.name()));
				bookingData.setTotalFare(eachBooking.getDouble(BookingState.TOTAL_TICKET_FARE.name()));
				bookingData.setTransactionId(eachBooking.getString(BookingState.TRANSACTION_ID.name()));
				bookingData.setTransactionStatus(eachBooking.getString(BookingState.TRANSACTION_STATUS.name()));
				bookingData.setTravelDate(eachBooking.getString(BookingState.TRAVEL_DATE.name()));

				List<Passenger> associatedPassenger = new ArrayList<>();
				List<Document> passengerDocs = (List<Document>) eachBooking
						.get(BookingState.ASSOCIATED_PASSENGER.name());

				for (Document pDoc : passengerDocs) {
					String name = pDoc.getString(PassengerCollection.NAME.name());
					byte age = ((Number) pDoc.get(PassengerCollection.AGE.name())).byteValue();
					char gender = pDoc.getString(PassengerCollection.GENDER.name()).charAt(0);
					String classType = pDoc.getString(PassengerCollection.CLASS_TYPE.name());
					String coachNo = pDoc.getString(PassengerCollection.COACH_NO.name());
					String currentStatus = pDoc.getString(PassengerCollection.CURRENT_STATUS.name());
					boolean autoUpgrade = pDoc.getBoolean(PassengerCollection.OPTED_AUTO_UPGRADE.name(), false);

					// ✅ Create Passenger
					Passenger passenger = new Passenger(name, classType, age, gender, "", // nationality not stored
																							// in DB
							autoUpgrade);

					passenger.setTicketStatus(currentStatus);
					System.out.println("Current Status :" + currentStatus);

					if (!currentStatus.equals("CAN")) {
						SeatMetaData seat = new SeatMetaData(classType, coachNo,
								Byte.parseByte(currentStatus.split("/")[1]));
						passenger.setSeatMetaData(seat);
					} else {
						SeatMetaData seat = new SeatMetaData(classType, coachNo, (byte) -1);
						passenger.setSeatMetaData(seat);
					}

					associatedPassenger.add(passenger);

				}
				bookingData.setAssociatedPassenger(associatedPassenger);
				allBooking.add(bookingData);

			}

		}
		return allBooking;
	}

	public Queue<Passenger> findRacAndWlPassengerIfExists(String pnrNumber,
			Map<String, Passenger> cancelledPassengerList) {

		// PriorityQueue will use Passenger.compareTo()
		Queue<Passenger> racAndWlQueue = new PriorityQueue<>();

		try (MongoClient mongoClient = MongoClients.create(DB_PROPERTIES.getProperty(MONGO_DB_CONNECTION_URL, ""))) {

			MongoDatabase mongoDatabase = mongoClient.getDatabase(DB_PROPERTIES.getProperty(TRAIN_BOOKING_DB_NAME, ""));

			MongoCollection<Document> bookingCollection = mongoDatabase
					.getCollection(TrainBookingDatabase.BOOKING_STATE.name());

			// Find booking by PNR
			Document bookingDoc = bookingCollection.find(Filters.eq(BookingState.PNR_NUMBER.name(), pnrNumber)).first();

			if (bookingDoc == null) {
				return racAndWlQueue;
			}

			String classType = bookingDoc.getString(BookingState.CLASS_TYPE.name());

			String trainName = bookingDoc.getString(BookingState.TRAIN_NAME.name());

			String trainId = bookingDoc.getString(BookingState.TRAIN_ID.name());

			String travelDate = bookingDoc.getString(BookingState.TRAVEL_DATE.name());

			FindIterable<Document> allBookings = bookingCollection
					.find(Filters.and(Filters.eq(BookingState.TRAVEL_DATE.name(), travelDate),
							Filters.eq(BookingState.TRAIN_NAME.name(), trainName),
							Filters.eq(BookingState.TRAIN_ID.name(), trainId),
							Filters.eq(BookingState.CLASS_TYPE.name(), classType)));

			for (Document eachBooking : allBookings) {
				List<Document> passengerDocs = eachBooking.getList(BookingState.ASSOCIATED_PASSENGER.name(),
						Document.class);

				if (passengerDocs == null || passengerDocs.isEmpty()) {
					return racAndWlQueue;
				}

				for (Document pDoc : passengerDocs) {

					String currentStatus = pDoc.getString(PassengerCollection.CURRENT_STATUS.name());

					System.out.println("Current Status : " + currentStatus);
					// We only want RAC or WL
					if (currentStatus == null
							|| (!currentStatus.startsWith("RAC") && !currentStatus.startsWith("WL"))) {
						continue;
					}

					String name = pDoc.getString(PassengerCollection.NAME.name());
					byte age = pDoc.getInteger(PassengerCollection.AGE.name()).byteValue();
					char gender = pDoc.getString(PassengerCollection.GENDER.name()).charAt(0);
					String coachNo = pDoc.getString(PassengerCollection.COACH_NO.name());
					boolean autoUpgrade = pDoc.getBoolean(PassengerCollection.OPTED_AUTO_UPGRADE.name(), false);
					String currPassengerPnrNumber = eachBooking.getString(BookingState.PNR_NUMBER.name());

					// Extract seat / WL / RAC number (RAC/2, WL/5)
					byte position = Byte.parseByte(currentStatus.split("/")[1]);

					Passenger passenger = new Passenger(name, null, // preference (not stored in DB)
							age, gender, null, // nationality (not stored in DB)
							autoUpgrade);

					passenger.setTicketStatus(currentStatus.startsWith("RAC") ? "RAC" : "WL");

					passenger.setSeatMetaData(new SeatMetaData(classType, coachNo, position));
					passenger.setPnrNumber(currPassengerPnrNumber);

					racAndWlQueue.add(passenger);
				}

			}
			System.out.println("RAC and WaitingList Passenger " + racAndWlQueue);

			if (!racAndWlQueue.isEmpty()) {
				promoteRacAndWLPassengerIfExists(pnrNumber, classType, trainName, trainId, racAndWlQueue,
						cancelledPassengerList);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return racAndWlQueue;
	}

	private void promoteRacAndWLPassengerIfExists(String pnrNumber, String classType, String trainName, String trainId,
			Queue<Passenger> racAndWlQueue, Map<String, Passenger> cancelledPassengerList) {

		try (MongoClient mongoClient = MongoClients.create(DB_PROPERTIES.getProperty(MONGO_DB_CONNECTION_URL, ""))) {

			ClientSession session = mongoClient.startSession();
			session.startTransaction();

			try {

				MongoDatabase db = mongoClient.getDatabase(DB_PROPERTIES.getProperty(TRAIN_BOOKING_DB_NAME, ""));

				MongoCollection<Document> bookingCollection = db
						.getCollection(TrainBookingDatabase.BOOKING_STATE.name());

				MongoCollection<Document> seatAvailabilityCollection = db
						.getCollection(TrainBookingDatabase.SEAT_AVAILABILITY.name());

				for (Map.Entry<String, Passenger> eachPassenger : cancelledPassengerList.entrySet()) {

					Passenger freedSeatPassenger = eachPassenger.getValue();

					System.out.println("Free Seat Passenger : " + freedSeatPassenger);
					Passenger promoteCandidate = racAndWlQueue.poll();
					System.out.println("Promote Passenger " + promoteCandidate);

					SeatMetaData seat = freedSeatPassenger.getSeatMetaData();

					String ticketStatus = promoteCandidate.getTicketStatus();

					// 🔐 Atomic promotion
					Document updatedPassenger = bookingCollection.findOneAndUpdate(session,
							Filters.and(Filters.eq("PNR_NUMBER", pnrNumber),
									Filters.elemMatch("ASSOCIATED_PASSENGER",
											Filters.and(Filters.eq("NAME", promoteCandidate.getName()),
													Filters.eq("CURRENT_STATUS", promoteCandidate.getTicketStatus())))),
							Updates.set("ASSOCIATED_PASSENGER.$.CURRENT_STATUS", freedSeatPassenger.getTicketStatus()),
							new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));

					// If null → someone else already promoted this passenger
					if (updatedPassenger == null) {
						continue;
					}
					System.out.println("Updated Passenger " + updatedPassenger.toJson());

					// Update seat availability atomically
					String bookedSeat = seat.getCoachNo() + "-" + seat.getSeatNumber();

					if (ticketStatus.startsWith("RAC")) {
						seatAvailabilityCollection
								.updateOne(
										session, Filters.eq(TRAIN_ID, trainId), Updates
												.combine(
														Updates.pull("rac",
																"RAC-" + promoteCandidate.getSeatMetaData()
																		.getSeatNumber()),
														Updates.push("booked", bookedSeat)));
					} else {
						seatAvailabilityCollection
								.updateOne(
										session, Filters.eq(TRAIN_ID, trainId), Updates
												.combine(
														Updates.pull("wl",
																"WL-" + promoteCandidate.getSeatMetaData()
																		.getSeatNumber()),
														Updates.push("booked", bookedSeat)));
					}

				}

				session.commitTransaction();

			} catch (Exception e) {
				session.abortTransaction();
				throw e;
			} finally {
				session.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void cancelPassengerTickets(String pnrNumber, Map<String, Passenger> passengersToCancel) {
		try (MongoClient mongoClient = MongoClients.create(DB_PROPERTIES.getProperty(MONGO_DB_CONNECTION_URL, ""))) {

			MongoDatabase mongoDatabase = mongoClient.getDatabase(DB_PROPERTIES.getProperty(TRAIN_BOOKING_DB_NAME, ""));

			MongoCollection<Document> bookingCollection = mongoDatabase
					.getCollection(TrainBookingDatabase.BOOKING_STATE.name());

			// Find booking by PNR
			Document bookingDoc = bookingCollection.find(Filters.eq(BookingState.PNR_NUMBER.name(), pnrNumber)).first();

			if (bookingDoc == null) {
				System.out.println("No Booking Available For Paritcular Pnr Number " + pnrNumber);
				return;
			}

			String trainId = bookingDoc.getString(BookingState.TRAIN_ID.name());
			String travelDate = bookingDoc.getString(BookingState.TRAVEL_DATE.name());

			List<Document> passengerDocs = bookingDoc.getList(BookingState.ASSOCIATED_PASSENGER.name(), Document.class);

			if (passengerDocs == null || passengerDocs.isEmpty()) {
				System.out.println("No Passenger To Shown for Pnr Number " + pnrNumber);
				return;
			}

			for (Document currentPassenger : passengerDocs) {

				String currentStatus = currentPassenger.getString(PassengerCollection.CURRENT_STATUS.name());

				if (passengersToCancel.containsKey(currentStatus)) {
					currentPassenger.put(PassengerCollection.CURRENT_STATUS.name(), "CAN");
					currentPassenger.put(PassengerCollection.CANCELLED_AT.name(), System.currentTimeMillis());
				}

			}

			// Persist changes
			bookingCollection.updateOne(Filters.eq(BookingState.PNR_NUMBER.name(), pnrNumber),
					Updates.set(BookingState.ASSOCIATED_PASSENGER.name(), passengerDocs));

			// remove Booking in Db
			removeBookingInDb(passengersToCancel, trainId, travelDate);

		}

	}

	private void removeBookingInDb(Map<String, Passenger> passengersToCancel, String trainId, String travelDate) {

		try (MongoClient mongoClient = MongoClients.create(DB_PROPERTIES.getProperty(MONGO_DB_CONNECTION_URL, ""))) {

			MongoDatabase mongoDatabase = mongoClient.getDatabase(DB_PROPERTIES.getProperty(TRAIN_BOOKING_DB_NAME, ""));

			MongoCollection<Document> seatAvailabiltyCollection = mongoDatabase
					.getCollection(TrainBookingDatabase.SEAT_AVAILABILITY.name());

			List<Bson> seatPullUpdates = new ArrayList<>();

			for (Passenger passenger : passengersToCancel.values()) {

				SeatMetaData seat = passenger.getSeatMetaData();
				String ticketStatus = passenger.getTicketStatus(); // CNF / RAC / WL

				switch (ticketStatus) {

				case "CNF" -> {
					String seatValue = seat.getCoachNo() + "-" + seat.getSeatNumber();
					seatPullUpdates.add(Updates.pull("booked", seatValue));
				}
				case "RAC" -> {
					String racValue = "RAC-" + seat.getSeatNumber();
					seatPullUpdates.add(Updates.pull("rac", racValue));
				}
				case "WL" -> {
					String wlValue = "WL-" + seat.getSeatNumber();
					seatPullUpdates.add(Updates.pull("wl", wlValue));
				}
				default -> {
					System.out.println("Different Passenger Status" + ticketStatus);
				}
				}

			}

			if (!seatPullUpdates.isEmpty()) {

				seatAvailabiltyCollection.updateOne(
						Filters.and(Filters.eq(TRAIN_ID, trainId), Filters.eq(DATE, travelDate)),
						Updates.combine(seatPullUpdates));
			}

		}

	}

}
