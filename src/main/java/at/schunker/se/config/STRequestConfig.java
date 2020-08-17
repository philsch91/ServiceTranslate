package at.schunker.se.config;

import at.schunker.se.model.STHttpRequest;

import java.util.Map;

public class STRequestConfig {

    private static STRequestConfig instance;
    private Map<String, STHttpRequest> inboundRequestMap;
    private Map<String, STHttpRequest> outboundRequestMap;

    private STRequestConfig () {}

    public static STRequestConfig getConfig () {
        if (STRequestConfig.instance == null) {
            STRequestConfig.instance = new STRequestConfig();
        }
        return STRequestConfig.instance;
    }

    public Map<String, STHttpRequest> getInboundRequestMap() {
        return this.inboundRequestMap;
    }

    public void setInboundRequestMap(Map<String, STHttpRequest> inboundRequestMap) {
        this.inboundRequestMap = inboundRequestMap;
    }

    public Map<String, STHttpRequest> getOutboundRequestMap() {
        return this.outboundRequestMap;
    }

    public void setOutboundRequestMap(Map<String, STHttpRequest> outboundRequestMap) {
        this.outboundRequestMap = outboundRequestMap;
    }
}
