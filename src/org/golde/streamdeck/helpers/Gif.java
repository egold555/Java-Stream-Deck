package org.golde.streamdeck.helpers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;

/**
 * A class to parse GIFs ad display them on the stream deck at the correct framerate
 * @author Eric Golde
 *
 */
public class Gif {

	private final BufferedImage[] frames;
	private final long delayTime;
	
	private long lastMS = System.currentTimeMillis();
	private int nextFrame = 0;

	/**
	 * Parse a GIF from a file
	 * @param file gif file
	 * @throws IOException thrown if we failed to parse the gif for whatever reason
	 */
	public Gif(File file) throws IOException {
		this.frames = parseFrames(file);
		this.delayTime = parseDelayTime(file);
	}

	/**
	 * Parse the GIF into BufferedImage frames.
	 * @param file GIF file
	 * @return an array of BufferedImages, with the backgrounds fixed.
	 * @throws IOException thrown if we fail to parse the GIF
	 */
	private static final BufferedImage[] parseFrames(File file) throws IOException {
		ImageReader reader = ImageIO.getImageReadersBySuffix("gif").next();
		reader.setInput(ImageIO.createImageInputStream(new FileInputStream(file)), false);

		final int numberOfFrames = reader.getNumImages(true);
		BufferedImage lastImage = reader.read(0);

		BufferedImage[] frameArray = new BufferedImage[numberOfFrames];
		
		frameArray[0] = lastImage; //first frame has all the data. No need to called makeImageForIndxed.

		for (int i = 1; i < numberOfFrames; i++) {
			frameArray[i] = makeImageForIndex(reader, i, lastImage);
		}

		return frameArray;
	}

	/**
	 * Create a image with a fixed background.
	 * GIF standard has the background transparent if the pixel color doesn't change.
	 * @param reader the ImageReader
	 * @param index index of the frame
	 * @param lastImage the last image
	 * @return a correct fixed BufferedImage for the gif
	 * @throws IOException thown if we fail to parse the GIF
	 */
	private static final BufferedImage makeImageForIndex(ImageReader reader, int index, BufferedImage lastImage) throws IOException {
		BufferedImage image = reader.read(index);
		BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);

		if(lastImage != null) {
			newImage.getGraphics().drawImage(lastImage, 0, 0, null);
		}
		newImage.getGraphics().drawImage(image, 0, 0, null);

		return newImage;
	}

	/**
	 * Extract and convert the framerate of the gif, into a millis delay time, so we can animate at the proper speed.
	 * @param file The GIF file
	 * @return the amount of time in MS between frames
	 * @throws IOException thrown if we fail to parse the GIF
	 */
	private static final long parseDelayTime(File file) throws IOException {
		ImageReader reader = ImageIO.getImageReadersBySuffix("gif").next();
		reader.setInput(ImageIO.createImageInputStream(new FileInputStream(file)), false);

		IIOMetadata imageMetaData =  reader.getImageMetadata(0);
		String metaFormatName = imageMetaData.getNativeMetadataFormatName();

		IIOMetadataNode root = (IIOMetadataNode)imageMetaData.getAsTree(metaFormatName);

		IIOMetadataNode graphicsControlExtensionNode = getNode(root, "GraphicControlExtension");

		long millis = Long.parseLong(graphicsControlExtensionNode.getAttribute("delayTime")) * 10;
		
		return millis;
	}

	/**
	 * Helper method to get a node by name
	 * @param rootNode the root node
	 * @param nodeName the node we want to find
	 * @return the node we want. If it doesn't exist, we create and append it to the root.
	 */
	private static final IIOMetadataNode getNode(IIOMetadataNode rootNode, String nodeName) {
		int nNodes = rootNode.getLength();
		for (int i = 0; i < nNodes; i++) {
			if (rootNode.item(i).getNodeName().compareToIgnoreCase(nodeName)== 0) {
				return((IIOMetadataNode) rootNode.item(i));
			}
		}
		IIOMetadataNode node = new IIOMetadataNode(nodeName);
		rootNode.appendChild(node);
		return(node);
	}
	
	/**
	 * Get the millis between frames, so we can animate at the correct speed
	 * @return millis between frames
	 */
	public double getMillisBetweenFrames() {
		return delayTime;
	}
	
	/**
	 * Return all the frames of the gif
	 * @return all the frames of the gif
	 */
	public BufferedImage[] getFrames() {
		return frames;
	}
	
	/**
	 * Get the next frame, once the delayTime has passed.
	 * You can call this method as much as you want in a loop, setting the key to the frame.
	 * @return
	 */
	public BufferedImage getNextAnimatableFrame() {
		
		if(hasTimeElapsed()) {
			lastMS = System.currentTimeMillis();
			nextFrame++;
		}
		
		if(nextFrame >= frames.length) {
			nextFrame = 0;
		}
		
		return frames[nextFrame];
		
	}
	
	/**
	 * Check if time has elapsed to update the frame.
	 * @return
	 */
	public boolean hasTimeElapsed() {
		if(System.currentTimeMillis() - lastMS > delayTime) {
			return true;
		}
		return false;
	}
	
	/**
	 * Resize the GIF to a new width and height.
	 * Helper for loop method that calls {@value ImageUtilities#scaleImage(BufferedImage, int, int) on every frame.
	 * You only need to call this once, when the gif object is created.
	 * @param newWidth new width of the frames
	 * @param newHeight new height of the frames
	 */
	public void resize(int newWidth, int newHeight) {
		for(int i = 0; i < frames.length; i++) {
			frames[i] = ImageUtilities.scaleImage(frames[i], newWidth, newHeight);
		}
	}

}
