package com.samprakash.profilemodel;

import com.samprakash.basemodel.Status;
import com.samprakash.paymentmodel.TransactionPurpose;
import com.samprakash.paymentview.PaymentGateway;

public record TransactionData(String userName, double totalAmount,String transactionDate, String transactionId, Status TransactionStatus,
		TransactionPurpose transactionPurpose, PaymentGateway paymentGateWay) {

}
