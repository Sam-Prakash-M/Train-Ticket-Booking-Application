package com.samprakash.paymentview.paypalview;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.paypal.http.HttpResponse;
import com.paypal.orders.AmountWithBreakdown;
import com.paypal.orders.ApplicationContext;
import com.paypal.orders.LinkDescription;
import com.paypal.orders.Order;
import com.paypal.orders.OrderRequest;
import com.paypal.orders.OrdersCreateRequest;
import com.paypal.orders.PurchaseUnitRequest;
import com.paypal.core.PayPalHttpClient; // Ensure this is imported
import com.samprakash.paymentview.PayPalClient;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/PayPalPayment")
public class PayPalClientView extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			String amountStr = request.getParameter("amount");
			if (amountStr == null || amountStr.isEmpty()) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Amount missing");
				return;
			}

			// 1. Initialize Client
			PayPalHttpClient client = PayPalClient.client();

			// 2. Construct Order Request
			OrderRequest orderRequest = new OrderRequest();
			orderRequest.checkoutPaymentIntent("CAPTURE");

			List<PurchaseUnitRequest> purchaseUnits = new ArrayList<>();
			PurchaseUnitRequest purchaseUnit = new PurchaseUnitRequest()
					.amountWithBreakdown(new AmountWithBreakdown().currencyCode("USD").value(amountStr));
			purchaseUnits.add(purchaseUnit);
			orderRequest.purchaseUnits(purchaseUnits);

			String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
					+ request.getContextPath();
			// Set Redirect URLs
			String returnUrl = baseUrl + "/PaymentSuccess?source=PAYPAL";
			String cancelUrl = baseUrl + "/payment.jsp?error=cancelled";

			orderRequest.applicationContext(new ApplicationContext().returnUrl(returnUrl).cancelUrl(cancelUrl)
					.brandName("Sam Railways").landingPage("LOGIN").userAction("PAY_NOW"));

			// 3. Call PayPal API (UNCOMMENTED & FIXED)
			OrdersCreateRequest requestApi = new OrdersCreateRequest().requestBody(orderRequest);

			// Execute the request
			HttpResponse<Order> responseApi = client.execute(requestApi);
			Order order = responseApi.result();

			// 4. Extract Approval Link
			String approveLink = null;
			for (LinkDescription link : order.links()) {
				if ("approve".equals(link.rel())) {
					approveLink = link.href();
					break;
				}
			}

			// 5. Redirect User
			if (approveLink != null) {
				response.sendRedirect(approveLink);
			} else {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No approval link found from PayPal");
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "PayPal Error: " + e.getMessage());
		}
	}
}