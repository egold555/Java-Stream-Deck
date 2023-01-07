package examples;

import org.golde.streamdeck.IStreamDeck;

/**
 * Example printing firmware and serial number to the console
 * @author Eric Golde
 *
 */
public class ExamplePrintStreamDeckInformation extends AbstractExample {

	public ExamplePrintStreamDeckInformation(IStreamDeck deck) {
		super(deck);
	}
	
	@Override
	protected void start() {
		System.out.println("Firmware: " + deck.getFirmwareVersion());
		System.out.println("Serial Number: " + deck.getSerialNumber());
	}

	@Override
	protected void loop() {
		try {
			Thread.sleep(100);
		} 
		catch (InterruptedException e) {}
	}

	

}
