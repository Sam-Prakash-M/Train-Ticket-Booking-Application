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
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

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
import com.samprakash.ticketbookmodel.SeatCounts;
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
			String source, String destination, String classType, String journeyDate) {

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
				throw new RuntimeException("Not enough seats (Confirmed + RAC + WL) for " + required + " passengers");
			}

			// We'll track allocations to rollback if something fails
			List<String> confirmedAllocated = new ArrayList<>();
			List<String> racAllocated = new ArrayList<>();
			List<String> wlAllocated = new ArrayList<>();

			// -------- PHASE A: allocate confirmed seats honoring preference and grouping
			// --------
			try {
				List<Passenger> stillUnallocated = allocateConfirmedGroup(passengerList, allSeatsInClass, bookedList,
						availCol, trainId, journeyDate, confirmedAllocated);

				// -------- PHASE B: allocate RAC for remaining --------
				if (!stillUnallocated.isEmpty() && freeRac > 0) {
					stillUnallocated = allocateRac(stillUnallocated, availCol, trainId, journeyDate, racLimit,
							racAllocated);
				}

				// -------- PHASE C: allocate WL for remaining --------
				if (!stillUnallocated.isEmpty() && freeWl > 0) {
					stillUnallocated = allocateWl(stillUnallocated, availCol, trainId, journeyDate, wlLimit,
							wlAllocated);
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
				return new Ticket(journeyDate,trainId, trainName, classType, source, destination, pnr, txnId,
						new HashSet<>(passengerList));

			} catch (Exception inner) {
				// Rollback and propagate
				rollback(availCol, availFilter, confirmedAllocated, racAllocated, wlAllocated);
				throw inner;
			}

		} catch (Exception e) {
			throw new RuntimeException("Booking failed: " + e.getMessage(), e);
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
			List<String> confirmedAllocated) {

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

		Document availFilter = new Document("train_id", trainId).append("date", journeyDate);
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
					confirmedAllocated);

			if (!allocated) {
				// fallback: all available seats in order (not booked)
				List<Document> fallback = allSeatsInClass.stream()
						.filter(d -> !bookedSet.contains(seatCode(d.getString("coach_no"), d.getString("seat_no"))))
						.collect(Collectors.toList());

				allocated = tryAllocateFromList(p, fallback, bookedSet, availCol, availFilter, options,
						confirmedAllocated);
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
			List<String> confirmedAllocated) {

		for (Document seatDoc : seatCandidates) {
			String coachNo = seatDoc.getString("coach_no");
			String seatNo = seatDoc.getString("seat_no");
			String code = seatCode(coachNo, seatNo);

			// Atomic push only if seat not already present in booked array
			Document updated = availCol.findOneAndUpdate(
					Filters.and(Filters.eq("train_id", availFilter.getString("train_id")),
							Filters.eq("date", availFilter.getString("date")), Filters.not(Filters.in("booked", code))),
					Updates.push("booked", code), options);

			if (updated != null) {
				// success: set passenger seat and mark it in our local bookedSet and
				// confirmedAllocated list
				p.setSeatMetaData(new SeatMetaData(coachNo, (byte) Integer.parseInt(seatNo)));
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
			String journeyDate, int racLimit, List<String> racAllocated) {

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
							Filters.and(Filters.eq("train_id", trainId), Filters.eq("date", journeyDate),
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
						Filters.and(Filters.eq("train_id", trainId), Filters.eq("date", journeyDate),
								Filters.eq("rac." + (idx - 1), "RAC")),
						Updates.set("rac." + (idx - 1), racCode), options);

				// If confirm == null, it's unexpected but continue — the rac code might already
				// be replaced by another process
				// Set passenger metadata to indicate RAC
				p.setSeatMetaData(new SeatMetaData("RAC", (byte) idx));
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
			String journeyDate, int wlLimit, List<String> wlAllocated) {

		List<Passenger> stillUnallocated = new ArrayList<>();

		Document availFilter = new Document("train_id", trainId).append("date", journeyDate);
		FindOneAndUpdateOptions options = new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER);

		for (Passenger p : remaining) {

			Document updated = availCol
					.findOneAndUpdate(
							Filters.and(Filters.eq("train_id", trainId), Filters.eq("date", journeyDate),
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
								Filters.and(Filters.eq("train_id", trainId), Filters.eq("date", journeyDate),
										Filters.eq("wl." + (idx - 1), "WL")),
								Updates.set("wl." + (idx - 1), wlCode), options);

				p.setSeatMetaData(new SeatMetaData("WL", (byte) idx));
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
					Filters.and(Filters.eq("train_id", availFilter.getString("train_id")),
							Filters.eq("date", availFilter.getString("date"))),
					Updates.pullAll("booked", confirmedAllocated));
		}

		if ((racAllocated != null && !racAllocated.isEmpty())) {
			availCol.updateOne(Filters.and(Filters.eq("train_id", availFilter.getString("train_id")),
					Filters.eq("date", availFilter.getString("date"))), Updates.pullAll("rac", racAllocated));
		}

		if ((wlAllocated != null && !wlAllocated.isEmpty())) {
			availCol.updateOne(Filters.and(Filters.eq("train_id", availFilter.getString("train_id")),
					Filters.eq("date", availFilter.getString("date"))), Updates.pullAll("wl", wlAllocated));
		}
	}

}
