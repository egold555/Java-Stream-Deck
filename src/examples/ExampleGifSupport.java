package examples;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.golde.streamdeck.IStreamDeck;
import org.golde.streamdeck.StreamDeckXL;
import org.golde.streamdeck.helpers.Gif;
import org.golde.streamdeck.helpers.ImageUtilities;

/**
 * Example showcasing gifs on a single and mutiple buttons
 * @author Eric Golde
 *
 */
public class ExampleGifSupport extends AbstractExample {

	private Gif singleSquareGif;
	private Gif multiSquareGif;
	
	public ExampleGifSupport(IStreamDeck deck) {
		super(deck);
	}
	
	@Override
	protected void start() {
		try {
			singleSquareGif = new Gif(new File("test-images/graph.gif"));
			singleSquareGif.resize(StreamDeckXL.IMG_SIZE, StreamDeckXL.IMG_SIZE);

			multiSquareGif = new Gif(new File("test-images/cat.gif"));
			multiSquareGif.resize(StreamDeckXL.IMG_SIZE * 2, StreamDeckXL.IMG_SIZE * 2);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void loop() {
		
		deck.setKey(0, singleSquareGif);
		
		//Only split the gif up when we know we need to redraw it.
		if(multiSquareGif.hasTimeElapsed()) {
			BufferedImage[] split = ImageUtilities.splitImage(multiSquareGif.getNextAnimatableFrame(), 2, 2);
			deck.setKey(22, split[0]);
			deck.setKey(23, split[1]);
			deck.setKey(30, split[2]);
			deck.setKey(31, split[3]);
		}
		
	}

	

}
