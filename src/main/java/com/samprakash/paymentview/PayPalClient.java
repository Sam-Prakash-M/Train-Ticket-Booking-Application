package com.samprakash.paymentview;

import java.io.InputStream;
import java.util.Properties;

import com.paypal.sdk.Environment;
import com.paypal.sdk.PaypalServerSdkClient;
import com.paypal.sdk.authentication.ClientCredentialsAuthModel;


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

    public static PaypalServerSdkClient  client() {
        String clientId = props.getProperty("paypal.clientId");
        String secret = props.getProperty("paypal.secret");

        PaypalServerSdkClient client =
        	    new PaypalServerSdkClient.Builder()
        	        .clientCredentialsAuth(
        	            new ClientCredentialsAuthModel.Builder(
        	            		clientId,
        	            		secret
        	            ).build()
        	        )
        	        .environment(Environment.SANDBOX)
        	        .build();
        return client;
    }
}
