package com.samprakash.paymentview;

import java.io.InputStream;
import java.util.Properties;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;


public class PayPalClient {
    private static final Properties props = new Properties();

    static {
        try (InputStream in = PayPalClient.class.getClassLoader()
                .getResourceAsStream("paypal.properties")) {

            if (in == null) throw new RuntimeException("paypal.properties not found!");
            props.load(in);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static PayPalHttpClient  client() {
        String clientId = props.getProperty("paypal.clientId");
        String secret = props.getProperty("paypal.secret");

     // Use Sandbox Environment
        PayPalEnvironment environment = new PayPalEnvironment.Sandbox(clientId, secret);
        return new PayPalHttpClient(environment);
    }
}
