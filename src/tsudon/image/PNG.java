package tsudon.image;
/*
 *  tsudon image library 0.0.0.1 2018/08/24
 *  PNG interface create full color saver only
 *  feature  - loader - indexed color and grey scale saver/ filler ,interlace mode support
 * 
 */

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Deflater;
import javafx.scene.image.Image;

public interface PNG {
	static public byte[] getPNGSignature() {
		return new byte[] { (byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47, (byte) 0x0D, (byte) 0x0A, (byte) 0x1A,
				(byte) 0x0A };
	}

	static public final int HEADER_SIZE = 4;

	public static PNGChunk createIHDR(int width, int height, int bitdepth, int colortype, int filter, int interlace) {
		PNGChunk header = new PNGChunk(ChunkTYPE.IHDR);
		byte[] buffer = new byte[header.getLength()];
		buffer[0] = (byte) (width >>> 24 & 0xff);
		buffer[1] = (byte) (width >>> 16 & 0xff);
		buffer[2] = (byte) (width >>> 8 & 0xff);
		buffer[3] = (byte) (width >>> 0 & 0xff);
		buffer[4] = (byte) (height >>> 24 & 0xff);
		buffer[5] = (byte) (height >>> 16 & 0xff);
		buffer[6] = (byte) (height >>> 8 & 0xff);
		buffer[7] = (byte) (height >>> 0 & 0xff);
		buffer[8] = (byte) bitdepth;
		buffer[9] = (byte) colortype;
		buffer[10] = 0; // compress type a
		buffer[11] = (byte) filter;
		buffer[12] = (byte) interlace;
		header.setBuffer(buffer);

		return header;
	}

	public static PNGChunk createIHDR(int width, int height) {
		return createIHDR(width, height, 8, 6, 0, 0);
	}

	public static PNGChunk createIHDR(int width, int height, boolean withalpha) {
		if (withalpha) {
			return createIHDR(width, height, 8, 6, 0, 0);
		} else {
			return createIHDR(width, height, 8, 2, 0, 0);
		}
	}

	public static PNGChunk createIEND() {
		return new PNGChunk(ChunkTYPE.IEND);
	}

	public static byte[] createJavaFXImageToPNGFrameRGB(Image img) {
		int width = (int) img.getWidth();
		int height = (int) img.getHeight();
		int raw = width * 3 + 1; // add Filter Byte(1byte)

		byte[] buffer = new byte[raw * height];
		for (int y = 0; y < height; y++) {
			int offset = y * raw;
			buffer[offset++] = 0; // scan line first byte is Filter Byte(1byte) /zero because no use filter
			for (int x = 0; x < width; x++) {
				int color = img.getPixelReader().getArgb(x, y);
				buffer[offset++] = (byte) ((color >>> 16) & 0xff); // R
				buffer[offset++] = (byte) ((color >>> 8) & 0xff); // G
				buffer[offset++] = (byte) ((color >>> 0) & 0xff); // B
			}
		}
		return buffer;
	}

	public static byte[] createJavaFXImageToPNGFrameRGBA(Image img) {
		int width = (int) img.getWidth();
		int height = (int) img.getHeight();
		int raw = width * 4 + 1; // add Filter Byte(1byte)

		byte[] buffer = new byte[raw * height];
		for (int y = 0; y < height; y++) {
			int offset = y * raw;
			buffer[offset++] = 0; // scan line first byte is Filter Byte(1byte) /zero because no use filter
			for (int x = 0; x < width; x++) {
				int color = img.getPixelReader().getArgb(x, y);
				buffer[offset++] = (byte) ((color >>> 16) & 0xff); // R
				buffer[offset++] = (byte) ((color >>> 8) & 0xff); // G
				buffer[offset++] = (byte) ((color >>> 0) & 0xff); // B
				buffer[offset++] = (byte) ((color >>> 24) & 0xff); // A
			}
		}
		return buffer;
	}

	public static PNGChunk createIDAT(Image img) {
		return createIDAT(img, true);
	}

	public static PNGChunk createIDAT(Image img, boolean withalpha) {
		PNGChunk data = new PNGChunk(ChunkTYPE.IDAT);
		byte[] buffer;
		if (withalpha) {
			buffer = createJavaFXImageToPNGFrameRGBA(img);
		} else {
			buffer = createJavaFXImageToPNGFrameRGB(img);
		}
		byte[] outbuffer = new byte[buffer.length];
		Deflater encoder = new Deflater();
		encoder.setInput(buffer);
		encoder.finish(); // must need
		int compresslength = encoder.deflate(outbuffer);
		data.setBuffer(outbuffer);
		data.setLength(compresslength);
		return data;
	}

	public static PNGChunk[] createIDATs(Image img, int buffersize, boolean withalpha) {
		byte[] buffer;
		List<PNGChunk> list = new ArrayList<PNGChunk>();

		if (withalpha) {
			buffer = createJavaFXImageToPNGFrameRGBA(img);
		} else {
			buffer = createJavaFXImageToPNGFrameRGB(img);
		}

		Deflater encoder = new Deflater();
		encoder.setInput(buffer);
		encoder.finish();

		int compresslength;
		do {
			byte[] outbuffer = new byte[buffersize];
			PNGChunk data = new PNGChunk(ChunkTYPE.IDAT);
			compresslength = encoder.deflate(outbuffer);
			data.setBuffer(outbuffer);
			data.setLength(compresslength);

			if (compresslength != 0) {
				list.add(data);
			}
		} while (compresslength != 0);

		PNGChunk[] chunks = new PNGChunk[list.size()];
		list.toArray(chunks);

		return chunks;
	}

// These method and class are not tested.
	public static PNGChunk createPLTE(int size, byte[] pallet) {
		return PNGPallet.createPalletChunk(size, pallet);
	}

//APNG
	public static PNGChunk createACTL(int length, int loopcount) {
		PNGChunk chunk = new PNGChunk(ChunkTYPE.acTL);
		byte[] buf = new byte[chunk.getLength()]; // 8
		buf[0] = (byte) (length >>> 24 & 0xff);
		buf[1] = (byte) (length >>> 16 & 0xff);
		buf[2] = (byte) (length >>> 8 & 0xff);
		buf[3] = (byte) (length >>> 0 & 0xff);
		buf[4] = 0;
		buf[5] = 0;
		buf[6] = 0;
		buf[7] = 0;
		chunk.setBuffer(buf);

		return chunk;
	}

	public static PNGChunk createFCTL(int seqNumber, int width, int height, int offsetX, int offsetY, int delay) {
		PNGChunk chunk = new PNGChunk(ChunkTYPE.fcTL);
		int timescale = 1000; // 1 Millisecond;
		byte[] buf = new byte[chunk.getLength()]; // 25
		buf[0] = (byte) (seqNumber >>> 24 & 0xff);
		buf[1] = (byte) (seqNumber >>> 16 & 0xff);
		buf[2] = (byte) (seqNumber >>> 8 & 0xff);
		buf[3] = (byte) (seqNumber >>> 0 & 0xff);
		buf[4] = (byte) (width >>> 24 & 0xff);
		buf[5] = (byte) (width >>> 16 & 0xff);
		buf[6] = (byte) (width >>> 8 & 0xff);
		buf[7] = (byte) (width >>> 0 & 0xff);
		buf[8] = (byte) (height >>> 24 & 0xff);
		buf[9] = (byte) (height >>> 16 & 0xff);
		buf[10] = (byte) (height >>> 8 & 0xff);
		buf[11] = (byte) (height >>> 0 & 0xff);
		buf[12] = (byte) (offsetX >>> 24 & 0xff);
		buf[13] = (byte) (offsetX >>> 16 & 0xff);
		buf[14] = (byte) (offsetX >>> 8 & 0xff);
		buf[15] = (byte) (offsetX >>> 0 & 0xff);
		buf[16] = (byte) (offsetY >>> 24 & 0xff);
		buf[17] = (byte) (offsetY >>> 16 & 0xff);
		buf[18] = (byte) (offsetY >>> 8 & 0xff);
		buf[19] = (byte) (offsetY >>> 0 & 0xff);

		buf[20] = (byte) (delay >>> 8 & 0xff);
		buf[21] = (byte) (delay >>> 0 & 0xff);
		buf[22] = (byte) (timescale >>> 8 & 0xff);
		buf[23] = (byte) (timescale >>> 0 & 0xff);
		buf[24] = 0; // APNG_DISPOSE_OP_NONE
		buf[25] = 0; // APNG_BLEND_OP_SOURCE
		chunk.setBuffer(buf);

		return chunk;
	}

	public static PNGChunk createFDAT(int seqNumber, Image img) {
		return createFDAT(seqNumber, img, true);
	}

	public static PNGChunk createFDAT(int seqNumber, Image img, boolean withalpha) {
		PNGChunk data = new PNGChunk(ChunkTYPE.fdAT);
		byte[] buffer;
		if (withalpha) {
			buffer = createJavaFXImageToPNGFrameRGBA(img);
		} else {
			buffer = createJavaFXImageToPNGFrameRGB(img);
		}
		byte[] outbuffer = new byte[buffer.length];
		Deflater encoder = new Deflater();
		encoder.setInput(buffer);
		encoder.finish();
		int compresslength = encoder.deflate(outbuffer, 4, outbuffer.length - 4);

		outbuffer[0] = (byte) (seqNumber >>> 24 & 0xff);
		outbuffer[1] = (byte) (seqNumber >>> 16 & 0xff);
		outbuffer[2] = (byte) (seqNumber >>> 8 & 0xff);
		outbuffer[3] = (byte) (seqNumber >>> 0 & 0xff);
		data.setBuffer(outbuffer);
		data.setLength(compresslength + 4);
		return data;
	}

	public static PNGChunk[] createFDATs(int seqNumber, Image img, int buffersize, boolean withalpha) {
		byte[] buffer;
		List<PNGChunk> list = new ArrayList<PNGChunk>();

		if (withalpha) {
			buffer = createJavaFXImageToPNGFrameRGBA(img);
		} else {
			buffer = createJavaFXImageToPNGFrameRGB(img);
		}

		Deflater encoder = new Deflater();
		encoder.setInput(buffer);
		encoder.finish();

		int compresslength;
		do {
			byte[] outbuffer = new byte[buffersize];
			PNGChunk data = new PNGChunk(ChunkTYPE.fdAT);
			compresslength = encoder.deflate(outbuffer, 4, outbuffer.length - 4);

			outbuffer[0] = (byte) (seqNumber >>> 24 & 0xff);
			outbuffer[1] = (byte) (seqNumber >>> 16 & 0xff);
			outbuffer[2] = (byte) (seqNumber >>> 8 & 0xff);
			outbuffer[3] = (byte) (seqNumber >>> 0 & 0xff);

			data.setBuffer(outbuffer);
			data.setLength(compresslength + 4);

			if (compresslength != 0) {
				list.add(data);
				seqNumber++;
			}
		} while (compresslength != 0);

		PNGChunk[] chunks = new PNGChunk[list.size()];
		list.toArray(chunks);

		return chunks;
	}

}
