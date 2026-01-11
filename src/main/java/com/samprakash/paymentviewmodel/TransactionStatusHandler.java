package com.samprakash.paymentviewmodel;

import com.samprakash.basemodel.Status;
import com.samprakash.paymentmodel.TransactionPurpose;
import com.samprakash.paymentview.PaymentGateway;
import com.samprakash.repository.DataBaseConnector;

public class TransactionStatusHandler {

	public static void storeTransactionStatusInDb(Double totalAmount, String transactionId, String userName,
			Status transactionStatus, TransactionPurpose transactionPurpose,PaymentGateway paymentGateway) {

		DataBaseConnector dataBaseConnector = DataBaseConnector.getInstance();
		dataBaseConnector.storeTransactionStatusInDb(totalAmount,transactionId,userName,transactionStatus.name(),transactionPurpose.name(),paymentGateway.name());
	}
}
