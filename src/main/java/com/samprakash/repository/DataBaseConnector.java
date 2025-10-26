package com.samprakash.repository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.samprakash.basemodel.TrainBookingCollections;
import com.samprakash.basemodel.UserCollection;
import com.samprakash.basemodel.Users;
import com.samprakash.baseviewmodel.Hashing;

public class DataBaseConnector {

	private static final DataBaseConnector DATA_BASE_CONNECTOR;
	private final Properties DB_PROPERTIES;

	static {

		DATA_BASE_CONNECTOR = new DataBaseConnector();
	}

	public static synchronized DataBaseConnector getInstance() {

		return DATA_BASE_CONNECTOR;
	}

	private DataBaseConnector() {

		DB_PROPERTIES = new Properties();
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

		try (MongoClient mongoClient = MongoClients.create(DB_PROPERTIES.getProperty("Db.Url", ""))) {

			MongoDatabase trainBookingDataBase = mongoClient
					.getDatabase(DB_PROPERTIES.getProperty("Db.TrainBooking.name", ""));

			MongoCollection<Document> allUsersCollection = trainBookingDataBase
					.getCollection(TrainBookingCollections.USERS.name());
			Document newUserDocument = new Document(UserCollection.FULL_NAME.name(), newUser.fullName())
					.append(UserCollection.EMAIL.name(), newUser.email())
					.append(UserCollection.USER_NAME.name(), newUser.userName())
					.append(UserCollection.HASHED_PASSWORD.name(), newUser.hashedPassword());

			allUsersCollection.insertOne(newUserDocument);
		}

		return true;

	}

	public boolean isUserAlreadyExist(String userName) {

		try (MongoClient mongoClient = MongoClients.create(DB_PROPERTIES.getProperty("Db.Url", ""))) {

			MongoDatabase trainBookingDataBase = mongoClient
					.getDatabase(DB_PROPERTIES.getProperty("Db.TrainBooking.name", ""));

			MongoCollection<Document> allUsersCollection = trainBookingDataBase
					.getCollection(TrainBookingCollections.USERS.name());
			Document userDocument = new Document(UserCollection.USER_NAME.name(), userName);

			return allUsersCollection.find(userDocument).iterator().hasNext();
		}

	}

	public boolean isUserCredentialIsCorrect(String userName, String password) {

		boolean isCredentialsIsCorrect = false;
		if (userName == null || password == null) {
			System.out.println("Username or Password is null");
			return isCredentialsIsCorrect;
		}
		try (MongoClient mongoClient = MongoClients.create(DB_PROPERTIES.getProperty("Db.Url", ""))) {

			MongoDatabase trainBookingDatabase = mongoClient
					.getDatabase(DB_PROPERTIES.getProperty("Db.TrainBooking.name", password));

			MongoCollection<Document> allUserCollection = trainBookingDatabase
					.getCollection(TrainBookingCollections.USERS.name());

			Document userDocument = allUserCollection.find(Filters.eq(UserCollection.USER_NAME.name(), userName))
					.first();

			if (userDocument != null) {

				String hashedPassword = userDocument.getString(UserCollection.HASHED_PASSWORD.name());

				return Hashing.isPlainPasswordMatchedWithHashedPassword(password, hashedPassword);

			}

			return false;

		}

	}

}
