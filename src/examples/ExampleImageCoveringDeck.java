package examples;

import java.awt.AWTException;
import java.awt.image.BufferedImage;

import org.golde.streamdeck.IStreamDeck;
import org.golde.streamdeck.StreamDeckXL;
import org.golde.streamdeck.helpers.ImageUtilities;

/**
 * Example of a image covering the entire stream deck
 * @author Eric Golde
 *
 */
public class ExampleImageCoveringDeck extends AbstractExample {

	public ExampleImageCoveringDeck(IStreamDeck deck) throws AWTException {
		super(deck);
	}

	@Override
	protected void start() {

		//create a rainbow image, that is exactly 96px * the amount of keys
		int width = StreamDeckXL.COLS * StreamDeckXL.IMG_SIZE;
		int height = StreamDeckXL.ROWS * StreamDeckXL.IMG_SIZE;

		int[] data = new int[width * height];
		int index = 0;
		for (int i = 0; i < height; i++) {
			int red = (i * 255) / (height - 1);
			for (int j = 0; j < width; j++) {
				int green = (j * 255) / (width - 1);
				int blue = 128;
				data[index++] = (red << 16) | (green << 8) | blue;
			}
		}

		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		img.setRGB(0, 0, width, height, data, 0, width);

		//split the image
		BufferedImage[] splitImages = ImageUtilities.splitImage(img, StreamDeckXL.ROWS, StreamDeckXL.COLS);
		for(int i = 0; i < StreamDeckXL.NUMBER_OF_KEYS; i++) {
			// Get part of screenshot corresponding to key, and set each key to it
			BufferedImage image = splitImages[i];
			deck.setKey(i, image);
		}
	}


	@Override
	protected void loop() {

	}

}
