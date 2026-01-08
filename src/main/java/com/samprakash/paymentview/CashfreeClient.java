package com.samprakash.paymentview;

import java.io.InputStream;
import java.util.Properties;
import com.cashfree.Cashfree;

public class CashfreeClient {
    private static final Properties props = new Properties();

    static {
        try (InputStream in = CashfreeClient.class.getClassLoader()
                .getResourceAsStream("cashfree.properties")) {
            if (in == null) throw new RuntimeException("cashfree.properties not found!");
            props.load(in);
            
            // Initialize Cashfree Global Config
            Cashfree.XClientId = props.getProperty("cashfree.clientid");
            Cashfree.XClientSecret = props.getProperty("cashfree.clientsecret");
            Cashfree.XEnvironment = Cashfree.SANDBOX; // Change to PRODUCTION for live
            
        } catch (Exception e) {
            throw new RuntimeException("Error initializing Cashfree", e);
        }
    }
    
    public static void init() {
        // Just triggers the static block
    }
}