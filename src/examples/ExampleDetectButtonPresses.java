package examples;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.golde.streamdeck.IStreamDeck;
import org.golde.streamdeck.StreamDeckXL;
import org.golde.streamdeck.StreamDeckKeyState;
import org.golde.streamdeck.helpers.ImageUtilities;

/**
 * Example of changing the color of each button when its pressed
 * @author Eric Golde
 *
 */
public class ExampleDetectButtonPresses extends AbstractExample {

	public ExampleDetectButtonPresses(IStreamDeck deck) {
		super(deck);
	}
	
	@Override
	protected void start() {
		
		for(int key = 0; key < StreamDeckXL.NUMBER_OF_KEYS; key++) {
			BufferedImage img = ImageUtilities.createSolidColorImage(Color.BLACK);
			ImageUtilities.drawTextOnImage(img, "" + key, Color.WHITE);
			deck.setKey(key, img);
		}
		
		deck.registerKeyListener((key, state) -> {

			System.out.println("Key: " + key  + ", state: " + state);
			
			if(state == StreamDeckKeyState.State.RELEASED) {
				return;
			}
			
			Color randomColor = Color.getHSBColor(RANDOM.nextFloat(), RANDOM.nextFloat(), 1);
			
			BufferedImage img = ImageUtilities.createSolidColorImage(randomColor);
			ImageUtilities.drawTextOnImage(img, "" + key, ImageUtilities.invertColor(randomColor));

			deck.setKey(key, img);

		});
	}

	@Override
	protected void loop() {
		try {
			Thread.sleep(100);
		} 
		catch (InterruptedException e) {}
	}

	

}
