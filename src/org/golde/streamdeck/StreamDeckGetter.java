package org.golde.streamdeck;

import java.util.ArrayList;
import java.util.List;

import org.hid4java.HidDevice;
import org.hid4java.HidManager;
import org.hid4java.HidServices;

/**
 * Class to create the StreamDeckXL Object
 * @author Eric Golde
 *
 */
public class StreamDeckGetter {

	private StreamDeckGetter() {}

	private static final Integer VENDOR_ID = 0xfd9;
	private static final Integer PRODUCT_ID = 0x6c;

	/**
	 * Get a stream deck XL by its serial number
	 * @param serialNumber serial number of the device
	 * @return the stream deck, or null if no device was found
	 */
	public static IStreamDeck getBySerialNumber(String serialNumber) {
		HidServices hidServices = HidManager.getHidServices();

		hidServices.start();

		HidDevice hidDevice = hidServices.getHidDevice(VENDOR_ID, PRODUCT_ID, serialNumber);

		hidServices.stop();
		return hidDevice == null ? null : new StreamDeckXL(hidDevice);
	}
	
	/**
	 * Get the first stream deck XL we can find, or null if no deck was found.
	 * @return  the first stream deck we find, or null if no deck was found.
	 */
	public static IStreamDeck getFirstDeck() {
		IStreamDeck[] decks = getAllConnectedStreamDecks();
		
		if(decks.length == 0) {
			return null;
		}
		
		return decks[0];
	}

	/**
	 * Get every stream deck that is connected to the device
	 * @return an array of stream decks, or a empty list if none are found.
	 */
	public static IStreamDeck[] getAllConnectedStreamDecks() {
		HidServices hidServices = HidManager.getHidServices();

		hidServices.start();
		
		List<IStreamDeck> decks = new ArrayList<IStreamDeck>();

		for(HidDevice device : hidServices.getAttachedHidDevices()) {
			if(device.getVendorId() == VENDOR_ID && device.getProductId() == PRODUCT_ID) {
				decks.add(new StreamDeckXL(device));
			}
		}
		
		hidServices.stop();
		
		return decks.toArray(new IStreamDeck[0]);
	}

}
