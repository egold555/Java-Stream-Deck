package examples;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import org.golde.streamdeck.IStreamDeck;
import org.golde.streamdeck.StreamDeckXL;
import org.golde.streamdeck.helpers.ImageUtilities;

/**
 * Example to showcase screen capturing and displaying on all the buttons on th streamdeck
 * @author Eric Golde
 *
 */
public class ExampleScreenCast extends AbstractExample {

	private final Robot robot;
	
	public ExampleScreenCast(IStreamDeck deck) throws AWTException {
		super(deck);
		robot = new Robot();
	}


	@Override
	protected void loop() {
		BufferedImage screenshot = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
		BufferedImage outputImage = ImageUtilities.scaleImage(screenshot, StreamDeckXL.COLS * StreamDeckXL.IMG_SIZE, StreamDeckXL.ROWS * StreamDeckXL.IMG_SIZE);

		BufferedImage[] splitImages = ImageUtilities.splitImage(outputImage, StreamDeckXL.ROWS, StreamDeckXL.COLS);
		for(int i = 0; i < StreamDeckXL.NUMBER_OF_KEYS; i++)
		{
			// Get part of screenshot corresponding to key
			BufferedImage image = splitImages[i];
			// Set the key bitmap
			deck.setKey(i, image);
		}
	}

}
