/*
 * Copyright (c) 2016 Kloudtek Ltd
 */

package com.kloudtek.kryptotek;

import com.kloudtek.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.kloudtek.kryptotek.DigestAlgorithm.*;

/**
 * Various digest utilities
 */
public class DigestUtils {
    private static final Logger logger = Logger.getLogger(DigestUtils.class.getName());
    private static final SecureRandom random = new SecureRandom();
    public static final int BUFSZ = 8192;

    /**
     * Create a digest object
     *
     * @param alg Digest algorithm
     * @return Message digest object
     */
    public static Digest digest(DigestAlgorithm alg) {
        return CryptoUtils.engine.digest(alg);
    }

    /**
     * Create a digest from a byte array
     *
     * @param data Data to create digest from
     * @param alg  Algorithm to use for digest
     * @return digest value
     */
    public static byte[] digest(byte[] data, DigestAlgorithm alg) {
        return CryptoUtils.engine.digest(data, alg);
    }

    /**
     * Create a digest from a file
     *
     * @param file File from which to create digest from
     * @param alg  Algorithm to use for digest
     * @return digest value
     * @throws java.io.IOException If an error occurs while reading the file
     */
    public static byte[] digest(File file, DigestAlgorithm alg) throws IOException {
        FileInputStream is = new FileInputStream(file);
        try {
            return digest(is, alg);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }

    /**
     * Read all data from a stream and create a digest from it
     *
     * @param inputStream Data stream
     * @param alg         Algorithm to use for digest
     * @return Digest
     * @throws IOException If an error occurs reading from the stream
     */
    public static byte[] digest(InputStream inputStream, DigestAlgorithm alg) throws IOException {
        byte[] buffer = new byte[BUFSZ];
        Digest digest = digest(alg);
        for (int i = inputStream.read(buffer, 0, BUFSZ); i != -1; i = inputStream.read(buffer, 0, BUFSZ)) {
            digest.update(buffer, 0, i);
        }
        return digest.digest();
    }

    /**
     * Create a salted digest from the provided data
     *
     * @param data Data to create digest from
     * @param alg  Algorithm to use for digest
     * @return Digest
     */
    public static byte[] saltedDigest(byte[] data, DigestAlgorithm alg) {
        return saltedDigest(generateSalt(), data, alg);
    }

    /**
     * Create a salted digest from the provided data
     *
     * @param salt Salt value
     * @param data Data to create digest from
     * @param alg  Algorithm to use for digest
     * @return Digest
     */
    public static byte[] saltedDigest(byte[] salt, byte[] data, DigestAlgorithm alg) {
        Digest sha = digest(alg);
        sha.update(data);
        sha.update(salt);
        byte[] digest = sha.digest();
        byte[] digestWithSalt = new byte[alg.getHashLen() + salt.length];
        System.arraycopy(digest, 0, digestWithSalt, 0, digest.length);
        System.arraycopy(salt, 0, digestWithSalt, digest.length, salt.length);
        return digestWithSalt;
    }

    /**
     * Compare a salted digest to some data.
     *
     * @param digest Salted digest
     * @param data   Data to validate digest again
     * @param alg    Algorithm to use for digest
     * @return True if the data matches the digest
     */
    public static boolean compareSaltedDigest(byte[] digest, byte[] data, DigestAlgorithm alg) {
        Digest sha = digest(alg);
        byte[] digestData = Arrays.copyOfRange(digest, 0, alg.getHashLen());
        byte[] salt = Arrays.copyOfRange(digest, alg.getHashLen(), digest.length);
        sha.update(data);
        sha.update(salt);
        byte[] encoded = sha.digest();
        return compareDigest(digestData, encoded);
    }

    /**
     * Compares two digests. Use this instead of doing it manually or using {@link java.security.MessageDigest#isEqual(byte[], byte[])} to protect
     * against timing attacks (see https://codahale.com/a-lesson-in-timing-attacks/)
     *
     * @param digesta First digest
     * @param digestb Second digest
     * @return True if the digests are the same
     */
    public static boolean compareDigest(byte[] digesta, byte[] digestb) {
        if (digesta.length != digestb.length) {
            return false;
        }
        int result = 0;
        // time-constant comparison
        for (int i = 0; i < digesta.length; i++) {
            result |= digesta[i] ^ digestb[i];
        }
        return result == 0;
    }

    public static boolean compareDigest(byte[] data, DigestAlgorithm algorithm, byte[] digest) {
        return compareDigest(digest(data, algorithm), digest);
    }

    public static boolean compareSha1Digest(byte[] data, byte[] digest) {
        return compareDigest(data, SHA1, digest);
    }

    public static boolean compareSha256Digest(byte[] data, byte[] digest) {
        return compareDigest(data, SHA256, digest);
    }

    public static boolean compareSha512Digest(byte[] data, byte[] digest) {
        return compareDigest(data, SHA512, digest);
    }

    public static boolean compareMD5Digest(byte[] data, byte[] digest) {
        return compareDigest(data, MD5, digest);
    }

    /**
     * Compare a salted digest to some data.
     *
     * @param b64Digest Base 64 encoded Salted digest
     * @param data      Data to validate digest again
     * @param alg       Algorithm to use for digest
     * @return True if the data matches the digest
     */
    public static boolean compareSaltedDigest(String b64Digest, String data, DigestAlgorithm alg) {
        return compareSaltedDigest(StringUtils.base64Decode(b64Digest), data.getBytes(), alg);
    }

    public static byte[] saltedDigest(String text, DigestAlgorithm alg) {
        return saltedDigest(StringUtils.utf8(text), alg);
    }

    public static String saltedB64Digest(String text, DigestAlgorithm alg) {
        return StringUtils.base64Encode(saltedDigest(text, alg));
    }

    public static String saltedB64Digest(byte[] data, DigestAlgorithm alg) {
        return StringUtils.base64Encode(saltedDigest(data, alg));
    }

    public static byte[] sha1(byte[] data) {
        return digest(data, SHA1);
    }

    public static byte[] sha1(InputStream data) throws IOException {
        return digest(data, SHA1);
    }

    public static byte[] sha1(File file) throws IOException {
        return digest(file, SHA1);
    }

    public static byte[] sha256(byte[] data) {
        return digest(data, SHA256);
    }

    public static byte[] sha256(InputStream data) throws IOException {
        return digest(data, SHA256);
    }

    public static byte[] sha256(File file) throws IOException {
        return digest(file, SHA256);
    }

    public static byte[] sha512(byte[] data) {
        return digest(data, SHA512);
    }

    public static byte[] sha512(InputStream data) throws IOException {
        return digest(data, SHA512);
    }

    public static byte[] sha512(File file) throws IOException {
        return digest(file, SHA512);
    }

    public static byte[] md5(byte[] data) {
        return digest(data, MD5);
    }

    public static byte[] md5(InputStream data) throws IOException {
        return digest(data, MD5);
    }

    public static byte[] md5(File file) throws IOException {
        return digest(file, MD5);
    }

    /**
     * Generate a random SALT value of 8 bytes
     *
     * @return Salt data
     */
    private static byte[] generateSalt() {
        final byte[] salt = new byte[8];
        random.nextBytes(salt);
        return salt;
    }
}
