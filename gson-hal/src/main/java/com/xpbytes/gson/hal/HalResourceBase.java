package com.xpbytes.gson.hal;

// TODO _links, _embedded reserved
@HalResource
public class HalResourceBase {

    @HalLink( name = "self" )
    private String halSelfReference;

    public String getHalSelfReference() {
        return halSelfReference;
    }

    public void setHalSelfReference( String halSelfReference ) {
        this.halSelfReference = halSelfReference;
    }
}
