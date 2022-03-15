package com.example.nfcproject.DataRep;

import static com.example.nfcproject.DataRep.Byte_Array.concatenate;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

class Capsule {

	protected Capsule innerCapsule(SecretKey key) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
		byte [] unencryptedInnerData = Encryption.encryptedByteArray_to_plainTextByteArray(this.encryptedInnerData, key);
		byte [][] decat = splitFields(unencryptedInnerData);

		byte[] coordsBytes = decat[0];
		double[] coords = Byte_Array.byteArrayToCoords(coordsBytes);
		byte[] messageBytes = decat[1];
		String message = (String) Byte_Array.byteArrayToCharSequence(messageBytes);
		byte[] innerInnerData = decat[2];
		return new Capsule(coords[0], coords[1], message, key, innerInnerData);
	}

	protected Capsule outerCapsule(double lat, double lon, CharSequence message, SecretKey key) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
		return new Capsule(lat, lon, message, key, this.asBytes());
	}

	protected double[] getCoords(){
		return this.latLon;}
	protected CharSequence getMessage(){
		return this.message;}


	private double[] latLon;
	private CharSequence message;
	private byte[] encryptedInnerData;

	/**
	 * Represents a Capsule, which is a layer of nested, encrypted data with a plaintext head
	 * consisting of a pair of doubles and a string
	 * @param lat latitude
	 * @param lon longitude
	 * @param message the meditation
	 * @param innerData the inner capsule, as bytes
	 */
	private Capsule(double lat, double lon, CharSequence message, SecretKey key, byte[] innerData) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
		this.setCoords(lat, lon);
		this.setMessage(message);
		this.setEncryptedInnerData(innerData, key);
	}
	private byte[] asBytes(){
		byte[] coordBytes = Byte_Array.coordsToByteArray(this.latLon[0], this.latLon[1]);
		byte[] messageBytes = Byte_Array.charSequenceToByteArray(this.message);
		byte[] innerBytes = this.encryptedInnerData;
		return mergeFields(coordBytes, messageBytes, innerBytes);
	}
	private void setEncryptedInnerData(byte[] unencryptedInnerData, SecretKey key) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
		this.encryptedInnerData = Encryption.plainTextByteArray_to_encryptedByteArray(unencryptedInnerData, key);
	}
	private void setCoords(double lat, double lon){
		this.latLon = new double[]{lat, lon};
	}
	private void setMessage(CharSequence message){
		this.message = message;
	}
	private byte [][] splitFields(byte[] body) {
		byte[] doublesBytes = new byte[Double.BYTES*2];
		byte[] strBytes = new byte[256-(Double.BYTES*2)];
		byte[] innerCapsuleBytes = new byte[body.length - 256];
		System.arraycopy(body, 0, doublesBytes, 0,Double.BYTES*2);
		System.arraycopy(body, Double.BYTES*2, strBytes, 0, 256-(Double.BYTES*2));
		System.arraycopy(body, 256, innerCapsuleBytes, 0, body.length-256);
		return new byte[][]{doublesBytes, strBytes, innerCapsuleBytes};
	}
	private byte[] mergeFields(byte[] coordBytes, byte[] messageBytes, byte[] innerBytes){
		return concatenate(coordBytes, concatenate(messageBytes, innerBytes));
	}

}