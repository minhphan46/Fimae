package com.example.fimae.adapters.Dating;

import java.io.Serializable;

public class DatingImageAdapterItem implements Serializable {
    String url;
    boolean isLocal;
public DatingImageAdapterItem(String url, boolean isLocal){
        this.url = url;
        this.isLocal = isLocal;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }
}
