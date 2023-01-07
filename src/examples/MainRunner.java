package examples;

import org.golde.streamdeck.IStreamDeck;
import org.golde.streamdeck.StreamDeckGetter;

/**
 * Main class for examples:
 * 
 * ExampleDetectButtonPress - Showcases detecting button press and releases
 * ExampleGifSupport - Showcases displaying animated GIFs
 * ExmpleImageCoveringDeck - Showcases displaying a image that covers the entire screen
 * ExamplePrintStreamDeckInformation - Prints firmware & Serial number
 * ExampleRandomFlashingSquares - Showcases driving mutiple buttons at once individually
 * ExampleScreenCast - "Cast" your PC screen to the stream deck
 * 
 * @author Eric Golde
 *
 */
public class MainRunner {

	public static void main(String[] args) throws Exception {
		
		System.out.println("Hello World");
		
		//Get the first deck, connect and clear all the buttons
		IStreamDeck deck = StreamDeckGetter.getFirstDeck();
		deck.connect();
		deck.clearDeck();
		
		//TODO: Change the example here!
		AbstractExample example = new ExampleGifSupport(deck);
		
		example.start();
		
		while(true) {
			example.loop();
		}
		
	}
	
}
