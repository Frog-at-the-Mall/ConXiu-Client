package com.example.nfcproject.DataRep;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public abstract class Encryption {
    /**
     * @return a new SecretKey for use with AES encryption.
     */
    public static SecretKey generateSecretKey(){
        KeyGenerator keygen;
        SecretKey key;
        try {
            keygen = KeyGenerator.getInstance("AES");
            keygen.init(256);
            key = keygen.generateKey();

        } catch (NoSuchAlgorithmException e) {
            key = null;
            e.printStackTrace();
        }
        return key;
    }

    /**
     * @param bytes bytes to be considered as an AES key
     * @return the key
     */
    public static SecretKey secretKey_from_bytes(byte[] bytes){
        //see https://docs.oracle.com/javase/7/docs/api/javax/crypto/spec/SecretKeySpec.html
        return new SecretKeySpec(bytes, 0, bytes.length, "AES");
    }

    /**
     * @param plaintext: some bytes to encrypt
     * @param key: the key with which to do the encryption
     * @return the encrypted bytes
     */
    protected static byte[] plainTextByteArray_to_encryptedByteArray(byte[] plaintext, SecretKey key)
            throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException,
            BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        return cipher.doFinal(plaintext);
    }

    /**
     * @param ciphertext: encrypted bytes
     * @param key: the decryption key
     * @return the decrypted bytes
     */
    protected static byte[] encryptedByteArray_to_plainTextByteArray(byte[] ciphertext, SecretKey key)
            throws IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        return cipher.doFinal(ciphertext);
    }

}
