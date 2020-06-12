package at.schunker.se.model;

import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.cookie.Cookie;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;

/**
 * Considerations:
 * implements io.netty.handler.codec.http.HttpMessage
 */
public class STHttpRequest implements Cloneable {
    protected HttpVersion protocolVersion = null;
    protected String hostname = null;
    protected String url = null;
    protected String uri = null;
    protected Hashtable<String, String> headers = null;
    protected Hashtable<String, List<String>> queryParameters = null;
    protected Hashtable<String, String> trailingHeaders = null;
    protected List<Cookie> cookies = null;
    protected Object body = null;

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

    public Object getBody() {
        return this.body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    // Object

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof STHttpRequest)) return false;
        STHttpRequest that = (STHttpRequest) o;
        return Objects.equals(protocolVersion, that.protocolVersion) &&
                Objects.equals(hostname, that.hostname) &&
                Objects.equals(url, that.url) &&
                Objects.equals(uri, that.uri) &&
                Objects.equals(headers, that.headers) &&
                Objects.equals(queryParameters, that.queryParameters) &&
                Objects.equals(trailingHeaders, that.trailingHeaders) &&
                Objects.equals(cookies, that.cookies) &&
                Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(protocolVersion, hostname, url, uri, headers, queryParameters, trailingHeaders, cookies, body);
    }

    @Override
    public String toString() {
        return "STHttpRequest{" +
                "protocolVersion=" + protocolVersion +
                ", hostname='" + hostname + '\'' +
                ", url='" + url + '\'' +
                ", uri='" + uri + '\'' +
                ", headers=" + headers +
                ", queryParameters=" + queryParameters +
                ", trailingHeaders=" + trailingHeaders +
                ", cookies=" + cookies +
                ", body='" + body + '\'' +
                '}';
    }

    // Cloneable

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
