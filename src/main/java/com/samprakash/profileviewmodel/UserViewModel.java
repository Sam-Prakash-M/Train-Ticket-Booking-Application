package com.samprakash.profileviewmodel;

import com.samprakash.repository.DataBaseConnector;

public class UserViewModel {

	
	
	public static String getUserNameByEmailId(String emailId) {
		
		DataBaseConnector dataBaseConnector = DataBaseConnector.getInstance();
		
		return dataBaseConnector.getUserNameByEmailId(emailId);
	}
}
