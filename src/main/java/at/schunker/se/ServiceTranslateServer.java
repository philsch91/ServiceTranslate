package at.schunker.se;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Scanner;

public class ServiceTranslateServer {

    static final boolean SSL = System.getProperty("ssl") != null;
    static final int PORT = Integer.parseInt(System.getProperty("port", SSL? "8443" : "8080"));
    static final String CONFIG = System.getProperty("config",null);

    public static void main( String[] args ) throws Exception {
        System.out.println("ServiceTranslateApp");
        ServiceTranslateServer server = new ServiceTranslateServer();
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

    public void readConfig(){
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
    }
}
