package org.golde.streamdeck;

/**
 * A class to hold the key states of the stream deck XL
 * @author Eric Golde
 *
 */
public class StreamDeckKeyState {

	private boolean[] keyState = new boolean[StreamDeckXL.NUMBER_OF_KEYS];
	private boolean invalid = false;

	//DATA: 
	//    0, 0, 0, 0, [keys]
	//    1, 0, 32, 0, [keys]

	/**
	 * Create a key state object based on the raw data we get from the stream deck
	 * @param rawData
	 */
	public StreamDeckKeyState(byte[] rawData) {
		if (rawData[0] == 0) {
			invalid = true;
		}

		for (int i = 0; i < 32; i++) {
			keyState[i] = rawData[i + 4] == 1;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("StreamDeckKeyState[");
		
		for (int i = 0; i < keyState.length; i++) {
			builder.append(i).append(":").append(keyState[i] ? "down" : "up");
			
			if (i != keyState.length - 1) {
				builder.append(",");
			}
		}
		
		builder.append("]");
		
		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		
		if (obj == null || !(obj instanceof StreamDeckKeyState)) {
			return false;
		}

		StreamDeckKeyState state = (StreamDeckKeyState) obj;
		
		for (int i = 0; i < keyState.length; i++) {
			if (state.getKeyState(i) != this.getKeyState(i)) {
				return false;
			}
		}
		
		return true;
	}

	/**
	 * Is the data invalid?
	 * @return true if the data is invalid.
	 */
	public boolean isInvalid() {
		return invalid;
	}
	
	/**
	 * Button State
	 * @author Eric Golde
	 *
	 */
	public enum State {
		PRESSED, RELEASED;
		
		public static State fromBoolean(boolean state) {
			return state ? PRESSED : RELEASED;
		}
	}

	/**
	 * Return the current state of a given key
	 * @param key key to get the state
	 * @return the current state of the given key
	 */
	public boolean getKeyState(int key) {
		if (key < 0 || key > StreamDeckXL.NUMBER_OF_KEYS) {
			throw new IndexOutOfBoundsException("Expected key to be between 0 and " + (StreamDeckXL.NUMBER_OF_KEYS - 1));
		}
		return keyState[key];
	}
}
