package com.example.nfcproject.Capsule;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public abstract class Byte_Array {
	/**
	 * @param head: the bytes to begin the array with
	 * @param tail: the rest of the byte array
	 * @return a combination of the head and tail, as one array.
	 */
	public static byte[] concatenate(byte[] head, byte[] tail){
		byte[] body = new byte[head.length + tail.length];
		System.arraycopy(head, 0, body, 0, head.length);
		if (body.length - head.length >= 0)
			System.arraycopy(tail, 0, body, head.length, tail.length);
		return body;
	}

	/**
	 * @param someBytes: characters encoded as bytes
	 * @return the characters, decoded.
	 */
	public static CharSequence byteArrayToCharSequence(byte[] someBytes){
		return new String(someBytes, StandardCharsets.UTF_8);
	}

	/**
	 * @param someChars: a string of characters
	 * @return the characters encoded as bytes
	 */
	public static byte[] charSequenceToByteArray(CharSequence someChars){
	        return someChars.toString().getBytes(StandardCharsets.UTF_8);
	    }

	/**
	 * @param someDouble: a double to encode as bytes
	 * @return the bytes representing the double
	 */
	public static byte[] doubleToByteArray(double someDouble){
		ByteBuffer doubleBytes = ByteBuffer.allocate(Double.BYTES);
		doubleBytes.putDouble(someDouble);
		return doubleBytes.array();
	}

	/**
	 * @param lat latitude
	 * @param lon longitude
	 * @return a byte array representing lat, lon
	 */
	public static byte[] coordsToByteArray(double lat, double lon){
		return concatenate(doubleToByteArray(lat), doubleToByteArray(lon));
	}

	/**
	 * @param coordBytes a byte array representing lat, lon
	 * @return an array of doubles representing {lat, lon}
	 */
	public static double[] byteArrayToCoords(byte[] coordBytes){
		byte[] latBytes = new byte[Double.BYTES];
		byte[] lonBytes = new byte[Double.BYTES];
		for (int i = 0; i < Double.BYTES; i++) {
			latBytes[i] = coordBytes[i];
			lonBytes[i] = coordBytes[i+Double.BYTES];
		}
		return new double [] {byteArrayToDouble(latBytes), byteArrayToDouble(lonBytes)};
	}

	/**
	 * @param someBytes: bytes representing a double.
	 * @return the double, decoded.
	 */
	public static double byteArrayToDouble(byte[] someBytes){
		return ByteBuffer.wrap(someBytes).getDouble();
	}

	/**
	 * @param someBytes: bytes data
	 * @return A byte buffer containing the byte data
	 */
	public static Buffer byteArrrayToBuffer(byte [] someBytes) {
		return ByteBuffer.wrap(someBytes);
	}

	/** This method is used to ease the process of padding a byte segment to 255 bytes
	 * @param bytes: the segment in need of padding. Must be <= 255 bytes.
	 * @return a byte array 256 in length.
	 * */
	protected static byte[] padBytes256(byte[] bytes) throws Exception {
		if (bytes.length > 256) throw new Exception("this block is too large!");
		return java.util.Arrays.copyOf(bytes, 256);
	}
}