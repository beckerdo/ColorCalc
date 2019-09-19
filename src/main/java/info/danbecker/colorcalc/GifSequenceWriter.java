// 
//  GifSequenceWriter.java
//  
//  Created by Elliot Kroo on 2009-04-25.
//
// This work is licensed under the Creative Commons Attribution 3.0 Unported
// License. To view a copy of this license, visit
// http://creativecommons.org/licenses/by/3.0/ or send a letter to Creative
// Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
package info.danbecker.colorcalc;

import javax.imageio.*;
import javax.imageio.metadata.*;
import javax.imageio.stream.*;

import java.awt.image.*;
import java.io.*;
import java.util.Iterator;

/**
 * Use GifSequenceWriter to sequence a number of Images
 * or files to an animated Gif.
 * <p>
 * @author <a href="mailto://dan@danbecker.info>Dan Becker</a>
 *
 */
public class GifSequenceWriter {
	protected ImageWriter gifWriter;
	protected ImageWriteParam imageWriteParam;
	protected IIOMetadata imageMetaData;

	/**
	 * Creates a new GifSequenceWriter
	 * 
	 * @param outputStream        the ImageOutputStream to be written to
	 * @param imageType           one of the imageTypes specified in
	 *                            java.awt.image.BufferedImage, e.g. TYPE_INT_ARGB,
	 *                            TYPE_4BYTE_ABGR
	 * @param timeBetweenFramesMS the time between frames in milliseconds
	 * @param loopContinuously    whether the gif should loop repeatedly
	 * @throws IIOException if no gif ImageWriters are found
	 *
	 * @author Elliot Kroo (elliot[at]kroo[dot]net)
	 */
	public GifSequenceWriter(ImageOutputStream outputStream, int imageType, int timeBetweenFramesMS,
			boolean loopContinuously) throws IIOException, IOException {
		// my method to create a writer
		gifWriter = getWriter();
		imageWriteParam = gifWriter.getDefaultWriteParam();
		ImageTypeSpecifier imageTypeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(imageType);

		imageMetaData = gifWriter.getDefaultImageMetadata(imageTypeSpecifier, imageWriteParam);

		String metaFormatName = imageMetaData.getNativeMetadataFormatName();

		IIOMetadataNode root = (IIOMetadataNode) imageMetaData.getAsTree(metaFormatName);

		IIOMetadataNode graphicsControlExtensionNode = getNode(root, "GraphicControlExtension");

		graphicsControlExtensionNode.setAttribute("disposalMethod", "none");
		graphicsControlExtensionNode.setAttribute("userInputFlag", "FALSE");
		graphicsControlExtensionNode.setAttribute("transparentColorFlag", "FALSE");
		graphicsControlExtensionNode.setAttribute("delayTime", Integer.toString(timeBetweenFramesMS / 10));
		graphicsControlExtensionNode.setAttribute("transparentColorIndex", "0");

		IIOMetadataNode commentsNode = getNode(root, "CommentExtensions");
		commentsNode.setAttribute("CommentExtension", "Created by MAH");

		IIOMetadataNode appEntensionsNode = getNode(root, "ApplicationExtensions");

		IIOMetadataNode child = new IIOMetadataNode("ApplicationExtension");

		child.setAttribute("applicationID", "NETSCAPE");
		child.setAttribute("authenticationCode", "2.0");

		int loop = loopContinuously ? 0 : 1;

		child.setUserObject(new byte[] { 0x1, (byte) (loop & 0xFF), (byte) ((loop >> 8) & 0xFF) });
		appEntensionsNode.appendChild(child);

		imageMetaData.setFromTree(metaFormatName, root);

		gifWriter.setOutput(outputStream);

		gifWriter.prepareWriteSequence(null);
	}

	public void writeToSequence(RenderedImage img) throws IOException {
		gifWriter.writeToSequence(new IIOImage(img, null, imageMetaData), imageWriteParam);
	}

	/**
	 * Close this GifSequenceWriter object. This does not close the underlying
	 * stream, just finishes off the GIF.
	 */
	public void close() throws IOException {
		gifWriter.endWriteSequence();
	}

	/**
	 * Returns the first available GIF ImageWriter using
	 * ImageIO.getImageWritersBySuffix("gif").
	 * 
	 * @return a GIF ImageWriter object
	 * @throws IIOException if no GIF image writers are returned
	 */
	private static ImageWriter getWriter() throws IIOException {
		Iterator<ImageWriter> iter = ImageIO.getImageWritersBySuffix("gif");
		if (!iter.hasNext()) {
			throw new IIOException("No GIF Image Writers Exist");
		} else {
			return iter.next();
		}
	}

	/**
	 * Returns an existing child node, or creates and returns a new child node (if
	 * the requested node does not exist).
	 * 
	 * @param rootNode the <tt>IIOMetadataNode</tt> to search for the child node.
	 * @param nodeName the name of the child node.
	 * 
	 * @return the child node, if found or a new node created with the given name.
	 */
	private static IIOMetadataNode getNode(IIOMetadataNode rootNode, String nodeName) {
		int nNodes = rootNode.getLength();
		for (int i = 0; i < nNodes; i++) {
			if (rootNode.item(i).getNodeName().compareToIgnoreCase(nodeName) == 0) {
				return ((IIOMetadataNode) rootNode.item(i));
			}
		}
		IIOMetadataNode node = new IIOMetadataNode(nodeName);
		rootNode.appendChild(node);
		return (node);
	}

	/**
	 * Writes a given array of input file names to an output file name
	 * <p>
	 * Example:
	 * <code>animateGIF( "output.gif", new [] { "input.png", "input.jpg" }, 0, true );</code>
	 * <p>
	 * Issue from
	 * <a href="https://stackoverflow.com/questions/51163881/issue-with-converting-an-arraylist-of-bufferedimages-to-a-gif-using-gifsequencew">
	 * Stack Overflow</a>
	 * <p>
	 * There's a problem with the the GifSequenceWriter when using palette images
	 * (BufferedImage.TYPE_BYTE_INDEXED with IndexColorModel). This will create
	 * metadata based on a default 216 color palette (the web safe palette), which
	 * is clearly different from the colors in your image.
	 * <p>
	 * <pre>
	 * The problematic lines in GifSequenceWriter: 
	 *    ImageTypeSpecifier imageTypeSpecifier =
	 *       ImageTypeSpecifier.createFromBufferedImageType(imageType); 
	 *    imageMetaData = gifWriter.getDefaultImageMetadata(imageTypeSpecifier, imageWriteParam);
	 * </pre>
	 * <p>
	 * You can simply use:
	 * <pre> 
	 *    GifSequenceWriter writer = new GifSequenceWriter(output,
	 *       BufferedImage.TYPE_INT_ARGB, delayTimeMS, true);
	 * </pre>
	 * 
	 * @param outputFileName   name of animated GIF output file
	 * @param inputFileNames   names of Java supported input image files
	 * @param frameDelay       time in milli seconds between frames
	 * @param loopContinuously continuous loop or play once?
	 * @throws IOException
	 */
	public static void animateGIF(String outputFileName, String[] inputFileNames, int frameDelay,
			boolean loopContinuously) throws IOException {
		if (inputFileNames.length > 1) {
			// create a new image output stream with the output file name
			ImageOutputStream output = new FileImageOutputStream(new File(outputFileName));

			// grab the output image type from the first image in the sequence
			BufferedImage firstImage = ImageIO.read(new File(inputFileNames[0]));

			// create a gif sequence with the first image type, given frame delay, and looping
			GifSequenceWriter writer = new GifSequenceWriter(output, firstImage.getType(), 
				frameDelay,	loopContinuously);

			// write out the first image to our sequence...
			writer.writeToSequence(firstImage);

			// write remaining images
			for (int i = 1; i < inputFileNames.length - 1; i++) {
				BufferedImage nextImage = ImageIO.read(new File(inputFileNames[i]));
				writer.writeToSequence(nextImage);
			}

			// close up
			writer.close();
			output.close();
		}
	}
	
	/**
	 * Writes a numbered list of input file names to an output file name. The input
	 * file name is formated with String.format("%d", frameNumber).
	 * <p>
	 * Example:
	 * <code>animateGIF( "output.gif", "input%d.png", 64, 0, true );</code>
	 * 
	 * @param outputFileName   name of animated GIF output file
	 * @param inputFilePattern names of Java supported input image files where a
	 *                         count in substituted into the pattern with
	 *                         String.format("%d", frameNumber)
	 * @param frameCount       the number of input files that match the pattern
	 * @param frameDelay       time in milli seconds between frames
	 * @param loopContinuously continuous loop or play once?
	 * @throws IOException
	 */
	public static void animateGIF( String outputFileName, String inputFileNamePattern, int frameCount, 
		int frameDelay, boolean loopContinuously ) throws IOException {
		
		String [] inputFileNames = new String[ frameCount ];
		for ( int i = 0; i < frameCount; i++) {
			inputFileNames[ i ] = String.format( inputFileNamePattern, i);
		}
		animateGIF( outputFileName, inputFileNames, frameDelay, loopContinuously );		
	}
}