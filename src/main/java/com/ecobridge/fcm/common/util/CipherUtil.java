/*
 * Copyright 2023 jinyoonoh@gmail.com (postgresql.co.kr, ecobridge.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ecobridge.fcm.common.util;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class CipherUtil {
    // RSA key size (in bits)
    private static final int RSA_KEY_SIZE = 2048;

    // AES key size (in bits)
    private static final int AES_KEY_SIZE = 256;

    // AES block size (in bytes)
    private static final int AES_BLOCK_SIZE = 16;

    // RSA algorithm
    private static final String RSA_ALGORITHM = "RSA";

    // AES algorithm
    private static final String AES_ALGORITHM = "AES";

    // SHA algorithm
    private static final String SHA_ALGORITHM = "SHA-256";

    // Base64 encoder
    private static final Base64.Encoder base64Encoder = Base64.getEncoder();

    // Base64 decoder
    private static final Base64.Decoder base64Decoder = Base64.getDecoder();

    /**
     * Generate a new RSA public/private key pair.
     *
     * @return the RSA key pair encoded as a Base64 string array
     * @throws NoSuchAlgorithmException if the RSA algorithm is not available
     */
    public static String[] generateRSAKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        keyPairGenerator.initialize(RSA_KEY_SIZE);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        String publicKey = base64Encoder.encodeToString(keyPair.getPublic().getEncoded());
        String privateKey = base64Encoder.encodeToString(keyPair.getPrivate().getEncoded());
        return new String[]{publicKey, privateKey};
    }



    /**
     * Encrypt plaintext with an RSA public key.
     *
     * @param publicKey the RSA public key encoded as a Base64 string
     * @param plaintext the plaintext to be encrypted
     * @return the encrypted ciphertext encoded as a Base64 string
     * @throws NoSuchAlgorithmException if the RSA algorithm is not available
     * @throws InvalidKeySpecException  if the RSA public key is invalid
     * @throws IllegalBlockSizeException if the plaintext is not a multiple of the RSA key size
     * @throws BadPaddingException if the plaintext cannot be padded
     */
    public static String encryptRSA(String publicKey, String plaintext) throws NoSuchAlgorithmException,
            InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, InvalidKeyException {
        byte[] publicKeyBytes = base64Decoder.decode(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        PublicKey rsaPublicKey = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
        byte[] ciphertextBytes = cipher.doFinal(plaintext.getBytes());
        return base64Encoder.encodeToString(ciphertextBytes);
    }

    /**
     * Decrypt ciphertext with an RSA private key.
     *
     * @param privateKey the RSA private key encoded as a Base64 string
     * @param ciphertext the ciphertext to be decrypted
     * @return the decrypted plaintext
     * @throws NoSuchAlgorithmException if the RSA algorithm is not available
     * @throws InvalidKeySpecException  if the RSA private key is invalid
     * @throws IllegalBlockSizeException if the ciphertext is not a multiple of the RSA key size
     * @throws BadPaddingException if the ciphertext cannot be padded
     */
    public static String decryptRSA(String privateKey, String ciphertext) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] privateKeyBytes = base64Decoder.decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        PrivateKey rsaPrivateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
        byte[] plaintextBytes = cipher.doFinal(base64Decoder.decode(ciphertext));
        return new String(plaintextBytes);
    }


    /**
     * Generate a new AES secret key.
     *
     * @param keySize the AES key size (in bits)
     * @return the AES secret key encoded as a Base64 string
     * @throws NoSuchAlgorithmException if the AES algorithm is not available
     */
    public static String generateAESKey(int keySize) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
        keyGenerator.init(keySize);
        SecretKey secretKey = keyGenerator.generateKey();
        return base64Encoder.encodeToString(secretKey.getEncoded());
    }
    /**
     * Generate a new AES secret key.
     * @return the AES secret key encoded as a Base64 string
     * @throws NoSuchAlgorithmException if the AES algorithm is not available
     */
    public static String generateAESKey() throws NoSuchAlgorithmException {
        return generateAESKey(AES_KEY_SIZE);
    }

    /**
     * Generate a new AES initialization vector (IV).
     *
     * @return the AES IV encoded as a Base64 string
     */
    public static String generateAESIV() {
        byte[] iv = new byte[AES_BLOCK_SIZE];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);
        return base64Encoder.encodeToString(iv);
    }


    /**
     * Encrypt plaintext with an AES secret key.
     *
     * @param secretKey the AES secret key encoded as a Base64 string
     * @param plaintext the plaintext to be encrypted
     * @param iv        the AES initialization vector encoded as a Base64 string
     * @return the encrypted ciphertext encoded as a Base64 string
     * @throws NoSuchAlgorithmException           if the AES algorithm is not available
     * @throws InvalidKeyException                if the AES secret key is invalid
     * @throws InvalidAlgorithmParameterException if the AES initialization vector is invalid
     * @throws IllegalBlockSizeException          if the plaintext is not a multiple of the AES block size
     * @throws BadPaddingException                if the plaintext cannot be padded
     */
    public static String encryptAES(String secretKey, String plaintext, String iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] secretKeyBytes = base64Decoder.decode(secretKey);
        byte[] ivBytes = base64Decoder.decode(iv);
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, AES_ALGORITHM);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] ciphertextBytes = cipher.doFinal(plaintext.getBytes());
        return base64Encoder.encodeToString(ciphertextBytes);
    }
    /**
     * Decrypt ciphertext with an AES secret key.
     *
     * @param secretKey   the AES secret key encoded as a Base64 string
     * @param ciphertext  the ciphertext to be decrypted
     * @param iv          the AES initialization vector encoded as a Base64 string
     * @return the decrypted
     */
    public static String decryptAES(String secretKey, String ciphertext, String iv) throws NoSuchAlgorithmException,
            InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException {
        byte[] secretKeyBytes = base64Decoder.decode(secretKey);
        byte[] ivBytes = base64Decoder.decode(iv);
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, AES_ALGORITHM);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] plaintextBytes = cipher.doFinal(base64Decoder.decode(ciphertext));
        return new String(plaintextBytes);
    }

    /**
     * Decrypt ciphertext with an AES secret key.
     *
     * @param input      the input string to be hashed
     * Calculate the SHA-256 hash of the input string.
     * @return the hash of the input string encoded as a Base64 string
     * @throws NoSuchAlgorithmException if the SHA-256 algorithm is not available
     */
    public static String hashSHA256(String input) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(SHA_ALGORITHM);
        messageDigest.update(input.getBytes());
        byte[] hashBytes = messageDigest.digest();
        return base64Encoder.encodeToString(hashBytes);
    }

}

