package com.samprakash.profileviewmodel;

import java.util.List;

import com.samprakash.profilemodel.TransactionData;
import com.samprakash.repository.DataBaseConnector;

public class TransactionViewModel {

	public static List<TransactionData> getUserTransaction(String userName) {


		
		DataBaseConnector dataBaseConnector = DataBaseConnector.getInstance();
		
		List<TransactionData> transactionList = dataBaseConnector.getCurrentUserTransactionList(userName);
		
		return transactionList;
	}

	public static List<TransactionData> getUserTransaction(String userName, int offset, int pageSize) {
		

		DataBaseConnector dataBaseConnector = DataBaseConnector.getInstance();
		
		List<TransactionData> transactionList = dataBaseConnector.getCurrentUserTransactionList(userName,offset,pageSize);
		return transactionList;
	}

}
