package com.samprakash.profileviewmodel;

import com.samprakash.basemodel.Status;
import com.samprakash.repository.DataBaseConnector;

public class UserViewModel {

	public static String getUserNameByEmailId(String emailId) {

		DataBaseConnector dataBaseConnector = DataBaseConnector.getInstance();

		return dataBaseConnector.getUserNameByEmailId(emailId);
	}

	public static Status updateUserPassword(String emailId, String newPassword) {

		DataBaseConnector dataBaseConnector = DataBaseConnector.getInstance();
		String userName = dataBaseConnector.getUserNameByEmailId(emailId);

		if (userName == null) {

			return Status.USER_DOES_NOT_EXIST;
		}

		

		return dataBaseConnector.updatePasswordForUserInDb(userName, null, newPassword);

	}

	public static String getEmailIdByUserName(String userName) {

		DataBaseConnector dataBaseConnector = DataBaseConnector.getInstance();

		return dataBaseConnector.getEmailIdByUserName(userName);

	}
}
