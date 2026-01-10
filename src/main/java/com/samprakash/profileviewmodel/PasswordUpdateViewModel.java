package com.samprakash.profileviewmodel;

import com.samprakash.basemodel.Status;
import com.samprakash.baseviewmodel.Hashing;
import com.samprakash.repository.DataBaseConnector;

public class PasswordUpdateViewModel {

	public static Status updatePassword(String userName, String currentPassword, String newPassword) {

		DataBaseConnector dataBaseConnector = DataBaseConnector.getInstance();
		
		System.out.println("Current Password Before Hashed : "+currentPassword);

		

		return dataBaseConnector.updatePasswordForUserInDb(userName, currentPassword,
				newPassword);

	}
}
