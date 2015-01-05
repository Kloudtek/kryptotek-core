/*
 * Copyright (c) 2015 Kloudtek Ltd
 */

package com.kloudtek.kryptotek.key;

import com.kloudtek.ktserializer.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by yannick on 03/01/2015.
 */
public class SubjectKeyIdentifier extends AbstractCustomSerializable {
    private byte[] keyIdentifier;

    public SubjectKeyIdentifier() {
    }

    public SubjectKeyIdentifier(@NotNull byte[] keyIdentifier) {
        this.keyIdentifier = keyIdentifier;
    }

    public byte[] getKeyIdentifier() {
        return keyIdentifier;
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public void serialize(@NotNull SerializationStream os) throws IOException {
        os.write(keyIdentifier);
    }

    @Override
    public void deserialize(@NotNull DeserializationStream is, int version) throws IOException, InvalidSerializedDataException {
        keyIdentifier = is.readRemaining();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SubjectKeyIdentifier that = (SubjectKeyIdentifier) o;

        if (!Arrays.equals(keyIdentifier, that.keyIdentifier)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(keyIdentifier);
    }
}