package org.golde.streamdeck;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.MemoryCacheImageOutputStream;

import org.golde.streamdeck.StreamDeckKeyState.State;
import org.golde.streamdeck.helpers.ByteArray;
import org.golde.streamdeck.helpers.Gif;
import org.golde.streamdeck.helpers.ImageUtilities;
import org.hid4java.HidDevice;

/**
 * Implementation of the Stream Deck XL v2
 * @author Eric Golde
 *
 */
public class StreamDeckXL implements IStreamDeck {

	/**
	 * Magic numbers used by commands
	 * @author Eric Golde
	 *
	 */
	private class MagicNumbers {
		public static final byte FIRMWARE_VERSION = 5;
		public static final byte SERIAL_NUMBER = 6;

		public static final byte CUSTOM_COMMAND = 0x03;

		public static final byte RESET_LOGO = 0x02;
		public static final byte SET_BRIGHTNESS = 0x08;

	}

	private static final int COMMAND_ARRAY_LENGTH = 32;

	public static final int IMG_SIZE = 96;
	public static final int IMG_TYPE = BufferedImage.TYPE_INT_RGB;

	private static final int MAX_PACKET_SIZE = 1024;
	private static final int PACKET_HEADER_LENGTH = 8;

	public static final int ROWS = 4;
	public static final int COLS = 8;
	public static final int NUMBER_OF_KEYS = ROWS * COLS;


	private final HidDevice device;

	private ArrayList<IStreamDeckListener> listeners = new ArrayList<IStreamDeckListener>();
	private Thread keyListenTask = null;
	private boolean isListening = false;

	/**
	 * Creates a StreamDeckXL based off a given HID device.
	 * Please use {@value StreamDeckGetter#getFirstDeck()} {@value StreamDeckGetter#getAllConnectedStreamDecks()} or {@value StreamDeckGetter#getBySerialNumber(String)}
	 * @param hidDevice the HID device
	 */
	StreamDeckXL(HidDevice hidDevice) {
		this.device = hidDevice;
	}

	/**
	 * Connects to the StremaDeck
	 * @return Did we successfully connect to it?
	 */
	@Override
	public boolean connect() {

		if(!device.isOpen()) {
			return device.open();
		}

		return device.isOpen();
	}

	/**
	 * Disconnect from the stream deck
	 */
	@Override
	public void disconnect() {
		device.close();
	}

	/**
	 * Get the firware version number of the device, from the device its self
	 * @return the firmware version number
	 */
	@Override
	public String getFirmwareVersion() {
		ByteArray data = this.getFeatureReport(MagicNumbers.FIRMWARE_VERSION);
		data.slice(5);
		int end = data.indexOf((byte) 0);
		if(end != -1) {
			data.section(0, end);
		}
		return data.toStringObj();
	}

	/**
	 * Get the serial number of the device, from the device its self
	 * @return the serial number
	 */
	@Override
	public String getSerialNumber() {
		ByteArray data = this.getFeatureReport(MagicNumbers.SERIAL_NUMBER);
		data.slice(6);
		int end = data.indexOf((byte) 0);
		if(end != -1) {
			data.section(0, end);
		}
		return data.toStringObj();
	}

	/**
	 * Show the stream deck logo on screen
	 */
	@Override
	public void resetToLogo() {
		ByteArray cmd = getEmptyCommand();
		cmd.set(0, MagicNumbers.RESET_LOGO);
		this.sendFeatureReport(MagicNumbers.CUSTOM_COMMAND, cmd);
	}

	/**
	 * Set the brightness of the display
	 * @param percentage 0-100
	 */
	@Override
	public void setBrightness(int percentage) {

		if (percentage < 0 || percentage > 100) {
			throw new IndexOutOfBoundsException("Expected brightness percentage to be between 0 and 100");
		}

		ByteArray cmd = getEmptyCommand();
		cmd.set(0, MagicNumbers.SET_BRIGHTNESS);
		cmd.set(1, (byte) percentage);
		this.sendFeatureReport(MagicNumbers.CUSTOM_COMMAND, cmd);
	}
	
	/**
	 * Clear the entire stream deck.
	 * Shortcut for doing a for loop over all keys, and calling clearKey(key)
	 */
	@Override
	public void clearDeck() {
		for(int i = 0; i < NUMBER_OF_KEYS; i++) {
			clearKey(i);
		}
	}

	/**
	 * Clear a specific key
	 * @param key the key id
	 */
	@Override
	public void clearKey(int key) {
		checkKey(key);
		setKey(key, Color.BLACK);
	}

	/**
	 * Fill a key with a specific color
	 * @param key key to change
	 * @param color Color to fill the screen
	 */
	@Override
	public void setKey(int key, Color color) {
		checkKey(key);
		this.setKey(key, color.getRGB());
	}

	/**
	 * Fill a key with a specific color
	 * @param key key to change
	 * @param hexColor Color to fill the screen
	 */
	@Override
	public void setKey(int key, int hexColor) {
		checkKey(key);
		this.setKey(key, ImageUtilities.createSolidColorImage(hexColor));
	}
	
	/**
	 * Write text on a specific key. Font size is automatically handled. The text color is defined by {@value ImageUtilities#getTextColorForBG(Color)}
	 * @param key key to change
	 * @param text text to write on the key
	 * @param backgroundColor background color
	 */
	@Override
	public void setKey(int key, String text, Color backgroundColor) {
		checkKey(key);
		this.setKey(key, text, backgroundColor, ImageUtilities.getTextColorForBG(backgroundColor));
	}
	
	/**
	 * Write text on a specific key. Font size is automatically handled.
	 * @param key key to change
	 * @param text text to write on the key
	 * @param backgroundColor background color
	 * @param textColor text color
	 */
	@Override
	public void setKey(int key, String text, Color backgroundColor, Color textColor) {
		checkKey(key);
		BufferedImage img = ImageUtilities.createSolidColorImage(backgroundColor);
		ImageUtilities.drawTextOnImage(img, text, textColor);
		this.setKey(key, img);
	}
	
	/**
	 * Set the gif to a buffered image. GIF must be the size defined in the constant {@value StreamDeckXL#IMG_SIZE}
	 * You can use {@value Gif#resize(int, int) to resize the gif if needed}
	 * You MUST call this function often to have the GIF animate properly.
	 * @param key key to change
	 * @param img the buffered image
	 */
	public void setKey(int key, Gif img) {
		
		//Check if time has elapsed. No need to send the same image to the deck twice
		//Unsure if this is needed tbh.
		if(img.hasTimeElapsed()) {
			this.setKey(key, img.getNextAnimatableFrame());
		}
	}

	/**
	 * Set the key to a buffered image. Image must be the size defined in the constant {@value StreamDeckXL#IMG_SIZE}
	 * @param key key to change
	 * @param img the buffered image
	 */
	@Override
	public void setKey(int key, BufferedImage img) {
		checkKey(key);
		synchronized (this) {

			//Rotate the image so it appears correctly on the streamdeck
			AffineTransform at = AffineTransform.getRotateInstance(Math.PI, img.getWidth()/2, img.getHeight()/2.0);
			
			img = ImageUtilities.applyTransform(img, at);

			ByteArrayOutputStream bao = new ByteArrayOutputStream();

			//Make the JPEG we output have 100% quality. Its 70% by default using ImageIO
			ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
			JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(null);
			jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			jpegParams.setCompressionQuality(1f);

			jpgWriter.setOutput(new MemoryCacheImageOutputStream(bao));

			IIOImage outputImage = new IIOImage(img, null, null);
			try {
				jpgWriter.write(null, outputImage, jpegParams);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			finally {
				jpgWriter.dispose();
			}

			byte[] jpegArray = bao.toByteArray();

			ByteArray[] packets = generateImagePackets(key, jpegArray);

			for(ByteArray packet : packets) {
				write(packet);
			}
		}
	}

	/**
	 * Register a IStreamDeckListener, to listen for events
	 * @param listener the listener to register
	 */
	@Override
	public void registerKeyListener(IStreamDeckListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
			if (keyListenTask == null) {
				isListening = true;
				keyListenTask = new Thread(new ThreadKeypressListener());
				keyListenTask.setName("StreamDeck button listener");
				keyListenTask.setDaemon(true);
				keyListenTask.start();
			}
		}
	}

	/**
	 * UnRegister a IStreamDeckListener, to no longer have it listen to events
	 * @param listener the listener to unregister
	 */
	@Override
	public void unRegisterKeyListener(IStreamDeckListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
			if (listeners.isEmpty() && keyListenTask != null) {
				isListening = false;
				keyListenTask.setDaemon(false);
				keyListenTask = null;
			}
		}
	}

	///////////////////////// [ Helpers ] ////////////////////////////////
	
	/**
	 * Double check that the given key is between 0 and {@value #NUMBER_OF_KEYS - 1}
	 * Throws a IndexOutOfBoundsException if the key provided is out of range
	 * @param key the key the user provided
	 */
	private void checkKey(int key) {
		if (key < 0 || key > NUMBER_OF_KEYS) {
			throw new IndexOutOfBoundsException("Expected key to be between 0 and " + (NUMBER_OF_KEYS - 1));
		}
	}
	
	/**
	 * Write data to the device
	 * @param arr ByteArray of data
	 * @param reportId the report id
	 */
	private void write(ByteArray arr, int reportId) {
		this.device.write(arr.toPrimitive(), arr.getLength(), (byte) reportId);
	}

	/**
	 * Write data to the device
	 * @param arr ByteArray of data, with the first byte of the array being the report ID
	 */
	private void write(ByteArray arr) {
		byte reportId = arr.get(0);
		arr.slice(1);
		write(arr, reportId);
	}

	/**
	 * Ask the device for a Feature report, given the report number
	 * @param reportNumber the report number
	 * @return the feature report given the report number
	 */
	private ByteArray getFeatureReport(byte reportNumber) {
		synchronized (this) {
			byte[] arr = new byte[COMMAND_ARRAY_LENGTH];
			device.getFeatureReport(arr, reportNumber);
			return ByteArray.of(arr);
		}
	}

	/**
	 * Send the device a Feature report, given the report number and data
	 * @param reportNumber the report number
	 * @param arr the data to send to the device
	 */
	private void sendFeatureReport(byte reportNumber, ByteArray arr) {
		synchronized (this) {
			device.sendFeatureReport(arr.toPrimitive(), reportNumber);
		}
	}

	/**
	 * Generates a empty ByteArray with the command length specified in {@value #COMMAND_ARRAY_LENGTH}
	 * @return a empty byte array with the correct length for the device
	 */
	private ByteArray getEmptyCommand() {
		return ByteArray.empty(COMMAND_ARRAY_LENGTH);
	}

	/**
	 * Generates an array of image packets to send to the device
	 * @param key the key to change
	 * @param jpegArray the raw jpeg data
	 * @return
	 */
	private static ByteArray[] generateImagePackets(int key, byte[] jpegArray) {

		final int MAX_PAYLOAD_SIZE = MAX_PACKET_SIZE - PACKET_HEADER_LENGTH;

		int remainingBytes = jpegArray.length;

		List<ByteArray> results = new ArrayList<ByteArray>();

		for (int part = 0; remainingBytes > 0; part++) {
			ByteBuffer packet = ByteBuffer.allocate(MAX_PACKET_SIZE);
			int byteCount = Math.min(remainingBytes, MAX_PAYLOAD_SIZE);
			writeFillImageCommandHeader(packet, key, part, remainingBytes <= MAX_PAYLOAD_SIZE, byteCount);
			int byteOffset = jpegArray.length - remainingBytes;
			remainingBytes -= byteCount;

			packet.put(jpegArray, byteOffset, byteCount);

			results.add(ByteArray.of(packet));
		}

		return results.toArray(new ByteArray[0]);

	}
	
	/**
	 * Create the image command header
	 * @param buffer buffer to write data to
	 * @param keyIndex the key index
	 * @param partIndex the part index
	 * @param isLast is this the last one
	 * @param bodyLength length of the body
	 */
	private static void writeFillImageCommandHeader(ByteBuffer buffer, int keyIndex, int partIndex, boolean isLast, int bodyLength) {

		buffer.order(ByteOrder.LITTLE_ENDIAN);

		buffer.put((byte) 0x02);
		buffer.put((byte) 0x07);
		buffer.put((byte) keyIndex);
		buffer.put((byte) (isLast ? 1 : 0));
		buffer.putShort((short) bodyLength);
		buffer.putShort((short) partIndex);

	}

	public class ThreadKeypressListener implements Runnable {

		private StreamDeckKeyState prevState = new StreamDeckKeyState(new byte[NUMBER_OF_KEYS + 4]);

		@Override
		public void run() {
			while(isListening) {

				if(device.isOpen()) {
					byte[] data = new byte[NUMBER_OF_KEYS + 4]; //4 byte header
					device.read(data, 1000);

					synchronized (listeners) {
						if (!listeners.isEmpty()) {
							StreamDeckKeyState state = new StreamDeckKeyState(data);

							if(!state.isInvalid() && !state.equals(prevState)) {
								for(int key = 0; key < NUMBER_OF_KEYS; key++) {
									if(prevState.getKeyState(key) != state.getKeyState(key)) {
										for (IStreamDeckListener listener : listeners) {
											listener.keyStateChanged(key, State.fromBoolean(state.getKeyState(key)));
										}
									}
								}
							}

							if(!state.isInvalid()) {
								prevState = state;
							}
						}
					}
				}

			}
		}

	}

	

}
