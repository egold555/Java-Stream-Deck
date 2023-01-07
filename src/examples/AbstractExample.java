package examples;

import java.util.Random;

import org.golde.streamdeck.IStreamDeck;

public abstract class AbstractExample implements Runnable {

	protected static final Random RANDOM = new Random();
	protected final IStreamDeck deck;
	protected void start() {};
	protected abstract void loop();
	
	public AbstractExample(IStreamDeck deck) {
		this.deck = deck;
	}
	
	@Override
	public final void run() {
		
		start();
		
		while(true) {
			loop();
		}
	}
	
}
