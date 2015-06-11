package com.xpbytes.gson.hal;

import java.net.URI;

public class HalLinkObject {

    public HalLinkObject() {}

    public URI href;
    public boolean templated;
    public String type;
    public String name;
    public URI profile;
    public URI deprecation;
    public String title;
    public String hreflang;

    public URI getHref() {
        return href;
    }
}
