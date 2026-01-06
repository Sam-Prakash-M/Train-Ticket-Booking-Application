package com.samprakash.paymentview.paypalview;

import java.io.IOException;
import java.util.Arrays;

import com.paypal.sdk.controllers.OrdersController;
import com.paypal.sdk.exceptions.ApiException;
import com.paypal.sdk.http.response.ApiResponse;
import com.paypal.sdk.models.AmountWithBreakdown;
import com.paypal.sdk.models.CheckoutPaymentIntent;
import com.paypal.sdk.models.CreateOrderInput;
import com.paypal.sdk.models.Order;
import com.paypal.sdk.models.OrderRequest;
import com.paypal.sdk.models.PurchaseUnitRequest;
import com.samprakash.paymentview.PayPalClient;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/PaypalServlet")
public class PayPalCreateOrderServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		OrdersController ordersController = PayPalClient.client().getOrdersController();

		CreateOrderInput input = new CreateOrderInput.Builder(null,
				new OrderRequest.Builder(CheckoutPaymentIntent.CAPTURE,
						Arrays.asList(
								new PurchaseUnitRequest.Builder(new AmountWithBreakdown.Builder("USD", "30.00").build())
										.build()))
						.build())
				.prefer("return=representation").build();

		ApiResponse<Order> apiResponse = null;

		try {
			apiResponse = ordersController.createOrder(input);
		} catch (ApiException | IOException e) {
			
			e.printStackTrace();
		}

		String orderId = apiResponse.getResult().getId();

		response.setContentType("application/json");
		response.getWriter().print("{\"orderId\":\"" + orderId + "\"}");
	}
}
