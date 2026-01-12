package com.samprakash.repository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import com.samprakash.basemodel.Status;
import com.samprakash.basemodel.TrainBookingDatabase;
import com.samprakash.basemodel.UserCollection;
import com.samprakash.basemodel.Users;
import com.samprakash.baseviewmodel.Hashing;
import com.samprakash.exception.SeatNotAvailableException;
import com.samprakash.paymentmodel.Passenger;
import com.samprakash.paymentmodel.PaymentsCollection;
import com.samprakash.paymentmodel.TransactionPurpose;
import com.samprakash.paymentview.PaymentGateway;
import com.samprakash.profilemodel.TransactionData;
import com.samprakash.ticketbookmodel.BookingData;
import com.samprakash.ticketbookmodel.BookingState;
import com.samprakash.ticketbookmodel.PassengerCollection;
import com.samprakash.ticketbookmodel.SeatMetaData;
import com.samprakash.ticketbookmodel.Ticket;
import com.samprakash.ticketbookmodel.TicketStatus;

public class DataBaseConnector {

	private static final DataBaseConnector DATA_BASE_CONNECTOR;

	private final Properties DB_PROPERTIES;

	private final String MONGO_DB_CONNECTION_URL, TRAIN_BOOKING_DB_NAME;
	private final String TRAIN_ID, DATE, TRAIN_NAME;

	static {

		DATA_BASE_CONNECTOR = new DataBaseConnector();
	}

	static class SlotCount {
		Queue<String> freedCnfSeats = new LinkedList<>();
		int freedRac;
		int freedWL;
	}

	static class RacWlQueues {
		Queue<Passenger> racQueue = new PriorityQueue<>();
		Queue<Passenger> wlQueue = new PriorityQueue<>();
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

	public Status addUser(Users newUser) {

		Status addStatus = Status.FAILURE;
		if (newUser == null) {
			System.out.println("Provided User Object is Null");
			return addStatus;
		}

		if (isUserAlreadyExist(newUser.userName())) {
			System.out.println("User Already Exist");
			addStatus = Status.ALREADY_EXIST;
			return addStatus;
		}

		if (isPropertyValueAlreadyUsedByAnotherUser(newUser.userName(), newUser.email(), UserCollection.EMAIL)) {
			addStatus = Status.EMAIL_ID_ALREADY_USED;
			return addStatus;
		}

		if (isPropertyValueAlreadyUsedByAnotherUser(newUser.userName(), newUser.contactNo(),
				UserCollection.CONTACT_NO)) {
			addStatus = Status.CONTACT_NO_ALREADY_USED;
			return addStatus;
		}

		try (MongoClient mongoClient = MongoClients.create(DB_PROPERTIES.getProperty(MONGO_DB_CONNECTION_URL, ""))) {

			MongoDatabase trainBookingDataBase = mongoClient
					.getDatabase(DB_PROPERTIES.getProperty(TRAIN_BOOKING_DB_NAME, ""));

			MongoCollection<Document> allUserDocument = trainBookingDataBase
					.getCollection(TrainBookingDatabase.USERS.name());
			Document newUserDocument = new Document(UserCollection.FULL_NAME.name(), newUser.fullName())
					.append(UserCollection.EMAIL.name(), newUser.email())
					.append(UserCollection.CONTACT_NO.name(), newUser.contactNo())
					.append(UserCollection.USER_NAME.name(), newUser.userName())
					.append(UserCollection.HASHED_PASSWORD.name(), newUser.hashedPassword());

			allUserDocument.insertOne(newUserDocument);

			addStatus = Status.SUCCESS;
		}

		return addStatus;

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

				Document classInfo = layoutDoc.get("class_info", Document.class);
				Document coachesDoc = layoutDoc.get("coaches", Document.class);

				Document seatAvail = seatAvailCol
						.find(Filters.and(Filters.eq(TRAIN_ID, trainID), Filters.eq(DATE, journeyDate))).first();

				Document classesAvail = seatAvail != null ? seatAvail.get("classes", Document.class) : null;

				JSONObject trainJson = new JSONObject();

				for (String coachClass : coachesDoc.keySet()) {

					Document classLimits = classInfo.get(coachClass, Document.class);
					int racLimit = classLimits.getInteger("rac_limit", 0);
					int wlLimit = classLimits.getInteger("wl_limit", 0);

					Document classAvail = classesAvail != null ? classesAvail.get(coachClass, Document.class) : null;

					List<String> booked = classAvail != null
							? classAvail.getList("booked", String.class, new ArrayList<>())
							: new ArrayList<>();

					List<String> rac = classAvail != null ? classAvail.getList("rac", String.class, new ArrayList<>())
							: new ArrayList<>();

					List<String> wl = classAvail != null ? classAvail.getList("wl", String.class, new ArrayList<>())
							: new ArrayList<>();

					int availableRAC = Math.max(0, racLimit - rac.size());
					int availableWL = Math.max(0, wlLimit - wl.size());

					JSONArray coachArray = new JSONArray();
					List<Document> coachList = (List<Document>) coachesDoc.get(coachClass);

					for (Document coach : coachList) {
						String coachNo = coach.getString("coach_no");
						int availableSeats = 0;

						for (Document seat : coach.getList("seats", Document.class)) {
							String seatId = coachNo + "-" + seat.getString("seat_no");
							if (!booked.contains(seatId)) {
								availableSeats++;
							}
						}

						if (availableSeats > 0) {
							coachArray.put(
									new JSONObject().put("coach_no", coachNo).put("available_seats", availableSeats));
						}
					}

					if (!coachArray.isEmpty()) {
						trainJson.put(coachClass, coachArray);
					} else if (availableRAC > 0) {
						trainJson.put(coachClass,
								new JSONObject().put("status", "RAC").put("available_seats", availableRAC));
					} else if (availableWL > 0) {
						trainJson.put(coachClass,
								new JSONObject().put("status", "WL").put("available_seats", availableWL));
					} else {
						trainJson.put(coachClass, new JSONObject().put("status", "NOT_AVAILABLE"));
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
				availDoc = new Document(TRAIN_ID, trainId).append(DATE, journeyDate).append("classes", new Document());
				availCol.insertOne(availDoc);
				availDoc = availCol.find(availFilter).first();
			}

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
			Document classes = availDoc.get("classes", Document.class);
			Document classDoc = classes.get(classType, Document.class);

			if (classDoc == null) {
				classDoc = new Document("booked", new ArrayList<>()).append("rac", new ArrayList<>()).append("wl",
						new ArrayList<>());
				classes.put(classType, classDoc);
				availCol.updateOne(availFilter, Updates.set("classes." + classType, classDoc));
			}

			List<String> bookedList = classDoc.getList("booked", String.class);
			List<String> racList = classDoc.getList("rac", String.class);
			List<String> wlList = classDoc.getList("wl", String.class);

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
					rollback(availCol, availFilter, confirmedAllocated, racAllocated, wlAllocated, classType);
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
				rollback(availCol, availFilter, confirmedAllocated, racAllocated, wlAllocated, classType);
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
							Filters.eq(DATE, availFilter.getString(DATE)),
							Filters.not(Filters.in("classes." + classType + ".booked", code))),
					Updates.addToSet("classes." + classType + ".booked", code), options);

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
											Arrays.asList(new Document("$size", "$classes." + classType + ".rac"),
													racLimit)))),
							Updates.addToSet("classes." + classType + ".rac", "RAC"), // push a placeholder; we'll
																						// determine
																						// its index by reading
							// updated doc
							options);

			if (updated != null) {
				// compute index of the newly added RAC by reading updated.rac size (it contains
				// the newly added placeholder)
				Document classes = updated.get("classes", Document.class);
				Document classDoc = classes.get(classType, Document.class);
				List<String> racListNow = classDoc.getList("rac", String.class, new ArrayList<>());
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
								Filters.eq("classes." + classType + ".rac." + (idx - 1), "RAC")),
						Updates.set("classes." + classType + ".rac." + (idx - 1), racCode), options);

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
									Filters.expr(new Document("$lt",
											Arrays.asList(new Document("$size", "$classes." + classType + ".wl"),
													wlLimit)))),
							Updates.addToSet("classes." + classType + ".wl", "WL"), options);

			if (updated != null) {
				Document classes = updated.get("classes", Document.class);
				Document classDoc = classes.get(classType, Document.class);
				List<String> wlNow = classDoc.getList("wl", String.class, new ArrayList<>());
				int idx = wlNow.size();
				String wlCode = "WL-" + idx;

				// set actual code at the last position atomically (similar approach as RAC)
				Document confirm = availCol.findOneAndUpdate(
						Filters.and(Filters.eq(TRAIN_ID, trainId), Filters.eq(DATE, journeyDate),
								Filters.eq("classes." + classType + ".wl." + (idx - 1), "WL")),
						Updates.set("classes." + classType + ".wl." + (idx - 1), wlCode), options);

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
			List<String> racAllocated, List<String> wlAllocated, String classType) {

		if ((confirmedAllocated != null && !confirmedAllocated.isEmpty())) {
			availCol.updateOne(
					Filters.and(Filters.eq(TRAIN_ID, availFilter.getString(TRAIN_ID)),
							Filters.eq(DATE, availFilter.getString(DATE))),
					Updates.pullAll("classes." + classType + ".booked", confirmedAllocated));
		}

		if ((racAllocated != null && !racAllocated.isEmpty())) {
			availCol.updateOne(
					Filters.and(Filters.eq(TRAIN_ID, availFilter.getString(TRAIN_ID)),
							Filters.eq(DATE, availFilter.getString(DATE))),
					Updates.pullAll("classes." + classType + ".rac", racAllocated));
		}

		if ((wlAllocated != null && !wlAllocated.isEmpty())) {
			availCol.updateOne(
					Filters.and(Filters.eq(TRAIN_ID, availFilter.getString(TRAIN_ID)),
							Filters.eq(DATE, availFilter.getString(DATE))),
					Updates.pullAll("classes." + classType + ".wl", wlAllocated));
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

	@SuppressWarnings("unchecked")
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

	@SuppressWarnings("unchecked")
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

	public void cancelAndPromoteTickets(String pnrNumber, Map<String, Passenger> passengersToCancel) {

		try (MongoClient client = MongoClients.create(DB_PROPERTIES.getProperty(MONGO_DB_CONNECTION_URL))) {

			ClientSession session = client.startSession();
			session.startTransaction();

			try {
				MongoDatabase db = client.getDatabase(DB_PROPERTIES.getProperty(TRAIN_BOOKING_DB_NAME));

				MongoCollection<Document> bookingCol = db.getCollection(TrainBookingDatabase.BOOKING_STATE.name());

				MongoCollection<Document> seatAvailCol = db
						.getCollection(TrainBookingDatabase.SEAT_AVAILABILITY.name());

				MongoCollection<Document> seatLayoutCol = db.getCollection(TrainBookingDatabase.SEAT_LAYOUT.name());

				Document booking = bookingCol.find(session, Filters.eq("PNR_NUMBER", pnrNumber)).first();

				if (booking == null)
					return;

				String trainId = booking.getString("TRAIN_ID");
				String trainName = booking.getString("TRAIN_NAME");
				String travelDate = booking.getString("TRAVEL_DATE");
				String classType = booking.getString("CLASS_TYPE");

				// 1️⃣ Load seat layout limits
				Document layout = seatLayoutCol.find(Filters.eq("train_id", trainId)).first();

				Document classCfg = layout.get("class_info", Document.class).get(classType, Document.class);

				int cnfCapacity = layout.get("coaches", Document.class).getList(classType, Document.class).stream()
						.mapToInt(c -> c.getList("seats", Document.class).size()).sum();

				int racLimit = classCfg.getInteger("rac_limit");

				// 2️⃣ Cancel passengers + free slots
				SlotCount slots = cancelPassengersAndFreeSeats(session, bookingCol, seatAvailCol, booking,
						passengersToCancel, trainId, travelDate, classType);

				// 3️⃣ Build queues (global, multi-PNR)
				RacWlQueues queues = buildRacAndWlQueues(session, bookingCol, trainId, trainName, travelDate,
						classType);

				// 4️⃣ recalculateAndPromote correctly
				recalculateAndPromote(session, bookingCol, seatLayoutCol, seatAvailCol, queues, trainId, trainName,
						travelDate, classType, cnfCapacity, racLimit, slots);

				session.commitTransaction();

			} catch (Exception e) {
				session.abortTransaction();
				throw e;
			} finally {
				session.close();
			}
		}
	}

	private SlotCount cancelPassengersAndFreeSeats(ClientSession session, MongoCollection<Document> bookingCol,
			MongoCollection<Document> seatAvailCol, Document booking, Map<String, Passenger> passengersToCancel,
			String trainId, String travelDate, String classType) {

		SlotCount count = new SlotCount();
		List<Document> passengers = booking.getList("ASSOCIATED_PASSENGER", Document.class);

		List<Bson> seatUpdates = new ArrayList<>();
		String base = "classes." + classType + ".";

		for (Document p : passengers) {
			String status = p.getString("CURRENT_STATUS");
			if (!passengersToCancel.containsKey(status))
				continue;

			Passenger cancelled = passengersToCancel.get(status);
			SeatMetaData seat = cancelled.getSeatMetaData();

			if ("CNF".equals(cancelled.getTicketStatus()))
				count.freedCnfSeats.add(seat.getCoachNo() + "-" + seat.getSeatNumber());
			else if ("RAC".equals(cancelled.getTicketStatus()))
				count.freedRac++;
			else if ("WL".equals(cancelled.getTicketStatus())) {
				count.freedWL++;
			}

			p.put("CURRENT_STATUS", "CAN");
			p.put("CANCELLED_AT", System.currentTimeMillis());

			switch (cancelled.getTicketStatus()) {
			case "CNF" ->
				seatUpdates.add(Updates.pull(base + "booked", seat.getCoachNo() + "-" + seat.getSeatNumber()));
			case "RAC" -> seatUpdates.add(Updates.pull(base + "rac", "RAC-" + seat.getSeatNumber()));
			case "WL" -> seatUpdates.add(Updates.pull(base + "wl", "WL-" + seat.getSeatNumber()));
			}
		}

		bookingCol.updateOne(session, Filters.eq("PNR_NUMBER", booking.getString("PNR_NUMBER")),
				Updates.set("ASSOCIATED_PASSENGER", passengers));

		if (!seatUpdates.isEmpty()) {
			seatAvailCol.updateOne(session,
					Filters.and(Filters.eq("train_id", trainId), Filters.eq("date", travelDate)),
					Updates.combine(seatUpdates));
		}

		return count;
	}

	private RacWlQueues buildRacAndWlQueues(ClientSession session, MongoCollection<Document> bookingCol, String trainId,
			String trainName, String travelDate, String classType) {

		RacWlQueues queues = new RacWlQueues();

		FindIterable<Document> bookings = bookingCol.find(session,
				Filters.and(Filters.eq("TRAIN_ID", trainId), Filters.eq("TRAIN_NAME", trainName),
						Filters.eq("TRAVEL_DATE", travelDate), Filters.eq("CLASS_TYPE", classType)));

		for (Document booking : bookings) {
			for (Document p : booking.getList("ASSOCIATED_PASSENGER", Document.class)) {

				String status = p.getString("CURRENT_STATUS");
				if (status == null || status.equals("CAN") || !status.contains("/"))
					continue;

				System.out.println("Status : " + status);
				byte pos = Byte.parseByte(status.split("/")[1]);

				Passenger passenger = new Passenger(p.getString("NAME"), null, p.getInteger("AGE").byteValue(),
						p.getString("GENDER").charAt(0), null, p.getBoolean("OPTED_AUTO_UPGRADE", false));

				passenger.setSeatMetaData(new SeatMetaData(classType, p.getString("COACH_NO"), pos));

				passenger.setPnrNumber(booking.getString("PNR_NUMBER"));

				if (status.startsWith("RAC")) {
					passenger.setTicketStatus("RAC");
					queues.racQueue.add(passenger);
				}

				if (status.startsWith("WL")) {
					passenger.setTicketStatus("WL");
					queues.wlQueue.add(passenger);
				}
			}
		}
		return queues;
	}

	private void promoteWithSeat(ClientSession session, MongoCollection<Document> bookingCol, Passenger p,
			String newStatus, String coach, String seatNo) {

		bookingCol.updateOne(session,
				Filters.and(Filters.eq("PNR_NUMBER", p.getPnrNumber()),
						Filters.elemMatch("ASSOCIATED_PASSENGER",
								Filters.and(Filters.eq("NAME", p.getName()),
										Filters.regex("CURRENT_STATUS", "^" + p.getTicketStatus())))),
				Updates.combine(Updates.set("ASSOCIATED_PASSENGER.$.CURRENT_STATUS", newStatus + "/" + seatNo),
						Updates.set("ASSOCIATED_PASSENGER.$.COACH_NO", coach)));
	}

	private void rebuildSeatAvailabilityFromBookings(ClientSession session, MongoCollection<Document> bookingCol,
			MongoCollection<Document> seatAvailCol, String trainId, String trainName, String travelDate,
			String classType, TicketStatus ticketStatus) {

		// List<String> booked = new ArrayList<>();

		List<String> availableList = new ArrayList<>();

		FindIterable<Document> bookings = bookingCol.find(session,
				Filters.and(Filters.eq("TRAIN_ID", trainId), Filters.eq("TRAIN_NAME", trainName),
						Filters.eq("TRAVEL_DATE", travelDate), Filters.eq("CLASS_TYPE", classType)));

		switch (ticketStatus) {

		case TicketStatus.CNF -> {
			for (Document b : bookings) {
				for (Document p : b.getList("ASSOCIATED_PASSENGER", Document.class)) {

					String s = p.getString("CURRENT_STATUS");
					if (s == null)
						continue;

					if (s.startsWith("CNF"))
						availableList.add(p.getString("COACH_NO") + "-" + s.split("/")[1]);

				}
			}
			seatAvailCol.updateOne(session,
					Filters.and(Filters.eq("train_id", trainId), Filters.eq("date", travelDate)),

					Updates.set("classes." + classType + ".booked", availableList));

		}
		case TicketStatus.RAC -> {
			for (Document b : bookings) {
				for (Document p : b.getList("ASSOCIATED_PASSENGER", Document.class)) {

					String s = p.getString("CURRENT_STATUS");
					if (s == null)
						continue;

					if (s.startsWith("RAC"))
						availableList.add("RAC");

				}
			}
			seatAvailCol.updateOne(session,
					Filters.and(Filters.eq("train_id", trainId), Filters.eq("date", travelDate)),

					Updates.set("classes." + classType + ".rac",
							IntStream.range(0, availableList.size()).mapToObj(i -> "RAC-" + (i + 1)).toList()));
		}
		case TicketStatus.WL -> {
			for (Document b : bookings) {
				for (Document p : b.getList("ASSOCIATED_PASSENGER", Document.class)) {

					String s = p.getString("CURRENT_STATUS");
					if (s == null)
						continue;

					if (s.startsWith("WL"))
						availableList.add("WL");

				}
			}
			seatAvailCol.updateOne(session,
					Filters.and(Filters.eq("train_id", trainId), Filters.eq("date", travelDate)),

					Updates.set("classes." + classType + ".wl",
							IntStream.range(0, availableList.size()).mapToObj(i -> "WL-" + (i + 1)).toList()));
		}
		default -> {
			throw new IllegalArgumentException("Provodied Object is Invalid");
		}

		}

	}

	private void recalculateAndPromote(ClientSession session, MongoCollection<Document> bookingCol,
			MongoCollection<Document> seatLayoutCol, MongoCollection<Document> seatAvailCol, RacWlQueues queues,
			String trainId, String trainName, String travelDate, String classType, int cnfCapacity, int racCapacity,
			SlotCount slots) {

		// 1️⃣ Count current CNF and RAC

		// 2️⃣ Find free CNF seats
		/*
		 * Queue<String> freeCnfSeats = findFreeCnfSeats(session,
		 * bookingCol,seatLayoutCol, trainId, trainName, travelDate, classType);
		 */

		Queue<String> freeCnfSeats = slots.freedCnfSeats;

		System.out.println("Before Apply RAC....");

		System.out.println("Free CNF Seats : " + freeCnfSeats);
		System.out.println("RAC Queues : " + queues.racQueue);
		System.out.println("WL Queues : " + queues.wlQueue);
		// 3️.1 RAC → CNF until CNF full
		while (!freeCnfSeats.isEmpty()) {

			if (queues.racQueue.isEmpty())
				break;
			String seat = freeCnfSeats.poll();

			Passenger p = queues.racQueue.poll();

			promoteWithSeat(session, bookingCol, p, "CNF", seat.split("-")[0], seat.split("-")[1]);
		}
		System.out.println("After Apply RAC....");

		System.out.println("Free CNF Seats : " + freeCnfSeats);
		System.out.println("RAC Queues : " + queues.racQueue);
		System.out.println("WL Queues : " + queues.wlQueue);

		// 3.2 WL -> CNF Until CNF full
		while (!freeCnfSeats.isEmpty()) {

			if (queues.wlQueue.isEmpty())
				break;
			String seat = freeCnfSeats.poll();

			Passenger p = queues.wlQueue.poll();

			promoteWithSeat(session, bookingCol, p, "CNF", seat.split("-")[0], seat.split("-")[1]);
		}
		System.out.println("Before Apply WL....");

		System.out.println("Free CNF Seats : " + freeCnfSeats);
		System.out.println("RAC Queues : " + queues.racQueue);
		System.out.println("WL Queues : " + queues.wlQueue);
		rebuildSeatAvailabilityFromBookings(session, bookingCol, seatAvailCol, trainId, trainName, travelDate,
				classType, TicketStatus.CNF);
		int currentRac = (int) bookingCol.countDocuments(session,
				Filters.and(Filters.eq("TRAIN_ID", trainId), Filters.eq("TRAIN_NAME", trainName),
						Filters.eq("TRAVEL_DATE", travelDate), Filters.eq("CLASS_TYPE", classType),
						Filters.elemMatch("ASSOCIATED_PASSENGER", Filters.regex("CURRENT_STATUS", "^RAC/"))));
		// 5️⃣ Rebuild seat availability (source of truth)
		rebuildSeatAvailabilityFromBookings(session, bookingCol, seatAvailCol, trainId, trainName, travelDate,
				classType, TicketStatus.RAC);

		Queue<Passenger> rebalancedRACQueue = findRACSeats(session, bookingCol, trainId, trainName, travelDate,
				classType);

		System.out.println("RebalancedRACQueue : " + rebalancedRACQueue);

		int racSeatNo = 1;

		while (!rebalancedRACQueue.isEmpty()) {
			int currentRacSeatNo = rebalancedRACQueue.peek().getSeatMetaData().getSeatNumber();
			if (racSeatNo == currentRacSeatNo) {
				racSeatNo++;
				rebalancedRACQueue.poll();
				continue;
			}
			Passenger p = rebalancedRACQueue.poll();

			promoteWithSeat(session, bookingCol, p, "RAC", "RAC", String.valueOf(racSeatNo++));

		}

		int availableRac = racCapacity - currentRac;

		System.out.println("Current RAC : " + currentRac);

		System.out.println("Available RAC : " + availableRac);

		// 4️⃣ WL → RAC until RAC full
		for (int i = 0; i < availableRac; i++) {
			if (queues.wlQueue.isEmpty())
				break;

			Passenger p = queues.wlQueue.poll();

			promoteWithSeat(session, bookingCol, p, "RAC", "RAC", String.valueOf(++currentRac));
			addBooking(session, seatAvailCol, trainId, travelDate, classType, currentRac, TicketStatus.RAC);
		}

		System.out.println("After Apply WL -> RAC....");

		System.out.println("Free CNF Seats : " + freeCnfSeats);
		System.out.println("RAC Queues : " + queues.racQueue);
		System.out.println("WL Queues : " + queues.wlQueue);

		int wlSeatNo = 1;
		while (!queues.wlQueue.isEmpty()) {

			Passenger p = queues.wlQueue.poll();

			promoteWithSeat(session, bookingCol, p, "WL", "WL", String.valueOf(wlSeatNo++));

		}

		System.out.println("After Apply WL....");

		System.out.println("Free CNF Seats : " + freeCnfSeats);
		System.out.println("RAC Queues : " + queues.racQueue);
		System.out.println("WL Queues : " + queues.wlQueue);

		rebuildSeatAvailabilityFromBookings(session, bookingCol, seatAvailCol, trainId, trainName, travelDate,
				classType, TicketStatus.WL);
	}

	private void addBooking(ClientSession session, MongoCollection<Document> seatAvailCol, String trainId,
			String travelDate, String classType, int seatNo, TicketStatus ticketStatus) {

		System.out.println("Train Id in Add Booking..." + trainId + " Travel Date in Add Booking " + travelDate
				+ "Class " + classType);
		String seatCode, statusType;
		switch (ticketStatus) {

		case TicketStatus.RAC -> {
			seatCode = "RAC-" + seatNo;
			statusType = ".rac";
		}
		case TicketStatus.WL -> {
			seatCode = "WL-" + seatNo;
			statusType = ".wl";
		}
		default -> {
			throw new IllegalArgumentException("Invalid Ticket Status Send");
		}
		}
		System.out.println("Seat Code " + seatCode + " StatusType : " + statusType);

		UpdateResult result = seatAvailCol.updateOne(session,
				Filters.and(Filters.eq("train_id", trainId), Filters.eq("date", travelDate)),
				Updates.push("classes." + classType + statusType, seatCode));

		System.out.println("Matched: " + result.getMatchedCount());
		System.out.println("Modified: " + result.getModifiedCount());
	}

	private Queue<Passenger> findRACSeats(ClientSession session, MongoCollection<Document> bookingCol, String trainId,
			String trainName, String travelDate, String classType) {

		Queue<Passenger> racPassengers = new PriorityQueue<>();

		FindIterable<Document> bookings = bookingCol.find(session,
				Filters.and(Filters.eq("TRAIN_ID", trainId), Filters.eq("TRAIN_NAME", trainName),
						Filters.eq("TRAVEL_DATE", travelDate), Filters.eq("CLASS_TYPE", classType)));

		for (Document booking : bookings) {
			String pnr = booking.getString("PNR_NUMBER");

			for (Document p : booking.getList("ASSOCIATED_PASSENGER", Document.class)) {

				String status = p.getString("CURRENT_STATUS");

				if (status == null || status.equals("CAN") || !status.contains("/"))
					continue;
				if (status.startsWith("RAC")) {

					byte seatNo = Byte.parseByte(status.split("/")[1]);
					Passenger passenger = new Passenger(p.getString("NAME"), null, p.getInteger("AGE").byteValue(),
							p.getString("GENDER").charAt(0), null, p.getBoolean("OPTED_AUTO_UPGRADE", false));

					passenger.setTicketStatus("RAC");
					passenger.setPnrNumber(pnr);

					SeatMetaData seatMetadata = new SeatMetaData(classType, status, seatNo);
					passenger.setSeatMetaData(seatMetadata);

					racPassengers.offer(passenger);
				}
			}
		}
		return racPassengers;

	}

	public Status updatePasswordForUserInDb(String userName, String currentPasswordPlain, String newPasswordPlain) {

		Status updateStatus = Status.FAILURE;
		try (MongoClient mongoClient = MongoClients.create(DB_PROPERTIES.getProperty(MONGO_DB_CONNECTION_URL, ""))) {

			MongoDatabase trainBookingDatabase = mongoClient
					.getDatabase(DB_PROPERTIES.getProperty(TRAIN_BOOKING_DB_NAME, ""));

			MongoCollection<Document> userCollection = trainBookingDatabase
					.getCollection(TrainBookingDatabase.USERS.name());

			Document userDocument = userCollection.find(Filters.eq(UserCollection.USER_NAME.name(), userName)).first();

			String latestHashedPasswordfromDB = userDocument.getString(UserCollection.HASHED_PASSWORD.name());

			String previousHashedPassword2fromDB = userDocument.getString(UserCollection.HASHED_PASSWORD_2.name());

			String previousHashedPassword3fromDB = userDocument.getString(UserCollection.HASHED_PASSWORD_3.name());

			System.out.println("Current Password Hashed In DB : " + latestHashedPasswordfromDB);
			System.out.println("Current Password Entered From Form  : " + currentPasswordPlain);

			if (!Hashing.isPlainPasswordMatchedWithHashedPassword(currentPasswordPlain, latestHashedPasswordfromDB)) {

				return Status.CURRENT_PASSWORD_MISMATCHED;
			}
			if (Hashing.isPlainPasswordMatchedWithHashedPassword(newPasswordPlain, latestHashedPasswordfromDB)
					|| Hashing.isPlainPasswordMatchedWithHashedPassword(newPasswordPlain, previousHashedPassword2fromDB)
					|| Hashing.isPlainPasswordMatchedWithHashedPassword(newPasswordPlain,
							previousHashedPassword3fromDB)) {

				System.out.println("You can't Use Password which was used in last 3 time");
				updateStatus = Status.OLD_PASSWORD_REUSED;
			} else {
				updatePassword(userName, newPasswordPlain);
				updateStatus = Status.SUCCESS;
			}

		}

		return updateStatus;
	}

	public void updatePassword(String userName, String newPasswordPlain) {

		try (MongoClient mongoClient = MongoClients.create(DB_PROPERTIES.getProperty(MONGO_DB_CONNECTION_URL, ""))) {

			MongoDatabase trainBookingDatabase = mongoClient
					.getDatabase(DB_PROPERTIES.getProperty(TRAIN_BOOKING_DB_NAME, ""));

			MongoCollection<Document> userCollection = trainBookingDatabase
					.getCollection(TrainBookingDatabase.USERS.name());

			String newPasswordHashed = Hashing.getHashedPassword(newPasswordPlain);

			Document userDocument = userCollection.find(Filters.eq(UserCollection.USER_NAME.name(), userName)).first();

			String latestHashedPasswordfromDB = userDocument.getString(UserCollection.HASHED_PASSWORD.name());
			String previousHashedPassword2fromDB = userDocument.getString(UserCollection.HASHED_PASSWORD_2.name());

			Document updateFields = new Document().append(UserCollection.HASHED_PASSWORD.name(), newPasswordHashed)
					.append(UserCollection.HASHED_PASSWORD_2.name(), latestHashedPasswordfromDB)
					.append(UserCollection.HASHED_PASSWORD_3.name(), previousHashedPassword2fromDB);

			UpdateResult result = userCollection.updateOne(Filters.eq(UserCollection.USER_NAME.name(), userName),
					new Document("$set", updateFields));

			System.out.println("Updated Document Count : " + result.getModifiedCount());
		}

	}

	public Status updatePassengerDetails(String userName, String fullName, String email, String contactNo) {

		Status updateStatus = Status.FAILURE;
		try (MongoClient mongoClient = MongoClients.create(DB_PROPERTIES.getProperty(MONGO_DB_CONNECTION_URL))) {

			MongoDatabase db = mongoClient.getDatabase(DB_PROPERTIES.getProperty(TRAIN_BOOKING_DB_NAME));

			MongoCollection<Document> users = db.getCollection(TrainBookingDatabase.USERS.name());

			// 1️⃣ Fetch existing user
			Document existingUser = users.find(Filters.eq(UserCollection.USER_NAME.name(), userName)).first();

			// 2️⃣ Build update document dynamically
			Document updateFields = new Document();

			if (fullName != null && !fullName.equals(existingUser.getString(UserCollection.FULL_NAME.name()))) {
				updateFields.append(UserCollection.FULL_NAME.name(), fullName);
			}

			if (email != null && !email.equals(existingUser.getString(UserCollection.EMAIL.name()))) {
				updateFields.append(UserCollection.EMAIL.name(), email);
			}

			if (contactNo != null && !contactNo.equals(existingUser.getString(UserCollection.CONTACT_NO.name()))) {
				updateFields.append(UserCollection.CONTACT_NO.name(), contactNo);
			}

			// 3️⃣ If nothing changed
			if (updateFields.isEmpty()) {

				updateStatus = Status.EMPTY_CHANGES;
				return updateStatus;
			}

			// 4️⃣ Update only changed fields
			UpdateResult result = users.updateOne(Filters.eq(UserCollection.USER_NAME.name(), userName),
					new Document("$set", updateFields));

			if (result.getModifiedCount() != 0) {
				updateStatus = Status.SUCCESS;
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
		return updateStatus;
	}

	public Users getUserDetails(String userName) {
		try (MongoClient mongoClient = MongoClients.create(DB_PROPERTIES.getProperty(MONGO_DB_CONNECTION_URL))) {

			MongoDatabase db = mongoClient.getDatabase(DB_PROPERTIES.getProperty(TRAIN_BOOKING_DB_NAME));

			MongoCollection<Document> users = db.getCollection(TrainBookingDatabase.USERS.name());

			// 1️⃣ Fetch existing user
			Document existingUser = users.find(Filters.eq(UserCollection.USER_NAME.name(), userName)).first();

			if (existingUser == null) {
				return null;
			}

			String fullName = existingUser.getString(UserCollection.FULL_NAME.name());

			String hashedPassword = existingUser.getString(UserCollection.HASHED_PASSWORD.name());

			String email = existingUser.getString(UserCollection.EMAIL.name());

			String contactNo = existingUser.getString(UserCollection.CONTACT_NO.name());
			System.out.println("User Object Retrieved");
			return new Users(fullName, email, contactNo, userName, hashedPassword);
		}

	}

	public boolean isPropertyValueAlreadyUsedByAnotherUser(String userName, String propertyValue,
			UserCollection property) {
		try (MongoClient mongoClient = MongoClients.create(DB_PROPERTIES.getProperty(MONGO_DB_CONNECTION_URL))) {

			MongoDatabase db = mongoClient.getDatabase(DB_PROPERTIES.getProperty(TRAIN_BOOKING_DB_NAME));

			MongoCollection<Document> usersCollection = db.getCollection(TrainBookingDatabase.USERS.name());

			FindIterable<Document> allUsers = usersCollection
					.find(Filters.ne(UserCollection.USER_NAME.name(), userName));

			for (Document user : allUsers) {

				String propertyValueOfCurrentUser = user.getString(property.name());

				if (propertyValueOfCurrentUser.equals(propertyValue)) {
					return true;
				}
			}

		}

		return false;
	}

	public void storeTransactionStatusInDb(Double totalAmount, String transactionId, String userName,
			String transactionStatus, String transactionPurpose, String paymentGateway) {
		try (MongoClient mongoClient = MongoClients.create(DB_PROPERTIES.getProperty(MONGO_DB_CONNECTION_URL))) {

			MongoDatabase trainDatabase = mongoClient.getDatabase(DB_PROPERTIES.getProperty(TRAIN_BOOKING_DB_NAME));

			MongoCollection<Document> paymentCollection = trainDatabase
					.getCollection(TrainBookingDatabase.PAYMENTS.name());

			Document newTransaction = new Document(PaymentsCollection.USER_NAME.name(), userName)
					.append(PaymentsCollection.TRASACTION_DATE.name(), Instant.now())
					.append(PaymentsCollection.TOTAL_AMOUNT.name(), totalAmount)
					.append(PaymentsCollection.TRANSACTION_ID.name(), transactionId)
					.append(PaymentsCollection.TRANSACTION_STATUS.name(), transactionStatus)
					.append(PaymentsCollection.TRANSACTION_PURPOSE.name(), transactionPurpose)
					.append(PaymentsCollection.PAYMENT_GATEWAY.name(), paymentGateway);

			paymentCollection.insertOne(newTransaction);

		}

	}

	public List<TransactionData> getCurrentUserTransactionList(String userName) {
		List<TransactionData> transactionList = new LinkedList<>();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MMMM dd EEE HH:mm:ss");
		try (MongoClient mongoClient = MongoClients.create(DB_PROPERTIES.getProperty(MONGO_DB_CONNECTION_URL))) {

			MongoDatabase trainDatabase = mongoClient.getDatabase(DB_PROPERTIES.getProperty(TRAIN_BOOKING_DB_NAME));

			MongoCollection<Document> paymentsCollection = trainDatabase
					.getCollection(TrainBookingDatabase.PAYMENTS.name());

			FindIterable<Document> usersTransactionDocument = paymentsCollection
					.find(Filters.eq(PaymentsCollection.USER_NAME.name(), userName));

			for (Document transaction : usersTransactionDocument) {

				String transactionId = transaction.getString(PaymentsCollection.TRANSACTION_ID.name());

				Date date = transaction.getDate(PaymentsCollection.TRASACTION_DATE.name());

				LocalDateTime ldt = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
				String transactionDate = ldt.format(formatter);

				String transactionStatus = transaction.getString(PaymentsCollection.TRANSACTION_STATUS.name());
				String transactionPurpose = transaction.getString(PaymentsCollection.TRANSACTION_PURPOSE.name());

				String paymentGateWay = transaction.getString(PaymentsCollection.PAYMENT_GATEWAY.name());

				double totalAmount = transaction.getDouble(PaymentsCollection.TOTAL_AMOUNT.name());

				TransactionData transactionDocument = new TransactionData(userName, totalAmount, transactionDate,
						transactionId, Status.valueOf(transactionStatus),
						TransactionPurpose.valueOf(transactionPurpose), PaymentGateway.valueOf(paymentGateWay));
				transactionList.add(transactionDocument);

			}

		}

		return transactionList;
	}

	public List<TransactionData> getCurrentUserTransactionList(String userName, int offset, int pageSize) {

		List<TransactionData> transactionList = new LinkedList<>();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MMMM dd EEE HH:mm:ss");
		try (MongoClient mongoClient = MongoClients.create(DB_PROPERTIES.getProperty(MONGO_DB_CONNECTION_URL))) {

			MongoDatabase trainDatabase = mongoClient.getDatabase(DB_PROPERTIES.getProperty(TRAIN_BOOKING_DB_NAME));

			MongoCollection<Document> paymentsCollection = trainDatabase
					.getCollection(TrainBookingDatabase.PAYMENTS.name());

			// 1. Create the query
			FindIterable<Document> usersTransactionDocument = paymentsCollection
					.find(Filters.eq(PaymentsCollection.USER_NAME.name(), userName))
					// 2. SORT: Newest transactions first (Essential for consistent pagination)
					.sort(Sorts.descending(PaymentsCollection.TRASACTION_DATE.name()))
					// 3. SKIP: The number of records to jump over (offset)
					.skip(offset)
					// 4. LIMIT: The max number of records to return (pageSize)
					.limit(pageSize);

			for (Document transaction : usersTransactionDocument) {

				String transactionId = transaction.getString(PaymentsCollection.TRANSACTION_ID.name());
				Date date = transaction.getDate(PaymentsCollection.TRASACTION_DATE.name());

				LocalDateTime ldt = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
				String transactionDate = ldt.format(formatter);
				String transactionStatus = transaction.getString(PaymentsCollection.TRANSACTION_STATUS.name());
				String transactionPurpose = transaction.getString(PaymentsCollection.TRANSACTION_PURPOSE.name());
				String paymentGateWay = transaction.getString(PaymentsCollection.PAYMENT_GATEWAY.name());
				double totalAmount = transaction.getDouble(PaymentsCollection.TOTAL_AMOUNT.name());

				TransactionData transactionDocument = new TransactionData(userName, totalAmount, transactionDate,
						transactionId, Status.valueOf(transactionStatus),
						TransactionPurpose.valueOf(transactionPurpose), PaymentGateway.valueOf(paymentGateWay));

				transactionList.add(transactionDocument);
			}
		}

		return transactionList;
	}

}
