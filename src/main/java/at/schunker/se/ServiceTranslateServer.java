package at.schunker.se;

import at.schunker.se.helper.SSLConfiguration;
import at.schunker.se.model.STHttpRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ServiceTranslateServer {

    static final boolean SSL = System.getProperty("ssl") != null;
    static final int PORT = Integer.parseInt(System.getProperty("port", SSL? "8443" : "8080"));
    static final String CONFIG = System.getProperty("config",null);
    static final String KEYSTORE = System.getProperty("javax.net.ssl.keyStore", null);
    static final String TRUSTSTORE = System.getProperty("javax.net.ssl.trustStore", null);

    public static void main( String[] args ) throws Exception {
        System.out.println("ServiceTranslateApp");

        ServiceTranslateServer server = new ServiceTranslateServer();
        server.setup();
        server.readConfig();
        server.bootstrap();
    }

    public ServiceTranslateServer(){}

    public void bootstrap() throws Exception {
        // Configure SSL.
        final SslContext sslCtx;

        if (SSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(),
                    ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }

        // Configure the server.
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup);
            b.channel(NioServerSocketChannel.class);
            b.handler(new LoggingHandler(LogLevel.INFO));
            b.childHandler(new HttpServerInitializer(sslCtx));

            Channel ch = b.bind(PORT).sync().channel();

            System.err.println("Open your web browser and navigate to " +
                    (SSL? "https" : "http") + "://127.0.0.1:" + PORT + '/');
            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void setup() throws KeyManagementException, NoSuchAlgorithmException {
        if (KEYSTORE == null && TRUSTSTORE == null) {
            SSLContext sc = SSLConfiguration.initDefaultSSLContext();
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            return;
        }

        //TODO: read keystore and call SSLConfiguration.createSSLSocketFactory()
    }

    public void readConfig() {
        if (CONFIG == null) {
            System.err.println("-Dconfig=<path> is missing");
            return;
        }

        File configFile = new File(CONFIG);
        if (!configFile.exists()){
            System.err.println(CONFIG + " not existing");
            return;
        }

        String jsonText = null;

        try {
            InputStream is = new FileInputStream(CONFIG);
            jsonText = IOUtils.toString(is, "UTF-8");
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return;
        }

        System.err.println(jsonText);
        JSONObject json = new JSONObject(jsonText);
        String a = json.getString("test");
        System.err.println(a);

        Gson gson = new GsonBuilder().create();
        //STHttpRequest httpRequest = gson.fromJson(json.getJSONObject("inbound").getJSONObject("request1").toString(), STHttpRequest.class);
        Map<String, STHttpRequest> requestList = gson.fromJson(json.getJSONObject("inbound").toString(), new TypeToken<Map<String, STHttpRequest>>(){}.getType());

        System.err.println(requestList.toString());
        System.err.println(requestList.get("request1").toString());
    }
}
