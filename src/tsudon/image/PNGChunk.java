package tsudon.image;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class PNGChunk {
	private ChunkTYPE type;
	private int length;
	private boolean crcClucFlag = true;
	private long crc;
	private byte[] buffer = null;
	private CRC crcCulc = new CRC();

	public PNGChunk(ChunkTYPE chunkType) {
		this.setType(chunkType);
		switch (chunkType) {
//Must need
		case IHDR: // HEADER
			this.setLength(13); // always 13
			break;
		case PLTE: // Color pallet
			break;
		case IDAT: // Image Data
			break;
		case IEND: // END of FILE
			this.setLength(0); // always ZERO
			// 0xAE 0x42 0x60 0x82
			this.crc = crcCulc.createCRC(null, 0, this.type);
			crcClucFlag = false;
			break;
		// APNG Chunks
		case acTL: // Animation Control
			this.setLength(8); // always 8
			break;
		case fcTL: // Frame Control
			this.setLength(26); // always 26
			break;
		case fdAT: // Frame Data
			break;

		default:

			// must before PLTE and IDAT
//		cHRM,	
//		tRNS,
//		gAMA,	// Gamma scale
//		sRGB,	// sRPG
			// between PLTE and IDAT
//		iCCP,
//		bKGD,
			// before IDAT
//		pHYs,
//		hIST,
			// non constrains
//		tIME,	// modify time - only single chunk
			// Multiple chunk OK
//		sPLT,
//		tEXt,	// TEXT	 
//		iTXt,	// i18n TEXT
//		zTXt,	// Archived TEXT
		}
	}

	private void setType(ChunkTYPE chunkType) {
		this.type = chunkType;
	}

	public ChunkTYPE getType() {
		return type;
	}

	public void culcCRC() {
		this.crc = crcCulc.createCRC(this.getBuffer(), this.getLength(), this.type);
	}

	public long getCRC() {
		if (crcClucFlag) {
			this.crc = crcCulc.createCRC(this.getBuffer(), this.getLength(), this.type);
		}
		return crc;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		// cannot SET FIXED SIZE HEADER
		this.length = length;
	}

	public void setLength(int length, boolean flagWithBuffer) {
		this.length = length;
		if (flagWithBuffer) {
			this.buffer = new byte[length];
		}
	}

	public byte[] getBuffer() {
		return buffer;
	}

	public void setBuffer(byte[] buffer) {
		this.buffer = buffer;
	}

	public byte[] getChunkText() {
		return this.type.toString().getBytes();
	}

	public void writeChunk(OutputStream out) throws IOException {
		ByteBuffer buf = ByteBuffer.allocate(4);
		out.write(buf.putInt((int) getLength()).array());
		out.write(getChunkText());
		if (getLength() > 0) {
			out.write(getBuffer(), 0, (int) getLength());
		}
		buf = ByteBuffer.allocate(4);
		out.write(buf.putInt((int) getCRC()).array());
//		System.out.println(type.toString() + " " + getLength()   + " " +  Integer.toHexString( (int)this.getCRC()));
	}

}
