package tsudon.image;

import java.awt.Canvas;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageProducer;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;

import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritablePixelFormat;


public class GIFSaver {
	
	static public BufferedImage JavaFXImageToAwtImage(javafx.scene.image.Image img) {
		int width = (int) img.getWidth(),height = (int) img.getHeight();
		BufferedImage bf = new BufferedImage (width,height,BufferedImage.TYPE_4BYTE_ABGR);

		WritablePixelFormat<IntBuffer> wpf = PixelFormat.getIntArgbInstance();

		int pix[] = new int [width*height];
		img.getPixelReader().getPixels(0, 0, width, height,wpf, pix, 0, width);

		java.awt.Image awtImage;
		
		Component ap = new Canvas();
		awtImage = ap.createImage( (ImageProducer) new MemoryImageSource(width,height,pix,0,width));
		Graphics gc = bf.getGraphics();
		gc.drawImage(awtImage, 0, 0, null);
		return bf;
	}
	
	static public void AnimationGIFWriteFile(String outpath, javafx.scene.image.Image[] imgs, int delays[]) throws IOException {
		int maxframe = imgs.length;
		int delay = delays[0];
//create writer
		File outFile = new File(outpath);
		if (outFile.exists()) {
			throw new IOException("File is already exist.");
		}
		Iterator<ImageWriter> itw = ImageIO.getImageWritersByFormatName("gif");
		ImageWriter writer = itw.hasNext() ? itw.next() : null;
		ImageOutputStream outStream = ImageIO.createImageOutputStream(outFile);
		if (writer == null) {
			throw new IOException();
		}

//Read 1st image for getting Width and Height.
		
		BufferedImage image = JavaFXImageToAwtImage(imgs[0]);
		
		//make metadata for Animation GIF
		ImageWriteParam iwp = writer.getDefaultWriteParam();
		IIOMetadata meta = writer.getDefaultImageMetadata(new ImageTypeSpecifier(image), iwp);
		String format = meta.getNativeMetadataFormatName();
		IIOMetadataNode root = (IIOMetadataNode) meta.getAsTree(format);

		IIOMetadataNode appExt = new IIOMetadataNode("ApplicationExtensions");
		root.appendChild(appExt);

		IIOMetadataNode appNode = new IIOMetadataNode("ApplicationExtension");
		appExt.appendChild(appNode);

		appNode.setAttribute("applicationID", "NETSCAPE");
		appNode.setAttribute("authenticationCode", "2.0");
		appNode.setUserObject(new byte[] { 0x1, 0x0, 0x0 });

		IIOMetadataNode node = new IIOMetadataNode("GraphicControlExtension");
		root.appendChild(node);

		node.setAttribute("disposalMethod", "none");
		node.setAttribute("userInputFlag", "FALSE");
		node.setAttribute("transparentColorFlag", "FALSE");
//Ugoira time scale is 1 millisecond, but GIF time scale is 10 millisecond.         
		node.setAttribute("delayTime", Integer.toString((delay + 5) / 10));
		node.setAttribute("transparentColorIndex", "0");
		root.appendChild(node);

		meta.setFromTree(format, root);

		writer.setOutput(outStream);
		writer.prepareWriteSequence(null);
//write 1st frame image
		writer.writeToSequence(new IIOImage(image, null, meta), null);

		int frametime;
		int nextFrametime = delay;
//If this node do not remove,Writer may be not able to write next frame header.
		root.removeChild(appNode);

		// Read 2nd image and after images
		for (int i = 1; i < maxframe; i++) {
			delay = delays[i];
			image = JavaFXImageToAwtImage(imgs[i]);
			frametime = nextFrametime;
			nextFrametime += delay;
			delay = nextFrametime - frametime;
			node.setAttribute("delayTime", Integer.toString((delay + 5) / 10));
			meta.setFromTree(format, root);
			writer.writeToSequence(new IIOImage(image, null, meta), null);
		}
		writer.endWriteSequence();
		outStream.close();

	}

	static public void GIFWriteFile(String outpath, javafx.scene.image.Image[] img) throws IOException {
	}
}
