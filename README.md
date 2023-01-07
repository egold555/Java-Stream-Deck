# Java Stream Deck

Control you're StreamDeck XL v2 from Java!

This has **ONLY BEEN TESTED** with the StreamDeck XL v2. I don't have any other devices to test with. I made this API because there were no API's on GitHub written in Java that worked with the v2 streamdeck xl.

## Getting Started
Get a stream Deck:
```java
//Use one of these 3 functions to get a Stream Deck.
IStreamDeck deck = StreamDeckGetter.getFirstDeck(); //Returns null if not found
IStreamDeck deck = StreamDeckGetter.getBySerialNumber(SERIAL_NUMBER); //Returns null if not found
IStreamDeck[] deck = StreamDeckGetter.getAllConnectedStreamDecks(); //Returns a empty list if none are found

//Connect to the deck
deck.connect();

//Clear the decks buttons
deck.clearDeck();
```

## Example Code
See the examples folder for code examples :)

Examples:
 * `ExampleDetectButtonPress.java` - Showcases detecting button press and releases
 * `ExampleGifSupport.java` - Showcases displaying animated GIFs
 * `ExmpleImageCoveringDeck.java` - Showcases displaying a image that covers the entire screen
 * `ExamplePrintStreamDeckInformation.java` - Prints firmware & Serial number
 * `ExampleRandomFlashingSquare.java` - Showcases driving mutiple buttons at once individually
 * `ExampleScreenCast.java` - "Cast" your PC screen to the stream deck

## Methods
```java
// IStreamDeck.java

/**
 * Connects to the StremaDeck
 * @return Did we successfully connect to it?
 */
public boolean connect();

/**
 * Disconnect from the stream deck
 */
public void disconnect();

/**
 * Get the serial number of the device, from the device its self
 * @return the serial number
 */
public String getSerialNumber();

/**
 * Get the firware version number of the device, from the device its self
 * @return the firmware version number
 */
public String getFirmwareVersion();

/**
 * Fill a key with a specific color
 * @param key key to change
 * @param color Color to fill the screen
 */
public void setKey(int key, Color color);

/**
 * Fill a key with a specific color
 * @param key key to change
 * @param hexColor Color to fill the screen
 */
public void setKey(int key, int hexColor);

/**
 * Write text on a specific key. Font size is automatically handled. The text color is defined by {@value ImageUtilities#getTextColorForBG(Color)}
 * @param key key to change
 * @param text text to write on the key
 * @param backgroundColor background color
 */
public void setKey(int key, String text, ColorbackgroundColor);

/**
 * Write text on a specific key. Font size is automatically handled.
 * @param key key to change
 * @param text text to write on the key
 * @param backgroundColor background color
 * @param textColor text color
 */
public void setKey(int key, String text, ColorbackgroundColor, Color textColor);

/**
 * Set the gif to a buffered image. GIF must be the size defined in the constant {@value StreamDeckXL#IMG_SIZE}
 * You can use {@value Gif#resize(int, int) to resize the gif if needed}
 * You MUST call this function often to have the GIF animate properly.
 * @param key key to change
 * @param img the buffered image
 */
public void setKey(int key, Gif img);

/**
 * Set the key to a buffered image. Image must be the size defined in the constant {@value StreamDeckXL#IMG_SIZE}
 * @param key key to change
 * @param img the buffered image
 */
public void setKey(int key, BufferedImage img);

/**
 * Clear a specific key
 * @param key the key id
 */
public void clearKey(int key);

/**
 * Clear the entire stream deck.
 * Shortcut for doing a for loop over all keys, and calling clearKey(key)
 */
public void clearDeck();

/**
 * Show the stream deck logo on screen
 */
public void resetToLogo();

/**
 * Set the brightness of the display
 * @param percentage 0-100
 */
public void setBrightness(int percentage);

/**
 * Register a IStreamDeckListener, to listen for events
 * @param listener the listener to register
 */
public void registerKeyListener(IStreamDeckListenerlistener);
/**
 * UnRegister a IStreamDeckListener, to no longer have it listen to events
 * @param listener the listener to unregister
 */
public void unRegisterKeyListener(IStreamDeckListenerlistener);
```