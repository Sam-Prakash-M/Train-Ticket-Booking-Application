package com.samprakash.profileviewmodel;

import com.samprakash.baseviewmodel.Hashing;
import com.samprakash.repository.DataBaseConnector;

public class PasswordUpdateViewModel {

	
	
	public static boolean updatePassword(String userName,String newPassword) {
		
		boolean updateStatus = false;
		
		
		DataBaseConnector dataBaseConnector = DataBaseConnector.getInstance();
		
		
		String hashedPassword = Hashing.getHashedPassword(newPassword);
		
		
		if(!dataBaseConnector.isPasswordSameAsAnyOfLastThreeOldPasswords(userName,hashedPassword)) {
			
			dataBaseConnector.updatePassword(userName,hashedPassword);
			updateStatus = true;
		}
		else {
			System.out.println("Password was Matched with Last 3 used Passwords");
		}
		
		return updateStatus;
	}
}
