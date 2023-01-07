package org.golde.streamdeck;

import org.golde.streamdeck.StreamDeckKeyState.State;

public interface IStreamDeckListener {
    
	/**
	 * Called whenever a key state is changed.
	 * @param key key that changed
	 * @param state state, wither PRESSED or RELEASED
	 */
    public void keyStateChanged(int key, State state);
}
