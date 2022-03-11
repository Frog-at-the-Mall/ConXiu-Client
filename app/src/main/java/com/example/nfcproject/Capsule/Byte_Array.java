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
	 * @param body: the array to be split into sections.
	 * @return an array of byte arrays. The first element will be the first 256 bytes of the array,
	 * with the remainder as the second element.
	 */
	static byte [][] decatenate(byte[] body) {
		byte[] tail = new byte[body.length - 256];
		byte[] head = new byte[256];
		return new byte[][]{head, tail};
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
		Buffer buf = ByteBuffer.wrap(someBytes);
		return buf;
	}
}