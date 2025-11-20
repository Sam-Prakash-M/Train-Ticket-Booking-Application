package com.samprakash.paymentview.razorpayview;

import java.io.IOException;
import java.io.PrintWriter;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/createOrder")
public class RazorPayClientView extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            // 1. Read amount from request (frontend sends in rupees)
            String amountStr = request.getParameter("amount");  
            int amount = Integer.parseInt(amountStr) * 100;   // convert to paise

            // 2. Create Razorpay client
            RazorpayClient client = new RazorpayClient(
                    "YOUR_KEY_ID",
                    "YOUR_KEY_SECRET"
            );

            // 3. Order request body
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "txn_" + System.currentTimeMillis());

            // 4. Create order
            Order order = client.orders.create(orderRequest);

            // 5. Send order details to frontend
            JSONObject orderJson = new JSONObject(order.toString());

            out.print(orderJson.toString());
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
