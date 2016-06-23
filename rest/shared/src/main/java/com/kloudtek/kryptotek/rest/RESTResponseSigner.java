/*
 * Copyright (c) 2016 Kloudtek Ltd
 */

package com.kloudtek.kryptotek.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by yannick on 28/10/2014.
 */
public class RESTResponseSigner {
    private String nonce;
    private String requestSignature;
    private int statusCode;
    private byte[] content;

    public RESTResponseSigner() {
    }

    public RESTResponseSigner(String nonce, String requestSignature, int statusCode) {
        this.nonce = nonce;
        this.requestSignature = requestSignature;
        this.statusCode = statusCode;
    }

    public RESTResponseSigner(String nonce, String requestSignature, int statusCode, byte[] content) {
        this.nonce = nonce;
        this.requestSignature = requestSignature;
        this.statusCode = statusCode;
        this.content = content;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getRequestSignature() {
        return requestSignature;
    }

    public void setRequestSignature(String requestSignature) {
        this.requestSignature = requestSignature;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public byte[] getDataToSign() throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        String str = nonce.trim() + "\n" + requestSignature.trim() + "\n" + Integer.toString(statusCode) + "\n";
        buf.write(str.getBytes("UTF-8"));
        if( content != null ) {
            buf.write(content);
        }
        buf.close();
        return buf.toByteArray();
    }
}
