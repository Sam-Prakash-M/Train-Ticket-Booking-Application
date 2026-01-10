package com.samprakash.profileviewmodel;

import com.samprakash.basemodel.Status;
import com.samprakash.basemodel.UserCollection;
import com.samprakash.basemodel.Users;
import com.samprakash.repository.DataBaseConnector;

public class ProfileUpdateViewModel {

	public static Status updatePassengerDetails(String userName, String fullName, String email, String contactNo) {

		Status updateStatus = Status.FAILURE;

		DataBaseConnector dataBaseConnector = DataBaseConnector.getInstance();

		if (dataBaseConnector.isUserAlreadyExist(userName)) {

			if (email != null) {
				if (dataBaseConnector.isPropertyValueAlreadyUsedByAnotherUser(userName, email, UserCollection.EMAIL)) {
					System.out.println("Can't Change Email ...Becasue Email has been used by Another User");
					updateStatus = Status.EMAIL_ID_ALREADY_USED;
					return updateStatus;
				}
			}

			if (contactNo != null) {
				if (dataBaseConnector.isPropertyValueAlreadyUsedByAnotherUser(userName, contactNo,
						UserCollection.CONTACT_NO)) {
					System.out.println("Can't Change Contact No ...Becasue ContactNo has been used by Another User");
					updateStatus = Status.CONTACT_NO_ALREADY_USED;
					return updateStatus;
				}
			}

			return dataBaseConnector.updatePassengerDetails(userName, fullName, email, contactNo);

		} else {
			System.out.println("User Does not Exist With the User Name : " + userName);
		}

		return updateStatus;
	}

	public static Users getUserDetails(String userName) {

		return DataBaseConnector.getInstance().getUserDetails(userName);
	}

}
