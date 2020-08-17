package at.schunker.se;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.awt.print.Book;

public class ServiceTranslateServerTest {
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    @Test
    public void testBoolean_parseStringFalse() {
        assertFalse(Boolean.parseBoolean("false"));
    }

    @Test
    public void testBoolean_parseStringTrue() {
        assertTrue(Boolean.parseBoolean("true"));
    }

    //@Test
    public void testServiceTranslateServer() throws Exception {
        System.setProperty("ssl", "true");
        String isSSLActiveString = System.getProperty("ssl", "true");
        boolean isSSLActive = Boolean.parseBoolean(isSSLActiveString);

        System.setProperty("port", isSSLActive? "8443" : "8080");
        System.setProperty("config", "config.json");
        //System.setProperty("javax.net.ssl.keyStore", null);
        //System.setProperty("javax.net.ssl.trustStore", null);

        ServiceTranslateServer server = new ServiceTranslateServer();
        server.setup();
        server.readConfig();
        server.bootstrap();

        assertTrue(true);
    }
}
