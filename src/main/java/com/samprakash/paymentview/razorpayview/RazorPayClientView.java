package com.samprakash.paymentview.razorpayview;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Properties;

import org.json.JSONObject;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/RazorPayPayment")
public class RazorPayClientView extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final Properties RAZOR_PAY_PROPERTIES = new Properties();

    static {
        try (InputStream inputStream =
                RazorPayClientView.class.getClassLoader()
                        .getResourceAsStream("razorpayprops.properties")) {

            if (inputStream == null) {
                throw new RuntimeException("❌ razorpayprops.properties not found!");
            }

            RAZOR_PAY_PROPERTIES.load(inputStream);

        } catch (IOException e) {
            throw new RuntimeException("Error loading Razorpay properties", e);
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {

        response.setContentType("application/json; charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {

            String amountStr = request.getParameter("amount");

            if (amountStr == null || amountStr.isEmpty()) {
                response.setStatus(400);
                out.print("{\"error\":\"Amount missing\"}");
                return;
            }

            // Convert rupees → paise safely
            int amountPaise = new BigDecimal(amountStr)
                    .multiply(new BigDecimal("100"))
                    .intValueExact();

            String key = RAZOR_PAY_PROPERTIES.getProperty("razorpay.key");
            String secret = RAZOR_PAY_PROPERTIES.getProperty("razorpay.secret");

            if (key == null || secret == null) {
                response.setStatus(500);
                out.print("{\"error\":\"Razorpay API Key/Secret missing\"}");
                return;
            }

            RazorpayClient client = new RazorpayClient(key, secret);

            // Build order request
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountPaise);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "txn_" + System.currentTimeMillis());

            // Create order
            Order order = client.orders.create(orderRequest);

            // Return JSON
            out.print(order.toString());

        } catch (IOException | RazorpayException e) {
			
			e.printStackTrace();
		} 
        catch(Exception e) {
        	e.printStackTrace();
        }
    }
}
