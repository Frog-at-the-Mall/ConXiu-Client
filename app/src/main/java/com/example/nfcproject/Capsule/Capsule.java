package com.example.nfcproject.Capsule;

import static com.example.nfcproject.Capsule.Encryption.encryptedByteArray_to_plainTextByteArray;
import static com.example.nfcproject.Capsule.Encryption.plainTextByteArray_to_encryptedByteArray;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;

public abstract class Capsule {

	/** This method is used to ease the process of padding a byte segment to 256 bytes
	 * @param bytes: the segment in need of padding. Must be <= 256 bytes.
	 * @return a byte array 256 in length.
	* */
	private static byte[] padBytes256(byte[] bytes) throws Exception {
		if (bytes.length > 256) throw new Exception("this block is too large!");
		return java.util.Arrays.copyOf(bytes, 256);
	}

	/**
	 * @param outerCapsule: the new data to add to the structure
	 * @param innerCapsule: the already encapsulated data that is getting added to
	 *                    If this is the innermost layer, this should be new byte[0].
	 * @param key: the AES encryption key for this layer
	 * @return an encrypted byte array containing the new data, and the old data.
	 */
	public static byte[] encapsulate (byte[] outerCapsule, byte[] innerCapsule, SecretKey key) throws Exception {
		if (innerCapsule.length % 256 != 0) {
			throw new ShortBufferException();
		}
		outerCapsule = padBytes256(outerCapsule);
		byte[] encapsulation = Byte_Array.concatenate(outerCapsule, innerCapsule);
		return plainTextByteArray_to_encryptedByteArray(encapsulation, key);
	}

	/**
	 *
	 * @param capsule: the structure to decipher and split
	 * @param key: the AES encryption key associated with the outermost layer
	 * @return an array consisting of two byte arrays.
	 * The first of which is plaintext data (coordinates and message),
	 * the rest of which is another capsule waiting for its key.
	 */
	public static byte[][] decapsulate (byte[] capsule, SecretKey key) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
		byte[] deciphered = encryptedByteArray_to_plainTextByteArray(capsule, key);
		return Byte_Array.decatenate(deciphered);
	}
}