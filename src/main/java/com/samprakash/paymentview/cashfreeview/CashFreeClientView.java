package com.samprakash.paymentview.cashfreeview;

import java.io.IOException;
import java.util.UUID;

import com.cashfree.ApiResponse;
import com.cashfree.Cashfree;
import com.cashfree.model.*;
import com.samprakash.paymentview.CashfreeClient;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/CashfreePayment")
public class CashFreeClientView extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	public void init() {
		CashfreeClient.init(); // Load keys
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			String amountStr = request.getParameter("amount");
			if (amountStr == null)
				throw new Exception("Amount is missing");

			Double amount = Double.parseDouble(amountStr);
			String orderId = "ORDER_" + System.currentTimeMillis();
			String customerId = "CUST_" + UUID.randomUUID().toString().substring(0, 8);
			String userPhone = "9999999999"; // Replace with actual user data if available
			String userEmail = "test@example.com";

			// 1. Prepare Customer Details
			CustomerDetails customer = new CustomerDetails();
			customer.setCustomerId(customerId);
			customer.setCustomerPhone(userPhone);
			customer.setCustomerEmail(userEmail);
			customer.setCustomerName("Sam Railways User");

			// 2. Prepare Order Meta (Return URL is critical)
			OrderMeta orderMeta = new OrderMeta();
			// This URL is where Cashfree redirects after payment
			String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
					+ request.getContextPath();

			String returnUrl = baseUrl + "/PaymentSuccess?source=CASH_FREE&order_id={order_id}";
			orderMeta.setReturnUrl(returnUrl);

			// 3. Create Order Request
			CreateOrderRequest orderRequest = new CreateOrderRequest();
			orderRequest.setOrderAmount(amount);
			orderRequest.setOrderCurrency("INR");
			orderRequest.setOrderId(orderId);
			orderRequest.setCustomerDetails(customer);
			orderRequest.setOrderMeta(orderMeta);

			// 4. Call Cashfree API
			ApiResponse<OrderEntity> orderResponse = null;
			try {
				orderResponse = new Cashfree().PGCreateOrder("2023-08-01", orderRequest, null, null, null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// 5. Get Session ID
			String paymentSessionId = orderResponse.getData().getPaymentSessionId();

			// 6. Forward to Checkout Page
			request.setAttribute("paymentSessionId", paymentSessionId);
			request.setAttribute("orderId", orderId);
			request.setAttribute("amount", amountStr);

			request.getRequestDispatcher("cashfreeCheckout.jsp").forward(request, response);

		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(500, "Cashfree Error: " + e.getMessage());
		}
	}
}