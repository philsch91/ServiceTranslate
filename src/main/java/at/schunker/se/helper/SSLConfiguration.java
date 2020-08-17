package at.schunker.se.helper;

import at.schunker.se.security.TestX509TrustManager;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class SSLConfiguration {

    public static SSLContext initDefaultSSLContext() throws NoSuchAlgorithmException, KeyManagementException {
        TestX509TrustManager testX509TrustManager = new TestX509TrustManager();
        TrustManager[] trustManagers = new TrustManager[] {testX509TrustManager};

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagers, null);

        //SSLContext.setDefault(sslContext);
        return sslContext;
    }

    /**
     * HttpsURLConnection.setSSLSocketFactory(createSSLSocketFactory(crtFile))
     * @param crtFile
     * @return
     * @throws GeneralSecurityException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     */
    public static SSLSocketFactory createSSLSocketFactory(File crtFile) throws GeneralSecurityException, IOException, NoSuchAlgorithmException, KeyStoreException {
        // Create a new trust store, use getDefaultType for .jks files or "pkcs12" for .p12 files
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());

        ks.load(null, null);

        // You can supply a FileInputStream to a .jks or .p12 file and the keystore password as an alternative to loading the crt file
        //FileInputStream in = new FileInputStream("<path to your key store>");
        //ks.load(in, "password".toCharArray());
        //in.close();

        // Read the certificate from disk
        X509Certificate result;
        try (InputStream input = new FileInputStream(crtFile)) {
            result = (X509Certificate) CertificateFactory.getInstance("X509").generateCertificate(input);
        }
        // Add it to the key store
        ks.setCertificateEntry(crtFile.getName(), result);

        // Convert the trust store to trust managers
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);
        TrustManager[] trustManagers = tmf.getTrustManagers();

        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustManagers, null);

        return sslContext.getSocketFactory();
    }
}
