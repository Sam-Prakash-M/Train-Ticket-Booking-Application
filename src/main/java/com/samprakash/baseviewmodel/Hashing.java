package com.samprakash.baseviewmodel;

import org.mindrot.jbcrypt.BCrypt;

public class Hashing {

	public static String getHashedPassword(String rawPassword) {

		if (rawPassword == null) {
			System.out.println("Given rawPassword is Null");
			return "";
		}

		return BCrypt.hashpw(rawPassword, BCrypt.gensalt());

	}

	public static boolean isPlainPasswordMatchedWithHashedPassword(String plainPassword, String hashedPassword) {

		return BCrypt.checkpw(plainPassword, hashedPassword);
	}
}
