package org.golde.streamdeck.helpers;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import org.golde.streamdeck.StreamDeckXL;

/**
 * Class full of image manipulation utilities
 * @author Eric Golde
 *
 */
public class ImageUtilities {

	private ImageUtilities() {}
	
	/**
	 * Invert a given color
	 * @param in color in
	 * @return the color we inputted but inverted
	 */
	public static final Color invertColor(Color in) {
		return new Color(255 - in.getRed(), 255 - in.getGreen(), 255 - in.getBlue());
	}

	/**
	 * Returns BLACK or WHITE based on the luminance of the background color passed
	 * @param bg background color
	 * @return A nice, easy on the eyes text color. Either Black or White
	 */
	public static final Color getTextColorForBG(Color bg) {
		 float luminance = (float) (0.2126 * bg.getRed() + 0.7152 * bg.getGreen() + 0.0722 * bg.getBlue());
		 return (luminance < 140) ? Color.WHITE : Color.BLACK;
	}
	
	/**
	 * Scale a image to a desired width and height
	 * @param in Image in
	 * @param width new width
	 * @param height new height
	 * @return New image with a new size
	 */
	public static final BufferedImage scaleImage(BufferedImage in, int width, int height) {
		Image resultingImage = in.getScaledInstance(width, height, Image.SCALE_DEFAULT);
		BufferedImage outputImage = new BufferedImage(width, height, StreamDeckXL.IMG_TYPE);
		outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
		return outputImage;
	}
	
	/**
	 * Create a solid color image that will fit a stream deck key perfectly
	 * @param color The color of the image
	 * @return A buffered image for the key
	 */
	public static final BufferedImage createSolidColorImage(Color color) {
		return createSolidColorImage(color.getRGB());
	}
	
	/**
	 * Create a solid color image that will fit a stream deck key perfectly
	 * @param hexColor The color of the image
	 * @return A buffered image for the key
	 */
	public static final BufferedImage createSolidColorImage(int hexColor) {
		return createSolidColorImage(StreamDeckXL.IMG_SIZE, StreamDeckXL.IMG_SIZE, hexColor);
	}
	
	/**
	 * Create a solid color image of any size
	 * @param color The color of the image
	 * @return A buffered image of a solid color
	 */
	public static final BufferedImage createSolidColorImage(int width, int height, Color color) {
		return createSolidColorImage(width, height, color.getRGB());
	}
	
	/**
	 * Create a solid color image of any size
	 * @param hexColor The color of the image
	 * @return A buffered image of a solid color
	 */
	public static final BufferedImage createSolidColorImage(int width, int height, int hexColor) {
		BufferedImage img = new BufferedImage(width, height, StreamDeckXL.IMG_TYPE);

		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				img.setRGB(x, y, hexColor);
			}
		}
		
		return img;
	}
	
	/**
	 * Draw text overlaying an existing image. Fon't size is automatically calculated.
	 * @param image existing image
	 * @param text Text to draw
	 * @param textColor the color of the text
	 */
	public static void drawTextOnImage(BufferedImage image, String text, Color textColor) {

        Graphics g = image.getGraphics();
        Rectangle rect = new Rectangle(image.getWidth(), image.getHeight());

        int fontSize = 98;
        FontMetrics metrics;
        do {
            fontSize -= 2;
            g.setFont(g.getFont().deriveFont((float) fontSize));
            metrics = g.getFontMetrics(g.getFont());
        } while (metrics.stringWidth(text) > rect.width);
        
        Font font = g.getFont();
        Color origColor = g.getColor();
        g.setColor(textColor);
        
        
        metrics = g.getFontMetrics(font);
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        g.setFont(font);
        g.drawString(text, x, y);
        
        
        g.setColor(origColor);
        g.dispose();
       
    }
	
	/**
	 * Split a image into mutiple images, based on number of rows and colums
	 * @param image Image to split up
	 * @param rows Number of rows to split the image into
	 * @param cols Number of colums to split the image into
	 * @return An array of buffered images. Rows than columns.
	 */
	public static final BufferedImage[] splitImage(BufferedImage image, int rows, int cols) {

		final int chunks = rows * cols;
		int chunkWidth = image.getWidth() / cols; // determines the chunk width and height
		int chunkHeight = image.getHeight() / rows;
		int count = 0;
		BufferedImage imgs[] = new BufferedImage[chunks]; //Image array to hold image chunks
		for (int x = 0; x < rows; x++) {
			for (int y = 0; y < cols; y++) {
				//Initialize the image array with image chunks
				imgs[count] = new BufferedImage(chunkWidth, chunkHeight, image.getType());

				// draws the image chunk
				Graphics2D gr = imgs[count++].createGraphics();
				gr.drawImage(image, 0, 0, chunkWidth, chunkHeight, chunkWidth * y, chunkHeight * x, chunkWidth * y + chunkWidth, chunkHeight * x + chunkHeight, null);
				gr.dispose();
			}
		}
		return imgs;
	}

	/**
	 * Apply a AffineTransform to a buffered image
	 * @param image Buffered image in
	 * @param at The transform we want to do to the image
	 * @return Returns a new image with the transform applied
	 */
	public static final BufferedImage applyTransform(BufferedImage image, AffineTransform at) {
		BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = newImage.createGraphics();
		g.transform(at);
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return newImage;
	}
	
}
