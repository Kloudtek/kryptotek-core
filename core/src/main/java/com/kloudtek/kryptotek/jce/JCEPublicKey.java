/*
 * Copyright (c) 2015 Kloudtek Ltd
 */

package com.kloudtek.kryptotek.jce;

import com.kloudtek.kryptotek.EncodedKey;
import com.kloudtek.kryptotek.InvalidKeyEncodingException;
import com.kloudtek.kryptotek.key.RSAPublicKey;

import java.security.InvalidKeyException;
import java.security.PublicKey;

/**
 * Created by yannick on 20/12/2014.
 */
public abstract class JCEPublicKey extends AbstractJCEKey<PublicKey> implements RSAPublicKey {
    public JCEPublicKey() {
    }

    public JCEPublicKey(JCECryptoEngine cryptoEngine, PublicKey publicKey) {
        super(cryptoEngine, publicKey);
    }

    public JCEPublicKey(JCECryptoEngine cryptoEngine, EncodedKey encodedKey) throws InvalidKeyException, InvalidKeyEncodingException {
        super(cryptoEngine, encodedKey);
    }

    public PublicKey getPublicKey() {
        return key;
    }
}