package com.supinfo.supwallet.Presenter.Adapters;

public class IpRowModel{
    private String ip;
    private long latency;

    public IpRowModel(String ip, long latency) {
        this.ip = ip;
        this.latency = latency;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public long getLatency() {
        return latency;
    }

    public void setLatency(long latency) {
        this.latency = latency;
    }
}
