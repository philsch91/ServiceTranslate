package at.schunker.se;

import at.schunker.se.config.STRequestConfig;
import at.schunker.se.helper.STRequestComparing;
import at.schunker.se.model.STHttpRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.util.CharsetUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.*;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;

public class STHttpServerHandler extends SimpleChannelInboundHandler<Object> {

    private HttpRequest request;
    // Buffer that stores the response content
    private final StringBuilder buf = new StringBuilder();
    protected String requestBody = null;
    protected STHttpRequest incomingRequest = null;

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest request = this.request = (HttpRequest) msg;

            if (HttpUtil.is100ContinueExpected(request)) {
                send100Continue(ctx);
            }

            this.buf.setLength(0);

            this.incomingRequest = new STHttpRequest();
            this.incomingRequest.setProtocolVersion(request.protocolVersion());
            String hostname = request.headers().get(HttpHeaderNames.HOST, "unknown");
            this.incomingRequest.setHostname(hostname);
            this.incomingRequest.setUri(request.uri());

            HttpHeaders headers = request.headers();

            if (!headers.isEmpty()) {
                Hashtable headerstable = new Hashtable<>(headers.size());
                for (Map.Entry<String, String> h : headers) {
                    String key = h.getKey();
                    String value = h.getValue();
                    headerstable.put(key, value);
                }
                this.incomingRequest.setHeaders(headerstable);
            }

            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
            Map<String, List<String>> params = queryStringDecoder.parameters();

            if (!params.isEmpty()) {
                Hashtable queryParameters = new Hashtable<>(params.size());
                for (Map.Entry<String, List<String>> p : params.entrySet()) {
                    String key = p.getKey();
                    List<String> vals = p.getValue();

                    queryParameters.put(key, vals);
                }
                this.incomingRequest.setQueryParameters(queryParameters);
            }

            STHttpServerHandler.appendDecoderResult(buf, request);
        }

        if (msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) msg;

            ByteBuf content = httpContent.content();
            if (content.isReadable()) {
                this.buf.append(content.toString(CharsetUtil.UTF_8));
                this.appendDecoderResult(this.buf, this.request);
            }

            if (msg instanceof LastHttpContent) {
                LastHttpContent trailer = (LastHttpContent) msg;

                if (!trailer.trailingHeaders().isEmpty()) {
                    Hashtable trailingHeaders = new Hashtable<>(trailer.trailingHeaders().size());
                    for (String name: trailer.trailingHeaders().names()) {
                        for (String value: trailer.trailingHeaders().getAll(name)) {
                            trailingHeaders.put(name, value);
                        }
                    }
                    this.incomingRequest.setTrailingHeaders(trailingHeaders);
                }

                this.requestBody = this.buf.toString();

                this.handleRequest(trailer, ctx);

                boolean keepAlive = false;
                keepAlive = this.writeResponse(trailer, ctx);

                if (!keepAlive) {
                    // If keep-alive is off, close the connection once the content is fully written.
                    ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
                }
            }
        }
    }

    private static void appendDecoderResult(StringBuilder buf, HttpObject object) {
        DecoderResult result = object.decoderResult();
        if (result.isSuccess()) {
            return;
        }

        System.err.println(result.cause().getMessage());
    }

    protected boolean handleRequest(HttpObject httpObject, ChannelHandlerContext ctx) {
        DecoderResult result = httpObject.decoderResult();

        if (result.isFailure()) {
            System.err.println(result.cause().getMessage());
            return false;
        }

        try {
            this.handleJsonRequest();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return false;
        }

        return true;
    }

    protected boolean handleJsonRequest() throws Exception {
        LinkedTreeMap<String, String> body = this.decodeJsonBody();
        if (body == null) {
            return false;
        }

        System.err.println("received " + this.incomingRequest.toString());
        System.err.println("received " + this.incomingRequest.getBody().toString());

        Set<Map.Entry<String, STHttpRequest>> inboundRequestSet = STRequestConfig.getConfig().getInboundRequestMap().entrySet();
        for (Map.Entry<String, STHttpRequest> entry : inboundRequestSet) {
            STHttpRequest request = entry.getValue();
            boolean result = STRequestComparing.compareRequests(request, this.incomingRequest);

            if (result) {
                System.err.println("Match found");
                System.err.println(entry.getKey());
                System.err.println(request);
            }
        }

        return false;
        /*
        //compare with config

        URL url = new URL("https://localhost:9999");

        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

        urlConnection.setRequestMethod("POST");
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        urlConnection.setUseCaches(false);
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestProperty("Content-Length", String.valueOf(this.requestBody.length()));

        OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
        writer.write(this.requestBody);
        writer.flush();

        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

        StringBuilder builder = new StringBuilder();
        builder.setLength(0);

        String line = reader.readLine();

        while (line != null) {
            builder.append(line);
            line = reader.readLine();
        }

        String response = builder.toString();
        System.err.println(response);

        writer.close();
        reader.close();

        return true;
        */
    }

    protected LinkedTreeMap<String, String> decodeJsonBody() {
        String contentType = this.incomingRequest.getHeaders().get("Content-Type");
        if (contentType == null || !contentType.equalsIgnoreCase("application/json")) {
            return null;
        }

        JSONObject json = null;

        try {
            json = new JSONObject(this.requestBody);
        } catch (JSONException ex) {
            System.err.println(ex.getMessage());
            return null;
        }

        //HashMap<String, String> body = null;
        LinkedTreeMap<String, String> body = null;

        Gson gson = new GsonBuilder().create();
        //body = gson.fromJson(json.toString(), HashMap.class);
        Type stringStringMapType = new TypeToken<Map<String, String>>(){}.getType();
        body = gson.fromJson(json.toString(), stringStringMapType);
        this.incomingRequest.setBody(body);

        return body;
    }

    private boolean writeResponse(HttpObject currentObj, ChannelHandlerContext ctx) {
        // Decide whether to close the connection or not.
        boolean keepAlive = HttpUtil.isKeepAlive(this.request);

        // Build the response object.
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, currentObj.decoderResult().isSuccess()? OK : BAD_REQUEST,
                Unpooled.copiedBuffer(this.requestBody, CharsetUtil.UTF_8));

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");

        if (keepAlive) {
            // Add 'Content-Length' header only for a keep-alive connection.
            response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
            // Add keep alive header as per:
            // - http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        // Encode the cookie.
        String cookieString = this.request.headers().get(HttpHeaderNames.COOKIE);

        if (cookieString != null) {
            Set<Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieString);
            if (!cookies.isEmpty()) {
                List<Cookie> cookieList = new ArrayList<Cookie>();
                // Reset the cookies if necessary.
                for (Cookie cookie: cookies) {
                    cookies.add(cookie);
                    response.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookie));
                }
                this.incomingRequest.setCookies(cookieList);
            }
        }

        // Write the response.
        ctx.write(response);

        return keepAlive;
    }

    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, CONTINUE, Unpooled.EMPTY_BUFFER);
        ctx.write(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
