package com.ecobridge.fcm.server;

import com.ecobridge.fcm.common.util.CipherUtil;
import org.junit.jupiter.api.Test;

public class CipherTest {
    @Test
    public void cipherTest() throws Exception {
        // RSA encryption and decryption example
        String[] rsaKeyPair = CipherUtil.generateRSAKeyPair();
        String publicKey = rsaKeyPair[0];
        String privateKey = rsaKeyPair[1];
        String plaintext = "RSA 평문입니다.";
        String ciphertext = CipherUtil.encryptRSA(publicKey, plaintext);
        String decryptedPlaintext = CipherUtil.decryptRSA(privateKey, ciphertext);
        System.out.println("RSA encryption and decryption example:");
        System.out.println("Public key: " + publicKey);
        System.out.println("Private key: " + privateKey);
        System.out.println("Plaintext: " + plaintext);
        System.out.println("Ciphertext: " + ciphertext);
        System.out.println("Decrypted plaintext: " + decryptedPlaintext);
        System.out.println();

        // AES encryption and decryption example
        String secretKey = CipherUtil.generateAESKey();
        String iv = CipherUtil.generateAESIV();
        plaintext = "AES 평문입니다.";
        ciphertext = CipherUtil.encryptAES(secretKey, plaintext, iv);
        decryptedPlaintext = CipherUtil.decryptAES(secretKey, ciphertext, iv);
        System.out.println("AES encryption and decryption example:");
        System.out.println("Secret key: " + secretKey);
        System.out.println("IV: " + iv);
        System.out.println("Plaintext: " + plaintext);
        System.out.println("Ciphertext: " + ciphertext);
        System.out.println("Decrypted plaintext: " + decryptedPlaintext);
        System.out.println();

        // SHA-256 hash example
        String input = "This is a string to be hashed.";
        String hash = CipherUtil.hashSHA256(input);
        System.out.println("SHA-256 hash example:");
        System.out.println("Input: " + input);
        System.out.println("Hash: " + hash);
        System.out.println();
    }

}
