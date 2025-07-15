package omgplatform.server.utils;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;

/**
 * SSL utility class for development purposes.
 * 
 * This class provides methods to disable SSL verification for self-signed certificates
 * during development. DO NOT use in production.
 *
 * @authors Clement Luo,
 * @date July 14, 2025
 * @since 1.0
 */
public class SSLHelper {
    
    /**
     * Disables SSL verification for development purposes.
     * 
     * This method creates a trust manager that accepts all certificates,
     * allowing connections to servers with self-signed certificates.
     * 
     * WARNING: This should ONLY be used for development with self-signed certificates.
     * Never use this in production as it bypasses all SSL security.
     */
    public static void disableSSLVerification() {
        try {
            // Create a trust manager that trusts all certificates
            TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        // Accept all client certificates
                    }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        // Accept all server certificates
                    }
                }
            };

            // Create an SSL context that uses our trust manager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Set the default SSL socket factory
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

            // Create a hostname verifier that accepts all hostnames
            HostnameVerifier allHostsValid = (hostname, session) -> true;
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Re-enables SSL verification (restores default behavior).
     * 
     * Call this method to restore normal SSL verification after
     * disabling it for development.
     */
    public static void enableSSLVerification() {
        try {
            // Restore default SSL context
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, null, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            
            // Restore default hostname verifier (null restores default behavior)
            HttpsURLConnection.setDefaultHostnameVerifier(null);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 