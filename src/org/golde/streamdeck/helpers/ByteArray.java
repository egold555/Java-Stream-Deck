package org.golde.streamdeck.helpers;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Class to modify data easily in a byte array
 * *** METHODS DIRECTLY MODIFY THE DATA! ***
 * @author Eric Golde
 *
 */
public class ByteArray {

	private byte[] data;
	
	private ByteArray(byte[] arr) {
		this.data = arr;
	}
	
	/**
	 * Create a new ByteArray from a primitive array
	 * @param arr primitive byte array
	 * @return a ByteArray
	 */
	public static ByteArray of(byte[] arr) {
		return new ByteArray(arr);
	}
	
	/**
	 * Create a new ByteArray from a ByteBuffer
	 * @param buff a ByteBuffer
	 * @return a ByteArray
	 */
	public static ByteArray of(ByteBuffer buff) {
		return new ByteArray(buff.array());
	}
	
	/**
	 * Creates a empty ByteBuffer with length of {@param length}
	 * @param length length of ByteArray
	 * @return a ByteArray
	 */
	public static ByteArray empty(int length) {
		return new ByteArray(new byte[length]);
	}
	
	/**
	 * Slice off the beginning of the ByteArray
	 * @param amt how much are we slicing off
	 */
	public void slice(int amt) {
		final int len = data.length;

		byte[] tmp = new byte[len - amt];
		
		for(int i = amt; i < len; i++) {
			tmp[i - amt] = data[i];
		}
		
		this.data = tmp;
		
	}
	
	/**
	 * Cut a section out of the ByteArray
	 * @param start where to start
	 * @param end where to end. END IS EXCLUSIVE
	 */
	public void section(int start, int end) {
		
		byte[] tmp = new byte[end - start];
		
		for(int i = start; i < end; i++) {
			tmp[i - start] = data[i];
		}
		
		this.data = tmp;
		
	}
	
	/**
	 * Find the index of a certain byte. If it doesn't exist, -1 is returned
	 * @param o byte we are trying to find
	 * @return index or -1 if not found
	 */
	public int indexOf(byte o) {
		for(int i = 0; i < data.length; i++) {
			if(data[i] == o) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Get a byte at a certain index
	 * @param i index
	 * @return byte at said index
	 */
	public byte get(int i) {
		return data[i];
	}
	
	/**
	 * Set a byte at a certain index
	 * @param i index
	 * @param o byte value
	 */
	public void set(int i, byte o) {
		data[i] = o;
	}
	
	/**
	 * Returns the length of the data
	 * @return length of the underlying array
	 */
	public int getLength() {
		return data.length;
	}
	
	/**
	 * Creates a string based out of the byte array
	 * @return a string converting the byte array to characters
	 */
	public String toStringObj() {
		return new String(data);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return Arrays.toString(data);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Arrays.hashCode(data);
	}
	
	/**
	 * Returns the primitive array
	 * @return
	 */
	public byte[] toPrimitive() {
		return data.clone();
	}
	
}
