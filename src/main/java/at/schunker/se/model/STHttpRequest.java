package at.schunker.se.model;

import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.cookie.Cookie;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

/**
 * Considerations:
 * implements io.netty.handler.codec.http.HttpMessage
 */
public class STHttpRequest {
    protected HttpVersion protocolVersion = null;
    protected String hostname = null;
    protected String url = null;
    protected String uri = null;
    protected Hashtable<String, String> headers = null;
    protected Hashtable<String, List<String>> queryParameters = null;
    protected Hashtable<String, String> trailingHeaders = null;
    protected List<Cookie> cookies = null;
    protected HashMap<String, String> body = null;

    public STHttpRequest() {}

    public HttpVersion getProtocolVersion() {
        return this.protocolVersion;
    }

    public void setProtocolVersion(HttpVersion protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public String getHostname() {
        return this.hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUri() {
        return this.uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Hashtable<String, String> getHeaders() {
        return this.headers;
    }

    public void setHeaders(Hashtable<String, String> headers) {
        this.headers = headers;
    }

    public Hashtable<String, List<String>> getQueryParameters() {
        return this.queryParameters;
    }

    public void setQueryParameters(Hashtable<String, List<String>> queryParameters) {
        this.queryParameters = queryParameters;
    }

    public Hashtable<String, String> getTrailingHeaders() {
        return this.trailingHeaders;
    }

    public void setTrailingHeaders(Hashtable<String, String> trailingHeaders) {
        this.trailingHeaders = trailingHeaders;
    }

    public List<Cookie> getCookies() {
        return this.cookies;
    }

    public void setCookies(List<Cookie> cookies) {
        this.cookies = cookies;
    }

    public HashMap<String, String> getBody() {
        return this.body;
    }

    public void setBody(HashMap<String, String> body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "STHttpRequest{" +
                "protocolVersion=" + protocolVersion +
                ", hostname='" + hostname + '\'' +
                ", requestUri='" + uri + '\'' +
                ", headers=" + headers +
                ", queryParameters=" + queryParameters +
                ", trailingHeaders=" + trailingHeaders +
                ", cookies=" + cookies +
                ", body='" + body + '\'' +
                '}';
    }
}
