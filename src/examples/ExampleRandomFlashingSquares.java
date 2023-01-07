package examples;

import java.awt.Color;

import org.golde.streamdeck.IStreamDeck;
import org.golde.streamdeck.StreamDeckXL;

/**
 * Example of changing each square to a different color
 * @author Eric Golde
 *
 */
public class ExampleRandomFlashingSquares extends AbstractExample {

	public ExampleRandomFlashingSquares(IStreamDeck deck) {
		super(deck);
	}

	@Override
	protected void loop() {
		for(int i = 0; i < StreamDeckXL.NUMBER_OF_KEYS; i++) {
			deck.setKey(i, new Color(RANDOM.nextInt(255), RANDOM.nextInt(255), RANDOM.nextInt(255)));
		}
	}

	

}
