package com.samprakash.profileviewmodel;

import com.samprakash.repository.DataBaseConnector;

public class ProfileUpdateViewModel {

	
	
	public static boolean updatePassengerDetails(String userName) {
		
		
		boolean updateStatus = false;
		
		
		DataBaseConnector dataBaseConnector = DataBaseConnector.getInstance();
		
		if(dataBaseConnector.isUserAlreadyExist(userName)) {
			
		
			
			
		}
		else {
			System.out.println("User Does not Exist With the User Name : "+userName);
		}
		
		return updateStatus;
	}
	
	
	
}
